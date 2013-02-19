package db.prob.mr.plan.ra.operators;

import java.util.Set;

import db.prob.mr.plan.ra.RAExpression;
import db.prob.mr.plan.ra.RAOperator;

public class Projection extends RAOperator {
	RAExpression subexpression;
	Set<String> attrs;

	public Projection(RAExpression subexpression, Set<String> attrs) {
		super();
		this.subexpression = subexpression;
		this.attrs = attrs;
	}

	@Override
	public int getEstimatedResultSize() {
		return this.subexpression.getEstimatedResultSize() / 2;
	}

}
