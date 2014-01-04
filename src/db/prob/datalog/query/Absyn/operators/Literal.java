package db.prob.datalog.query.Absyn.operators;

import db.prob.datalog.query.Absyn.Schema.RelationAttribute;

import java.util.Set;

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
