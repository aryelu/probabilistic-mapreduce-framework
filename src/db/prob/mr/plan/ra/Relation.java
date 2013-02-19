package db.prob.mr.plan.ra;

public class Relation implements RAExpression {
	private int size;
	private String name;
	
	public Relation(int size, String name) {
		super();
		this.size = size;
		this.name = name;
	}

	@Override
	public int getEstimatedResultSize() {
		return this.size;
	}

	public String getName() {
		return name;
	}	 

}
