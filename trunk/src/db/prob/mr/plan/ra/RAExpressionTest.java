package db.prob.mr.plan.ra;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import db.prob.mr.plan.ra.operators.Projection;


public class RAExpressionTest {

	@Test
	public void sizeOfSingleRelation() {
		int relationSize = 101;
		RAExpression rel = new Relation(relationSize, "emp");
		assertEquals(relationSize, rel.getEstimatedResultSize());
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
		assertEquals(relationSize / 2, proj.getEstimatedResultSize());
	}
	
}
