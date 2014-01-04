package db.prob.datalog.query.Absyn.operators; // Java Package generated by the BNF Converter.

import db.prob.datalog.query.Absyn.Schema.Relation;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;

import java.util.HashSet;
import java.util.Set;

public class QuerySelection implements Literal {
    protected Relation relation;
    protected Set<RelationAttribute> attribute_set = new HashSet<RelationAttribute>();

    public QuerySelection(Relation relation, Set<String> attribute_set) {
        this.relation = relation;
        for (String attr_name : attribute_set) {
            try {
                this.attribute_set.add(relation.getAttrByName(attr_name));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Set<RelationAttribute> getAttributes() {
        return attribute_set;
    }

    public String toString() {
        return "Select " + this.attribute_set + " from " + this.relation.getName();
    }

    public Relation getRelation() {
        return this.relation;
    }

}
