package db.prob;

import db.prob.datalog.query.Absyn.Schema.DatabaseSchema;
import db.prob.datalog.query.Absyn.Query;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;
import db.prob.datalog.query.Absyn.operators.Selection;
import db.prob.mr.plan.ra.RAExpression;
import db.prob.mr.plan.ra.operators.Projection;
import db.prob.mr.plan.ra.operators.StringExpression;

import java.util.HashSet;
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

    private static Selection get_relation(Set<Selection> R, String col_name) {
        for (Selection r : R) {
            if (r.get_attr().contains(col_name)) {
                return r;
            }
        }
        return null;
    }

    public static Set<String> SetToString(Set<RelationAttribute> relationAttributeSet){
        Set<String> stringSet= new HashSet<String>();
        for(RelationAttribute relationAttribute: relationAttributeSet){
            stringSet.add(relationAttribute.toString());
        }
        return stringSet;
    }
    public static RAExpression safeplan(Query q) {
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
        // TODO try to separate
        StringExpression se = new StringExpression("Separation not implemented");
        return se;
    }


}
