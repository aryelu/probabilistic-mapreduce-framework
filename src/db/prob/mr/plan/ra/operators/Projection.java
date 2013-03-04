package db.prob.mr.plan.ra.operators;

import java.util.Set;

import db.prob.mr.plan.ra.RAExpression;
import db.prob.mr.plan.ra.RAOperator;
import db.prob.mr.plan.ra.ResultSize;

public class Projection extends RAOperator {
	RAExpression subexpression;
	Set<String> attrs;

	public Projection(RAExpression subexpression, Set<String> attrs) {
		super();
		this.subexpression = subexpression;
		this.attrs = attrs;
	}

	@Override
	public String toLatex() {
		return String.format("\\Pi_{%s}(%s)", attrs.toString().replace("[", "").replace("]", ""), subexpression.toLatex());
	}



	@Override
	public int getEstimatedResultSize(ResultSize size) {
		int returnVal = 0;
		switch (size) {
		case ESTIMATION:
			returnVal = this.subexpression.getEstimatedResultSize(size) / 2;
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
