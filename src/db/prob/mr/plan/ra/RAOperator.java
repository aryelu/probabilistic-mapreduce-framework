package db.prob.mr.plan.ra;


public abstract class RAOperator implements RAExpression {
	protected String operatorName;

	@Override
	public String getName() {
		return this.operatorName;
	}
	
	
}
