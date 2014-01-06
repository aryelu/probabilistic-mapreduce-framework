package db.prob.datalog.query.Absyn.operators;

import java.util.Set;

import db.prob.datalog.query.Absyn.Schema.RelationAttribute;

/**
 * item in query's body
 */
public interface Literal {
    /**
     * acts as Attr(q) in article
     *
     * @return holds all attributes referenced in this literal
     */
    public Set<RelationAttribute> getAttributes();
}
