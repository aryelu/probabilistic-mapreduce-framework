package db.prob.datalog.query.Components;

import db.prob.datalog.query.Absyn.LiteralTerms;
import db.prob.datalog.query.Absyn.PredicateSym;
import db.prob.datalog.query.Absyn.Term;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: arye
 * Date: 3/23/13
 * Time: 7:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Relation extends LiteralTerms {
    public Relation(PredicateSym p1, List<Term> p2) {
        super(p1, p2);
    }
}
