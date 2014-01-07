package db.prob;

import db.prob.datalog.query.Absyn.Query;
import db.prob.datalog.query.Absyn.Schema.Relation;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;
import db.prob.datalog.query.Absyn.operators.QueryJoin;
import db.prob.mr.plan.ra.RAExpression;
import db.prob.mr.plan.ra.operators.Join;
import db.prob.mr.plan.ra.operators.Projection;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * implements safe plan algorithm input: Query in datalog like notation output:
 * a Plan which is safe or null otherwise
 */
public class SafePlan {
    /**
     * Breaks simple query to join expressions
     *
     * @param query original query
     * @return Join tree
     */
    private static RAExpression getJoins(Query query) throws Exception {
        List<QueryJoin> queryJoinList = query.bodyGetAllJoin();
        if (!queryJoinList.isEmpty()) {
            QueryJoin queryJoin = queryJoinList.get(0);

            Set<Relation> removeLeftRelationSet = new HashSet<Relation>();
            Relation left_relation = queryJoin.getLeftRelation();
            removeLeftRelationSet.add(left_relation);
            Query query_without_left = query.project_on_relation_set(removeLeftRelationSet, "removed_" + left_relation.getName());

            Set<Relation> removeRightRelationSet = new HashSet<Relation>();
            Relation right_relation = queryJoin.getRightRelation();
            removeRightRelationSet.add(right_relation);
            Query query_without_right = query.project_on_relation_set(removeRightRelationSet, "removed_" + right_relation.getName());

            RAExpression ans = new Join(SafePlan.getJoins(query_without_right), SafePlan.getJoins(query_without_left), queryJoin.getLeftName(), queryJoin.getRightName());
            return ans;
        }
        // no joins left then select with what we have
        // need to be from the same relation
        Set<Relation> relationSet = query.getRelationSet();
        if (!relationSet.isEmpty() && relationSet.size() == 1) {
            Relation relation = (Relation) relationSet.toArray()[0];
            Set<String> head = query.getHeadToStringSet();
            RAExpression ans = new db.prob.mr.plan.ra.Relation(1, relation.getName());
            return ans;
        }
        throw new Exception("Can't figure out");
    }

    /**
     * Creates a plan in simple case where Attr(query) == Head(query)
     *
     * @param query
     * @return
     */
    private static RAExpression simple_query_to_plan(Query query) throws Exception {
        // turn body into joins
        Projection proj = new Projection(SafePlan.getJoins(query), query.getHeadToStringSet());
        return proj;
    }

    /**
     * Builds set of strings for all attributes for toString method
     *
     * @param relationAttributeSet Attribute set to be printed
     * @return String representation of the set
     */
    public static Set<String> SetToString(Set<RelationAttribute> relationAttributeSet) {
        Set<String> stringSet = new HashSet<String>();
        for (RelationAttribute relationAttribute : relationAttributeSet) {
            stringSet.add(relationAttribute.toString());
        }
        return stringSet;
    }

    /**
     * @param query Query to build safe plan for
     * @return Safe plan for query if exists
     * @throws Exception
     */
    public static RAExpression buildSafePlan(Query query) throws Exception {
        Set<RelationAttribute> head = query.getHead();
        Set<RelationAttribute> attr = query.getAttribues();
        Set<RelationAttribute> diff = new HashSet<RelationAttribute>(attr);
        diff.removeAll(head);

        if (head.equals(attr)) {
            // make plan from query as-is and return it
            return simple_query_to_plan(query);
        }
        for (RelationAttribute diff_attr : diff) {
            // create new query with diff_attr in it's head

            Query query_add_a = query.query_add_head(diff_attr);
            if (Query.isProjectionSafe(query_add_a, head)) {
                return new Projection(buildSafePlan(query_add_a), SetToString(head));
            }
        }
        // split query to into q1 join q2
        // s.t. every R1 in rels(q1), R2 in rels(q2): R1,R2 are separated
        RAExpression splited_queries = SafePlan.splitToSeparateJoin(query);
        return splited_queries;
    }

    /**
     * build constraint graph from q and find separation:
     * The nodes of G(q) are Rels(q) and the edges are all pairs (Ri,Rj) of connected relations,
     * i.e. q contains some join condition Ri.A = Rj.B with either Ri.A or Rj.B not in Head(q).
     * Find the connected components of G
     *
     * @param query Query to split
     * @return Join of q1,q2 s.t. they separate q
     */
    private static Join splitToSeparateJoin(Query query) throws Exception {
        // TODO check if the spilt partition query head
        UndirectedGraph<Relation, DefaultEdge> constraintGraph =
                new SimpleGraph<Relation, DefaultEdge>(DefaultEdge.class);

        for (Relation relation : query.getRelationSet()) {
            constraintGraph.addVertex(relation);
        }

        // add all connected components
        List<QueryJoin> queryJoinSet = query.bodyGetAllJoin();
        for (QueryJoin queryJoin : queryJoinSet) {
            RelationAttribute relationA = queryJoin.termLeft;
            RelationAttribute relationB = queryJoin.termRight;
            if (query.isConnected(relationA.getRelation(), relationB.getRelation())) {
                constraintGraph.addEdge(relationA.getRelation(), relationB.getRelation());
            }
        }
        ConnectivityInspector<Relation, DefaultEdge> connectivityInspector =
                new ConnectivityInspector<Relation, DefaultEdge>(constraintGraph);

        if (connectivityInspector.isGraphConnected()) {
            throw new Exception("Failed to split due to connected graph");
        }

        List<Set<Relation>> relationConnectedSet = connectivityInspector.connectedSets();
        if (relationConnectedSet.size() == 2) {
            // find all leq that has one node in first set and second in the second set

            Set<Relation> relationConnectedSet_left = relationConnectedSet.get(0);
            Query left_query = query.project_on_relation_set(relationConnectedSet_left, "l");

            Set<Relation> relationConnectedSet_right = relationConnectedSet.get(1);
            Query right_query = query.project_on_relation_set(relationConnectedSet_right, "r");

            List<QueryJoin> queryJoinList = query.bodyGetJoinsBetweenConnectedSet(relationConnectedSet_left, relationConnectedSet_right);

            // TODO deduct there is always one
            // select first
            QueryJoin queryJoin = queryJoinList.get(0);
            RelationAttribute left = queryJoin.termLeft;
            RelationAttribute right = queryJoin.termRight;

            return new Join(buildSafePlan(left_query), buildSafePlan(right_query), left.getName(), right.getName());
        }
        // TODO - handle more than two connected sets
        // build new Queries from this,
        // head, and body
        return null;
    }
}
