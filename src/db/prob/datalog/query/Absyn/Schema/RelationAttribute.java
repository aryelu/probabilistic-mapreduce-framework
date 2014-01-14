package db.prob.datalog.query.Absyn.Schema;

/**
 * Attribute for probability value.
 * Used only in probabilistic relations.
 */
public class RelationAttribute {
    protected Relation relation;
    protected String name;

    public RelationAttribute(Relation relation, String name) {
        this.relation = relation;
        this.name = name;
    }
    
    public Relation getRelation(){
        return relation;
    }
    
    public String getName(){
        return name;
    }
    
    public String toString(){
        return String.format("%s.%s", this.relation.toString(), this.name);
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((relation == null) ? 0 : relation.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RelationAttribute)) {
			return false;
		}
		RelationAttribute other = (RelationAttribute) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (relation == null) {
			if (other.relation != null) {
				return false;
			}
		} else if (!relation.equals(other.relation)) {
			return false;
		}
		return true;
	}
    
    
}
