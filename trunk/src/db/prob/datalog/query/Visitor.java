package db.prob.datalog.query;

import db.prob.datalog.query.Absyn.*;
import db.prob.datalog.query.Components.ProbabilityRelation;
import db.prob.datalog.query.Components.Relation;

import java.util.List;


public interface Visitor {
    public void visit(Query p);

    public void visit(Head p);

    public void visit(Body p);

    public void visit(LiteralTerms p);

    public void visit(LiteralEQ p);

    public void visit(LiteralNE p);

    public void visit(PredicateSym p);

    public void visit(Term p);

    public void visit(TermVar p);

    public void visit(TermConst p);

    public void visit(Constant p);

    public void visit(ConstantId p);

    public void visit(ConstantStr p);

    public void visit(Variable p);

    public void visit(ProbabilityRelation pr);

    public void visit(Relation r);


    public void visit(Integer i);

    public void visit(Double d);

    public void visit(Character c);

    public void visit(String s);
}

