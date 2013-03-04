package db.prob.mr.plan.ra.operators;

import db.prob.mr.plan.ra.RAExpression;
import db.prob.mr.plan.ra.RAOperator;
import db.prob.mr.plan.ra.ResultSize;

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
	public String toLatex() {
		return String.format("%s \\bowtie_{%s=%s} %s", 
				this.leftSubexpr.toLatex(),
				this.leftAttr,
				this.rightArrt,
				this.rightSubexpr.toLatex());
	}

	@Override
	public int getEstimatedResultSize(ResultSize size) {
		int returnVal = 0;
		switch (size) {
		case ESTIMATION:
			returnVal = Math.max(leftSubexpr.getEstimatedResultSize(size), rightSubexpr.getEstimatedResultSize(size));
			break;
		case UPPER_BOUND:
			returnVal = leftSubexpr.getEstimatedResultSize(size) * rightSubexpr.getEstimatedResultSize(size);
			break;
		default:
			break;
		}
		return returnVal;
	}
	
}
