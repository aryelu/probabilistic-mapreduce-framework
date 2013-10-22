package db.prob.datalog.query.Absyn.Schema;

/**
 * attribute fields for relations
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
}
