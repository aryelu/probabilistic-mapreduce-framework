package db.prob;

import db.prob.datalog.query.Absyn.Query;
import db.prob.datalog.query.Absyn.Schema.Relation;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;
import db.prob.datalog.query.Absyn.operators.QueryJoin;
import db.prob.datalog.query.Absyn.operators.QuerySelection;
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
    /*
     * return any plan for them
     */
    private static RAExpression simple_query_to_plan(Query q) {
        return new StringExpression("Simple query");
    }

    private static QuerySelection get_relation(Set<QuerySelection> R, String col_name) {
        for (QuerySelection r : R) {
            if (r.get_attr().contains(col_name)) {
                return r;
            }
        }
        return null;
    }

    public static Set<String> SetToString(Set<RelationAttribute> relationAttributeSet) {
        Set<String> stringSet = new HashSet<String>();
        for (RelationAttribute relationAttribute : relationAttributeSet) {
            stringSet.add(relationAttribute.toString());
        }
        return stringSet;
    }

    public static RAExpression safeplan(Query q) throws Exception {
        Set<RelationAttribute> head = q.getHead();
        Set<RelationAttribute> attr = q.get_attr();
        Set<RelationAttribute> diff = new HashSet<RelationAttribute>(head);
        diff.removeAll(attr);

        if (head.equals(attr)) {
            // make plan from query as-is and return it
            return simple_query_to_plan(q);
        }
        for (RelationAttribute diff_attr : diff) {
            // create new query with diff_attr in it's head
            Query query_add_a = q.query_add_head(diff_attr);
            if (Query.is_projection_safe(query_add_a, q.getHead())) {

                return new Projection(safeplan(query_add_a), SetToString(head));
            }
        }
        // TODO separate
        // split q to into q1 join q2
        // s.t. every R1 in rels(q1), R2 in rels(q2): R1,R2 are separated
        Join splited_queries = SafePlan.split_to_separate_join(q);
        return splited_queries;
    }

    /**
     * build constraint graph from q and find separation:
     * The nodes of G(q) are Rels(q) and the edges are all pairs (Ri,Rj) of connected relations,
     * i.e. q contains some join condition Ri.A = Rj.B with either Ri.A or Rj.B not in Head(q).
     * Find the connected components of G
     * @param q
     * @return Join of q1,q2 s.t. they separate q
     */
    private static Join split_to_separate_join(Query q) throws Exception {
        UndirectedGraph<Relation, DefaultEdge> constraint_graph =
                new SimpleGraph<Relation, DefaultEdge>(DefaultEdge.class);

        for (Relation relation : q.getRelationSet()) {
            constraint_graph.addVertex(relation);
        }

        // add all connected components
        List<QueryJoin> queryJoinSet = q.body_get_literaleq();
        for (QueryJoin queryJoin: queryJoinSet){
            RelationAttribute relationA = queryJoin.term_left_;
            RelationAttribute relationB = queryJoin.term_right_;
            if (q.is_connected(relationA.getName(), relationB.getName())){
                constraint_graph.addEdge(relationA.getRelation(), relationB.getRelation());
            }
        }
        ConnectivityInspector<Relation, DefaultEdge> connectivityInspector =
                new ConnectivityInspector<Relation, DefaultEdge>(constraint_graph);

        if (connectivityInspector.isGraphConnected()){
            throw new Exception("Failed to split due to connected graph");
        }

        List<Set<Relation>> relationConnectedSet = connectivityInspector.connectedSets();
        if (relationConnectedSet.size() == 2){
            Set<RelationAttribute> query_head = q.getHead();

            Set<Relation> relationConnectedSet_left = relationConnectedSet.get(0);
            Set<RelationAttribute> left_query_attr = Relation.set_to_attr(relationConnectedSet_left);
            left_query_attr.removeAll(query_head);

            Query left_query = new Query(q.getSchema(),q.getName()+"l", left_query_attr, q.getBody());

            Set<Relation> relationConnectedSet_right = relationConnectedSet.get(1);
            Set<RelationAttribute> right_query_attr = Relation.set_to_attr(relationConnectedSet_right);
            right_query_attr.removeAll(query_head);

            Query right_query = new Query(q.getSchema(),q.getName()+"r", right_query_attr, q.getBody());
            // TODO add string to Join
            return new Join(safeplan(left_query), safeplan(right_query), "","");
        }
        // build new Queries from this,
        // head, and body
        return null;
    }
}
