package db.prob.mr.plan.ra.operators;

import db.prob.mr.plan.ra.RAExpression;
import db.prob.mr.plan.ra.RAOperator;

public class Selection extends RAOperator {
	RAExpression subexpression;
	String constraint;
	
	public Selection(RAExpression subexpression, String constraint) {
		super();
		super.operatorName = "select";
		this.subexpression = subexpression;
		this.constraint = constraint;
	}

	@Override
	public int getEstimatedResultSize() {
		return this.subexpression.getEstimatedResultSize() / 3;
	}
	
	
}
