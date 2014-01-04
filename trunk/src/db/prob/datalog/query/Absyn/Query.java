package db.prob.datalog.query.Absyn; // Java Package generated by the BNF Converter.

import db.prob.datalog.query.Absyn.Schema.DatabaseSchema;
import db.prob.datalog.query.Absyn.Schema.Relation;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;
import db.prob.datalog.query.Absyn.operators.Literal;
import db.prob.datalog.query.Absyn.operators.QueryJoin;
import db.prob.datalog.query.Absyn.operators.QuerySelection;
import db.prob.datalog.query.FunctionalDependency.FunctionalDependency;
import db.prob.datalog.query.FunctionalDependency.FunctionalDependencyGroup;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Query class
 */
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

        setAttributes();
        setAllRelations();
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

    /**
     * Loads attribute Set for later use
     */
    private void setAttributes() {
        for (Literal b : this.body) {
            this.attributes.addAll(b.getAttributes());
        }
    }

    /**
     * loads relation and probabilistic relations sets for later use
     */
    private void setAllRelations() {
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

    /**
     * Creates a new query with attr added to it's head
     *
     * @param attr attribute to be added
     * @return new query
     */
    public Query query_add_head(RelationAttribute attr) {
        Query new_query = new Query(this);
        new_query.name = this.name + "_" + attr;
        new_query.head.add(attr);
        return new_query;
    }

    /**
     * gets selection from query where relation participates
     * <p/>
     * assuming there is only one or less occurrences of it in query's body
     *
     * @param relation
     * @return the selection or null
     */
    public QuerySelection body_get_selection_by_relation(Relation relation) {
        for (Literal l : this.body) {
            if (l instanceof QuerySelection) {
                QuerySelection l_Query_selection = (QuerySelection) l;
                if (l_Query_selection.getRelation().equals(relation)) {
                    return l_Query_selection;
                }
            }
        }
        throw new NullPointerException("can't find Relation" + relation + "in query's body");
    }

    private List<Literal> get_literal_by_relation_set(Set<Relation> relationConnectedSet) {
        List<Literal> literalSet = new LinkedList<Literal>();
        for (Relation relation : relationConnectedSet) {
            literalSet.add(this.body_get_selection_by_relation(relation));
            literalSet.addAll(this.body_get_join_by_relation(relation));
        }
        return literalSet;
    }

    public Set<RelationAttribute> body_get_attribute_by_set(Set<Relation> relationSet) {
        Set<RelationAttribute> relationAttributeSet = new HashSet<RelationAttribute>();
        for (Relation relation : relationSet) {
            relationAttributeSet.addAll(this.body_get_selection_by_relation(relation).getAttributes());
        }
        return relationAttributeSet;
    }

    /**
     * @param req_set set of attributes which LiterlEQ should be in
     * @return list of all Join Operator where attribute appear
     */
    private List<QueryJoin> body_get_join_by_attribute_set(Set<RelationAttribute> req_set) {
        List<QueryJoin> leq_list = new LinkedList<QueryJoin>();
        for (Literal l : this.body) {
            if (l instanceof QueryJoin) {
                QueryJoin leq = (QueryJoin) l;
                if (req_set.containsAll(leq.getTerms())) {
                    leq_list.add(leq);
                }
            }
        }
        return leq_list;
    }

    /**
     * @param relation relation which joins were interested
     * @return all QueryJoin who's left or right part is attribute from this relation
     */
    private List<QueryJoin> body_get_join_by_relation(Relation relation) {
        List<QueryJoin> leq_list = new LinkedList<QueryJoin>();
        for (Literal l : this.body) {
            if (l instanceof QueryJoin) {
                QueryJoin leq = (QueryJoin) l;
                if (relation.get_attr().contains(leq.term_right_) &&
                        relation.get_attr().contains(leq.term_left_)) {
                    leq_list.add(leq);
                }
            }
        }
        return leq_list;
    }

    /**
     * @return list of all QueryJoin in body
     */
    public List<QueryJoin> body_get_all_join() {
        List<QueryJoin> leq_list = new LinkedList<QueryJoin>();
        for (Literal l : this.body) {
            if (l instanceof QueryJoin) {
                QueryJoin leq = (QueryJoin) l;
                leq_list.add(leq);
            }
        }
        return leq_list;
    }

    /**
     * tests if only one part of join is in head (definition from article)
     *
     * @param queryJoin join to be tested
     * @return if this join is connected under this query
     */
    private boolean test_connected(QueryJoin queryJoin) {
        // connection is how much of leq in head
        int connection = 0;
        if (this.head.contains(queryJoin.term_right_)) {
            connection++;
        }
        if (this.head.contains(queryJoin.term_left_)) {
            connection++;
        }
        return connection == 1;
    }

    /**
     * Tests if two relation are connected
     * <p/>
     * Rel_a, Rel_b in Rels(q) and if there's R.A=R.B and either is not in Head(q)
     * <p/>
     * assumes no literalEQ s.t. R1.a = R1.b
     * so they are always for different Relations
     * also assumes field names are different
     *
     * @param rel_a Relation to be tested
     * @param rel_b Relation to be tested
     * @return are they connected
     */
    public boolean isConnected(Relation rel_a, Relation rel_b) {
        Set<RelationAttribute> set_from_rels = new HashSet<RelationAttribute>();
        QuerySelection Ra = this.body_get_selection_by_relation(rel_a);
        Set<RelationAttribute> Ra_attr_set = Ra.getAttributes();
        set_from_rels.addAll(Ra_attr_set);

        QuerySelection Rb = this.body_get_selection_by_relation(rel_b);
        Set<RelationAttribute> Rb_attr_set = Rb.getAttributes();
        set_from_rels.addAll(Rb_attr_set);

        List<QueryJoin> bodyJoinLiteralList = this.body_get_join_by_attribute_set(set_from_rels);

        boolean is_connected = false;
        for (QueryJoin leq : bodyJoinLiteralList) {
            is_connected = this.test_connected(leq);
            // break fast
            if (is_connected) {
                return is_connected;
            }
        }
        return is_connected;
    }

    /**
     * tests a query with a new head to be safe or not
     * <p/>
     * Does this holds:
     * for every R^p in PRels(q)
     * Gamma := A1,...,An, R^p.E ==> head(q)
     *
     * @param query           query to be tested
     * @param projection_head new projection head
     * @return is the new query safe
     */
    public static boolean isProjectionSafe(Query query, Set<RelationAttribute> projection_head) {
        // create induced functional dependencies GAMMA^p(q) on Attr(q)
        DatabaseSchema schema = query.getSchema();
        Set<FunctionalDependency> fd_set = schema.getFdSet();
        // take all FDs from schema
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

    /**
     * Create Set of functional dependencies from query s.t.
     * - For every join predicate Ri.A=Rj.B, both Ri.A->Rj.B and Rj.B->Ri.A
     *
     * @return set of functional dependency
     */
    // TODO For every selection predicate Ri.A=c, {}->Ri.A
    private Set<FunctionalDependency> get_fd_set() {
        DatabaseSchema schema = this.schema;
        Set<FunctionalDependency> fd_set = new HashSet<FunctionalDependency>();

        // For every join predicate Ri.A=Rj.B, both Ri.A->Rj.B and Rj.B->Ri.A
        for (QueryJoin leq : this.body_get_all_join()) {
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

    public Set<RelationAttribute> getAttribues() {
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

    /**
     * Creates a new query with only relationConnectedSet involved
     *
     * @param relationConnectedSet set of relation which we want our query to hold
     * @param name                 new name for query
     * @return
     */
    public Query project_on_relation_set(Set<Relation> relationConnectedSet, String name) {
        DatabaseSchema databaseSchema = this.schema;
        String newQueryName = this.name + name;
        List<Literal> newQueryBody = this.get_literal_by_relation_set(relationConnectedSet);
        Set<RelationAttribute> newQueryHead = this.body_get_attribute_by_set(relationConnectedSet);
        Query newQuery = new Query(databaseSchema, newQueryName, newQueryHead, newQueryBody);
        return newQuery;
    }


}