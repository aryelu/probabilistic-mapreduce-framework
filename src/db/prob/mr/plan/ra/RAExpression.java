package db.prob.mr.plan.ra;

public interface RAExpression {
	int getEstimatedResultSize(ResultSize size);
	String getName();
	String toLatex();
}
