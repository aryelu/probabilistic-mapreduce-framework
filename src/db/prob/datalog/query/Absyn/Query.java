package db.prob.datalog.query.Absyn; // Java Package generated by the BNF Converter.

import db.prob.datalog.query.Absyn.Schema.DatabaseSchema;
import db.prob.datalog.query.Absyn.Schema.Relation;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;
import db.prob.datalog.query.Absyn.operators.Join;
import db.prob.datalog.query.Absyn.operators.Literal;
import db.prob.datalog.query.Absyn.operators.Selection;
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

    public Query(DatabaseSchema schema, String name, Set<RelationAttribute> head, List<? extends Literal> body) {
        this.schema = schema;
        this.name = name;
        this.body = body;
        this.head = head;

        set_attr();
        set_allrels();
    }

    public Query(Query q) {
        name = q.name;
        head = new HashSet<RelationAttribute>(q.head);
        body = new LinkedList<Literal>(q.body);
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
            if (literal instanceof Selection) {
                Selection selection = (Selection) literal;
                Relation relation = selection.getRelation();
                if (relation.is_probabilistic()) {
                    this.probabilisticRelationSet.add(relation);
                } else {
                    this.relationSet.add(relation);
                }

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

    public boolean is_separete(String Rel_a, String Rel_b) {
        return !is_connected(Rel_a, Rel_b);
    }

    private Selection body_get_relation(String relation_name) {
        for (Literal l : this.body) {
            if (l instanceof Selection) {
                Selection l_selection = (Selection) l;
                if (l_selection.getRelation().getName().equals(relation_name)) {
                    return l_selection;
                }
            }
        }
        return null;
    }

    /*
        select_set : set of attributes which LiterlEQ should be in
     */
    private List<Join> body_get_literaleq_by_set(Set<RelationAttribute> req_set) {
        List<Join> leq_list = new LinkedList<Join>();
        for (Literal l : this.body) {
            if (l instanceof Join) {
                Join leq = (Join) l;
                if (req_set.containsAll(leq.get_terms())) {
                    leq_list.add(leq);
                }
            }
        }
        return leq_list;
    }

    /*
        get all literalEQ
     */
    public List<Join> body_get_literaleq() {
        List<Join> leq_list = new LinkedList<Join>();
        for (Literal l : this.body) {
            if (l instanceof Join) {
                Join leq = (Join) l;
                leq_list.add(leq);
            }
        }
        return leq_list;
    }

    /*
        tests if only one part of leq is in head
     */
    private boolean test_connected(Join leq) {
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
    public boolean is_connected(String rel_a, String rel_b) {
        // Rel_a, Rel_b in Rels(q)
        // if there's R.A=R.B and either is not in Head(q)
        Set<RelationAttribute> set_from_rels = new HashSet<RelationAttribute>();
        Selection Ra = this.body_get_relation(rel_a);
        Set<RelationAttribute> Ra_attr_set = Ra.get_attr();
        set_from_rels.addAll(Ra_attr_set);

        Selection Rb = this.body_get_relation(rel_b);
        Set<RelationAttribute> Rb_attr_set = Rb.get_attr();
        set_from_rels.addAll(Rb_attr_set);

        boolean is_connected = false;
        List<Join> leq_body = this.body_get_literaleq_by_set(set_from_rels);
        for (Join leq : leq_body) {
            is_connected = this.test_connected(leq);
            if (is_connected) {
                return is_connected;
            }
        }
        return is_connected;
    }

    public boolean is_separate(String Ra, String Rb) {
        return !this.is_connected(Ra, Rb);
    }

    public static boolean is_projection_safe(Query query, Set<RelationAttribute> projection_head) {
        // TODO
        // for every R^p in PRels(q)
        // Gamma := A1,...,An, R^p.E ==> head(q)

        // create induced functional dependencies GAMMA^p(q) on Attr(q)
        // take all FDs from schema
        DatabaseSchema schema = query.get_schema();
        Set<FunctionalDependency> fd_set = schema.get_fd_set();
        fd_set.addAll(query.get_fd_set());

        Set<Relation> prel_set = query.probabilisticRelationSet;
        boolean is_safe = true;
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
        for (Join leq : this.body_get_literaleq()) {
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

    public DatabaseSchema get_schema() {
        return schema;
    }

    public Set<RelationAttribute> getHead() {
        return head;
    }
}