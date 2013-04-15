package db.prob.datalog.query.Operators;

import db.prob.datalog.query.Absyn.Head;
import db.prob.datalog.query.Absyn.PredicateSym;
import db.prob.datalog.query.Absyn.Term;
import db.prob.datalog.query.Components.Attribute;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: arye
 * Date: 3/23/13
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class VisitHeadAttribute extends VisitHead {

    public List<Attribute> attr_list =new LinkedList<Attribute>();

    public void visit(Head h){
        for (Attribute a : h.listterm_){
            attr_list.add(new Attribute(a));
        }
    }
}
