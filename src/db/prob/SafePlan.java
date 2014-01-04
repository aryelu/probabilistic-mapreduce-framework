package db.prob;

import db.prob.datalog.query.Absyn.Query;
import db.prob.datalog.query.Absyn.Schema.Relation;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;
import db.prob.datalog.query.Absyn.operators.QueryJoin;
import db.prob.mr.plan.ra.RAExpression;
import db.prob.mr.plan.ra.operators.Join;
import db.prob.mr.plan.ra.operators.Projection;
import db.prob.mr.plan.ra.operators.StringExpression;
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
     * Creates a plan in simple case where Attr(query) == Head(query)
     *
     * @param query
     * @return
     */
    private static RAExpression simple_query_to_plan(Query query) {
        return new StringExpression("SQ: head:(" + query.getHead() + ") (body:" + query.getBody() + ")");
    }

    /**
     * Builds set of strings for all attributes for toString method
     *
     * @param relationAttributeSet
     * @return
     */
    public static Set<String> SetToString(Set<RelationAttribute> relationAttributeSet) {
        Set<String> stringSet = new HashSet<String>();
        for (RelationAttribute relationAttribute : relationAttributeSet) {
            stringSet.add(relationAttribute.toString());
        }
        return stringSet;
    }

    /**
     * @param query
     * @return
     * @throws Exception
     */
    public static RAExpression safeplan(Query query) throws Exception {
        Set<RelationAttribute> head = query.getHead();
        Set<RelationAttribute> attr = query.getAttribues();
        Set<RelationAttribute> diff = new HashSet<RelationAttribute>(head);
        diff.removeAll(attr);

        if (head.equals(attr)) {
            // make plan from query as-is and return it
            return simple_query_to_plan(query);
        }
        for (RelationAttribute diff_attr : diff) {
            // create new query with diff_attr in it's head
            Query query_add_a = query.query_add_head(diff_attr);
            if (Query.isProjectionSafe(query_add_a, query.getHead())) {

                return new Projection(safeplan(query_add_a), SetToString(head));
            }
        }
        // TODO separate
        // split query to into q1 join q2
        // s.t. every R1 in rels(q1), R2 in rels(q2): R1,R2 are separated
        Join splited_queries = SafePlan.split_to_separate_join(query);
        return splited_queries;
    }

    /**
     * build constraint graph from q and find separation:
     * The nodes of G(q) are Rels(q) and the edges are all pairs (Ri,Rj) of connected relations,
     * i.e. q contains some join condition Ri.A = Rj.B with either Ri.A or Rj.B not in Head(q).
     * Find the connected components of G
     *
     * @param query
     * @return Join of q1,q2 s.t. they separate q
     */
    private static Join split_to_separate_join(Query query) throws Exception {
        UndirectedGraph<Relation, DefaultEdge> constraint_graph =
                new SimpleGraph<Relation, DefaultEdge>(DefaultEdge.class);

        for (Relation relation : query.getRelationSet()) {
            constraint_graph.addVertex(relation);
        }

        // add all connected components
        List<QueryJoin> queryJoinSet = query.body_get_all_join();
        for (QueryJoin queryJoin : queryJoinSet) {
            RelationAttribute relationA = queryJoin.term_left_;
            RelationAttribute relationB = queryJoin.term_right_;
            if (query.isConnected(relationA.getRelation(), relationB.getRelation())) {
                constraint_graph.addEdge(relationA.getRelation(), relationB.getRelation());
            }
        }
        ConnectivityInspector<Relation, DefaultEdge> connectivityInspector =
                new ConnectivityInspector<Relation, DefaultEdge>(constraint_graph);

        if (connectivityInspector.isGraphConnected()) {
            throw new Exception("Failed to split due to connected graph");
        }

        List<Set<Relation>> relationConnectedSet = connectivityInspector.connectedSets();
        if (relationConnectedSet.size() == 2) {
            // find leq and remove it
            Set<Relation> relationConnectedSet_left = relationConnectedSet.get(0);
            Query left_query = query.project_on_relation_set(relationConnectedSet_left, "l");
            Set<Relation> relationConnectedSet_right = relationConnectedSet.get(1);
            Query right_query = query.project_on_relation_set(relationConnectedSet_right, "r");

            // TODO add string to Join
            return new Join(safeplan(left_query), safeplan(right_query), "", "");
        }
        // build new Queries from this,
        // head, and body
        return null;
    }
}
