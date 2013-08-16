package db.prob;

import db.prob.datalog.query.Absyn.LiteralEQ;
import db.prob.datalog.query.Absyn.Query;
import db.prob.datalog.query.Absyn.Relation;
import db.prob.mr.plan.ra.RAExpression;
import db.prob.mr.plan.ra.operators.Projection;
import db.prob.mr.plan.ra.operators.Selection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 * implements safe plan algorithm input: Query in datalog like notation output:
 * a Plan which is safe or null otherwise
 */
public class SafePlan {
	/*
	 * return any plan for them
	 */
	private static RAExpression simple_query_to_plan(Query q) {
		return null;
	}

	private static Relation get_relation(Set<Relation> R, String col_name) {
		for (Relation r : R) {
			if (r.get_attr().contains(col_name)) {
				return r;
			}
		}
		return null;
	}

	public static RAExpression safeplan(Query q) {
		Set<String> head = q.head();
		Set<String> attr = q.attr();
		Set<String> diff = new HashSet<String>(head);
		diff.removeAll(head);

		if (head.equals(attr)) {
			// TODO
			// make plan from query as-is
			// and return it
			RAExpression ra = simple_query_to_plan(q);
		}
		for (String a : diff) {
			// TODO
			Query query_add_a = new Query(q);
			head = query_add_a.head();
			head.add(a);
			query_add_a.load_attr();
			query_add_a.is_projection_safe();

			// add a to head,
			// test if safeplan
		}
		return null;
	}
}
