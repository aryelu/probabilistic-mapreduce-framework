package db.prob.datalog.query.Absyn.operators; // Java Package generated by the BNF Converter.

import java.util.HashSet;
import java.util.Set;

import db.prob.datalog.query.Absyn.Schema.Relation;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;

public class QuerySelection implements Literal {
    protected Relation relation;
    protected Set<RelationAttribute> attributes = new HashSet<RelationAttribute>();

    /**
     * 
     * @param relation
     * @param attributes
     */
    public QuerySelection(Relation relation, Set<String> attributes) {
        this.relation = relation;
        for (String attrName : attributes) {
            try {
                this.attributes.add(relation.getAttrByName(attrName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Set<RelationAttribute> getAttributes() {
        return attributes;
    }

    public String toString() {
        return "Select " + this.attributes + " from " + this.relation.getName();
    }

    public Relation getRelation() {
        return this.relation;
    }

}