package db.prob.mr.plan.ra;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import db.prob.mr.plan.ra.operators.Join;
import db.prob.mr.plan.ra.operators.Projection;
import db.prob.mr.plan.ra.operators.Selection;


public class RAExpressionTest {

	@Test
	public void sizeOfSingleRelation() {
		int relationSize = 101;
		RAExpression rel = new Relation(relationSize, "emp");
		System.out.println(rel.toLatex());
		assertEquals(relationSize, rel.getEstimatedResultSize(ResultSize.ESTIMATION));
		assertEquals(relationSize, rel.getEstimatedResultSize(ResultSize.UPPER_BOUND));
	}

	@Test
	public void sizeOfProjection() {
		int relationSize = 101;
		RAExpression rel = new Relation(relationSize, "emp");
		@SuppressWarnings("serial")
		Set<String> attrs = new HashSet<String>() {{
			add("rid");
			add("did");
		}};
		RAExpression proj = new Projection(rel,attrs);
		System.out.println(proj.toLatex());
		assertEquals(relationSize / 2, proj.getEstimatedResultSize(ResultSize.ESTIMATION));
		assertEquals(relationSize, proj.getEstimatedResultSize(ResultSize.UPPER_BOUND));
	}
	
	@Test
	public void sizeOfSelection() {
		int relationSize = 101;
		RAExpression rel = new Relation(relationSize, "emp");
		RAExpression sel = new Selection(rel, "dept=1");
		System.out.println(sel.toLatex());
		assertEquals(relationSize / 3, sel.getEstimatedResultSize(ResultSize.ESTIMATION));
		assertEquals(relationSize, sel.getEstimatedResultSize(ResultSize.UPPER_BOUND));
	}
	
	@Test
	public void sizeOfJoin() {
		int empSize = 101;
		int deptSize = 20;
		RAExpression emp  = new Relation(empSize,  "emp");
		RAExpression dept = new Relation(deptSize, "dept");
		
		RAExpression join = new Join(emp, dept, "did", "did");
		System.out.println(join.toLatex());
		assertEquals(Math.max(empSize, deptSize), join.getEstimatedResultSize(ResultSize.ESTIMATION));
		assertEquals(empSize * deptSize, join.getEstimatedResultSize(ResultSize.UPPER_BOUND));
	}
}
