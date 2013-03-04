package db.prob.mr.plan.ra;

public class Relation implements RAExpression {
	private int size;
	private String name;
	
	public Relation(int size, String name) {
		super();
		this.size = size;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toLatex() {
		return name;
	}

	@Override
	public int getEstimatedResultSize(ResultSize size) {
		return this.size;
	}	 

}
