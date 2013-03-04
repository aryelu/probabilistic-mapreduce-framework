package db.prob.mr.plan.ra.operators;

import db.prob.mr.plan.ra.RAExpression;
import db.prob.mr.plan.ra.RAOperator;
import db.prob.mr.plan.ra.ResultSize;

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
	public String toLatex() {
		return String.format("\\sigma_{%s}(%s)", constraint, subexpression.toLatex());
	}

	@Override
	public int getEstimatedResultSize(ResultSize size) {
		int returnVal = 0;
		switch (size) {
		case ESTIMATION:
			returnVal = this.subexpression.getEstimatedResultSize(size) / 3;
			break;
		case UPPER_BOUND:
			returnVal = this.subexpression.getEstimatedResultSize(size);
			break;
		default:
			break;
		}
		return returnVal;
	}
	
	
}
