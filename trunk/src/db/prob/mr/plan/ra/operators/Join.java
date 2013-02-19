package db.prob.mr.plan.ra.operators;

import db.prob.mr.plan.ra.RAExpression;
import db.prob.mr.plan.ra.RAOperator;

public class Join extends RAOperator {
	RAExpression leftSubexpr;
	RAExpression rightSubexpr;
	String leftAttr;
	String rightArrt;
	
	
	
	public Join(RAExpression leftSubexpr, RAExpression rightSubexpr, String leftAttr, String rightArrt) {
		super();
		super.operatorName = "join";
		this.leftSubexpr = leftSubexpr;
		this.rightSubexpr = rightSubexpr;
		this.leftAttr = leftAttr;
		this.rightArrt = rightArrt;
	}



	@Override
	public int getEstimatedResultSize() {
		return Math.max(leftSubexpr.getEstimatedResultSize(), rightSubexpr.getEstimatedResultSize());
	}
	
}
