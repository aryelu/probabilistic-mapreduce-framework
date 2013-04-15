package db.prob;


import db.prob.datalog.query.Absyn.*;
import db.prob.datalog.query.Components.Attribute;
import db.prob.datalog.query.Operators.VisitHead;
import db.prob.datalog.query.Operators.VisitHeadAttribute;
import db.prob.datalog.query.Operators.VisitRels;

import java.util.LinkedList;
import java.util.List;

public class testQueryAST {

    public static Query setup1(){
        Variable v1 = new Variable("v1");
        Variable v2 = new Variable("v2");
        Attribute tv1=new Attribute(v1);
        Attribute tv2=new Attribute(v2);
        List<Attribute> list_terms = new LinkedList<Attribute>();
        list_terms.add(tv1);
        list_terms.add(tv2);
        PredicateSym ps_head = new PredicateSym("Q1");
        Head h = new Head(ps_head,list_terms);

        LiteralEQ eq = new LiteralEQ(tv1,tv2);
        PredicateSym q2 = new PredicateSym("Q2");
        List<Term> ltq2 = new LinkedList<Term>();
        ltq2.add(tv1);
        ltq2.add(tv2);
        LiteralTerms ltq2t = new LiteralTerms(q2,ltq2);

        List<Literal> ll = new LinkedList<Literal>();
        ll.add(eq);
        ll.add(ltq2t);

        Body b = new Body(ll);
        Query q = new Query(h,b);
        return  q;
    }
    public static void main(String[] args){
        Query b1 = setup1();
        VisitHeadAttribute visitor = new VisitHeadAttribute();
        b1.accept(visitor);
        List<Attribute> rels = visitor.attr_list;
        System.out.println("done");

    }
}
