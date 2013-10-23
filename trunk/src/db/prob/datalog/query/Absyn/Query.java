package db.prob.datalog.query.Absyn; // Java Package generated by the BNF Converter.

import db.prob.datalog.query.Absyn.Schema.DatabaseSchema;
import db.prob.datalog.query.Absyn.Schema.Relation;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;
import db.prob.datalog.query.Absyn.operators.QueryJoin;
import db.prob.datalog.query.Absyn.operators.Literal;
import db.prob.datalog.query.Absyn.operators.QuerySelection;
import db.prob.datalog.query.FunctionalDependency.FunctionalDependency;
import db.prob.datalog.query.FunctionalDependency.FunctionalDependencyGroup;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Query {
    private DatabaseSchema schema;
    // name of query
    private String name;

    // query's head
    private Set<RelationAttribute> head;

    // query's body, includes list of joins and selections
    private List<? extends Literal> body;

    // set of all relations involved in this query
    private Set<Relation> relationSet = new HashSet<Relation>();

    // set of all probabilistic relations involved in this query
    private Set<Relation> probabilisticRelationSet = new HashSet<Relation>();

    // set of all attributes appearing in this query
    private Set<RelationAttribute> attributes = new HashSet<RelationAttribute>();
    private Object _body;

    public Query(DatabaseSchema schema, String name, Set<RelationAttribute> head, List<? extends Literal> body) {
        this.schema = schema;
        this.name = name;
        this.body = body;
        this.head = head;

        set_attr();
        set_allrels();
    }

    public Query(Query q) {
        schema = q.schema;
        name = q.name;
        head = new HashSet<RelationAttribute>(q.head);
        body = new LinkedList<Literal>(q.body);
    }

    public Set<Relation> getRelationSet() {
        return relationSet;
    }

    /*
        loads attribute Set for later use
     */
    private void set_attr() {
        for (Literal b : this.body) {
            this.attributes.addAll(b.get_attr());
        }
    }

    /*
        loads relation and probabilistic relations sets for later use
     */
    private void set_allrels() {
        for (Literal literal : this.body) {
            if (literal instanceof QuerySelection) {
                QuerySelection querySelection = (QuerySelection) literal;
                Relation relation = querySelection.getRelation();
                if (relation.is_probabilistic()) {
                    this.probabilisticRelationSet.add(relation);
                }
                this.relationSet.add(relation);
            }
        }
    }

    /*
        creates a new query with attr in it head
     */
    public Query query_add_head(RelationAttribute attr) {
        Query new_query = new Query(this);
        new_query.name = this.name + "_" + attr;
        new_query.head.add(attr);
        return new_query;
    }

    public QuerySelection body_get_selection(Relation relation) {
        for (Literal l : this.body) {
            if (l instanceof QuerySelection) {
                QuerySelection l_Query_selection = (QuerySelection) l;
                if (l_Query_selection.getRelation().equals(relation)) {
                    return l_Query_selection;
                }
            }
        }
        return null;
    }
    private List<Literal> get_literal_by_relation_set(Set<Relation> relationConnectedSet) {
        List<Literal> literalSet = new LinkedList<Literal>();
        for (Relation relation:relationConnectedSet){
            literalSet.add(this.body_get_selection(relation));
            literalSet.addAll(this.body_get_literaleq_by_relation(relation));
        }
        return literalSet;
    }

    public Set<RelationAttribute> body_get_attribute_by_set(Set<Relation> relationSet){
        Set<RelationAttribute> relationAttributeSet = new HashSet<RelationAttribute>();
        for (Relation relation:relationSet){
            relationAttributeSet.addAll(this.body_get_selection(relation).get_attr());
        }
        return relationAttributeSet;
    }


    /*
        select_set : set of attributes which LiterlEQ should be in
     */
    private List<QueryJoin> body_get_literaleq_by_set(Set<RelationAttribute> req_set) {
        List<QueryJoin> leq_list = new LinkedList<QueryJoin>();
        for (Literal l : this.body) {
            if (l instanceof QueryJoin) {
                QueryJoin leq = (QueryJoin) l;
                if (req_set.containsAll(leq.get_terms())) {
                    leq_list.add(leq);
                }
            }
        }
        return leq_list;
    }
    private List<QueryJoin> body_get_literaleq_by_relation(Relation relation) {
        List<QueryJoin> leq_list = new LinkedList<QueryJoin>();
        for (Literal l : this.body) {
            if (l instanceof QueryJoin) {
                QueryJoin leq = (QueryJoin) l;
                if (relation.get_attr().contains(leq.term_right_) &&
                        relation.get_attr().contains(leq.term_left_)){
                    leq_list.add(leq);
                }
            }
        }
        return leq_list;
    }

    /*
        get all literalEQ
     */
    public List<QueryJoin> body_get_literaleq() {
        List<QueryJoin> leq_list = new LinkedList<QueryJoin>();
        for (Literal l : this.body) {
            if (l instanceof QueryJoin) {
                QueryJoin leq = (QueryJoin) l;
                leq_list.add(leq);
            }
        }
        return leq_list;
    }

    /*
        tests if only one part of leq is in head
     */
    private boolean test_connected(QueryJoin leq) {
        // connection is how much of leq in head
        int connection = 0;
        if (this.head.contains(leq.term_right_)) {
            connection++;
        }
        if (this.head.contains(leq.term_left_)) {
            connection++;
        }
        return connection == 1;
    }

    /*
        assumes no literalEQ s.t. R1.a = R1.b
        so they are always for different Relations

        also assumes field names are different
     */
    public boolean is_connected(Relation rel_a, Relation rel_b) {
        //TODO test if works
        // Rel_a, Rel_b in Rels(q)
        // if there's R.A=R.B and either is not in Head(q)
        Set<RelationAttribute> set_from_rels = new HashSet<RelationAttribute>();
        QuerySelection Ra = this.body_get_selection(rel_a);
        Set<RelationAttribute> Ra_attr_set = Ra.get_attr();
        set_from_rels.addAll(Ra_attr_set);

        QuerySelection Rb = this.body_get_selection(rel_b);
        Set<RelationAttribute> Rb_attr_set = Rb.get_attr();
        set_from_rels.addAll(Rb_attr_set);

        boolean is_connected = false;
        List<QueryJoin> leq_body = this.body_get_literaleq_by_set(set_from_rels);

        for (QueryJoin leq : leq_body) {
            is_connected = this.test_connected(leq);
            // break fast
            if (is_connected) {
                return is_connected;
            }
        }
        return is_connected;
    }

    public static boolean is_projection_safe(Query query, Set<RelationAttribute> projection_head) {
        // TODO
        // for every R^p in PRels(q)
        // Gamma := A1,...,An, R^p.E ==> head(q)

        // create induced functional dependencies GAMMA^p(q) on Attr(q)
        // take all FDs from schema
        DatabaseSchema schema = query.getSchema();
        Set<FunctionalDependency> fd_set = schema.get_fd_set();
        fd_set.addAll(query.get_fd_set());

        Set<Relation> prel_set = query.probabilisticRelationSet;
        boolean is_safe = false;
        if (!prel_set.isEmpty()) {
            for (Relation prel : prel_set) {
                if (is_safe) {
                    Set<RelationAttribute> query_head_and_probabilistic = new HashSet<RelationAttribute>();
                    query_head_and_probabilistic.addAll(projection_head);
                    RelationAttribute probabilistic_attribute = prel.getProbabilistic_attribute();
                    query_head_and_probabilistic.add(probabilistic_attribute);

                    FunctionalDependencyGroup fdg = new FunctionalDependencyGroup(schema, fd_set);
                    FunctionalDependency fd_to_find = new FunctionalDependency(schema, query_head_and_probabilistic, query.head);
                    is_safe = fdg.statisfy(fd_to_find);
                } else {
                    break;
                }

            }
        }
        return is_safe;
    }

    /*
        For every join predicate Ri.A=Rj.B, both Ri.A->Rj.B and Rj.B->Ri.A
        TODO For every selection predicate Ri.A=c, {}->Ri.A
     */
    private Set<FunctionalDependency> get_fd_set() {
        DatabaseSchema schema = this.schema;
        Set<FunctionalDependency> fd_set = new HashSet<FunctionalDependency>();

        // For every join predicate Ri.A=Rj.B, both Ri.A->Rj.B and Rj.B->Ri.A
        for (QueryJoin leq : this.body_get_literaleq()) {
            RelationAttribute left = leq.term_left_;
            Set<RelationAttribute> left_set = new HashSet<RelationAttribute>();
            left_set.add(left);

            RelationAttribute right = leq.term_right_;
            Set<RelationAttribute> right_set = new HashSet<RelationAttribute>();
            right_set.add(right);

            fd_set.add(new FunctionalDependency(schema, left_set, right_set));
            fd_set.add(new FunctionalDependency(schema, right_set, left_set));
        }
        return fd_set;
    }

    public Set<RelationAttribute> get_attr() {
        return this.attributes;
    }

    public DatabaseSchema getSchema() {
        return schema;
    }

    public Set<RelationAttribute> getHead() {
        return head;
    }

    public List<? extends Literal> getBody() {
        return body;
    }

    public String getName() {
        return name;
    }

    public Query project_on_relation_set(Set<Relation> relationConnectedSet,String name ) {
        DatabaseSchema schema_ = this.schema;
        String name_ = this.name + name;
        List<Literal> body_ = this.get_literal_by_relation_set(relationConnectedSet);
        Set<RelationAttribute> head_ = this.body_get_attribute_by_set(relationConnectedSet);
        Query query_new = new Query(schema_, name_,head_, body_ );
        return query_new;
    }


}