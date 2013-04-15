package db.prob.datalog.query.Operators;

import db.prob.datalog.query.Absyn.Query;
import db.prob.datalog.query.VisitSkel;

/**
 * Created with IntelliJ IDEA.
 * User: arye
 * Date: 3/23/13
 * Time: 8:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class VisitBody extends VisitSkel {
    public void visit(Query query) {
    /* Code For Query Goes Here */

        query.body_.accept(this);
    }
}
