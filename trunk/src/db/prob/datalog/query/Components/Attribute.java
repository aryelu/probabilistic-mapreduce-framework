package db.prob.datalog.query.Components;

import db.prob.datalog.query.Absyn.*;

/**
 * Created with IntelliJ IDEA.
 * User: arye
 * Date: 3/23/13
 * Time: 8:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class Attribute extends TermVar {
    public Attribute(Variable p1) {
        super(p1);
    }

    public Attribute(Attribute a) {
        super(a.variable_);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
