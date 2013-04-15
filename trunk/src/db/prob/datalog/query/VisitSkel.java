package db.prob.datalog.query;

import db.prob.datalog.query.Absyn.*;
import db.prob.datalog.query.Components.ProbabilityRelation;
import db.prob.datalog.query.Components.Relation;
import db.prob.datalog.query.Visitor;

import java.util.LinkedList;
import java.util.List;

/**
 * BNFC-Generated Visitor Design Pattern Skeleton. **
 */
/* This implements the common visitor design pattern.
   Tests show it to be slightly less efficient than the
   instanceof method, but easier to use.
   Note that this method uses Visitor-traversal of lists, so
   List.accept() does NOT traverse the list. This allows different
   algorithms to use context information differently. */

public class VisitSkel implements Visitor {

    public void visit(Query query) {
    /* Code For Query Goes Here */

        query.head_.accept(this);
        query.body_.accept(this);
    }

    public void visit(Head head) {
    /* Code For Head Goes Here */

        head.predicatesym_.accept(this);
        if (head.listterm_ != null) {
            for (Term t : head.listterm_) {
                t.accept(this);
            }
        }
    }

    public void visit(Body body) {
    /* Code For Body Goes Here */

        if (body.listliteral_ != null) {
            for (Literal l : body.listliteral_) {
                l.accept(this);
            }
        }
    }

    public void visit(LiteralTerms literalterms) {
    /* Code For LiteralTerms Goes Here */

        literalterms.predicatesym_.accept(this);
        if (literalterms.listterm_ != null) {
            for (Term t : literalterms.listterm_) {
                t.accept(this);
            }
        }
    }

    public void visit(LiteralEQ literaleq) {
    /* Code For LiteralEQ Goes Here */

        literaleq.term_1.accept(this);
        literaleq.term_2.accept(this);
    }

    public void visit(LiteralNE literalne) {
    /* Code For LiteralNE Goes Here */

        literalne.term_1.accept(this);
        literalne.term_2.accept(this);
    }

    public void visit(PredicateSym predicatesym) {
    /* Code For Predicatesym Goes Here */

        visit(predicatesym.string_);
    }

    public void visit(Term term) {
    } //abstract class

    public void visit(TermVar termvar) {
    /* Code For TermVar Goes Here */

        termvar.variable_.accept(this);
    }

    public void visit(TermConst termconst) {
    /* Code For TermConst Goes Here */

        termconst.constant_.accept(this);
    }

    public void visit(Constant constant) {
    } //abstract class

    public void visit(ConstantId constantid) {
    /* Code For ConstantId Goes Here */

        visit(constantid.ident_);
    }

    @Override
    public void visit(ConstantStr constantStr) {
        constantStr.accept(this);
    }

    public void visit(Variable variable) {
    /* Code For Variable Goes Here */

        visit(variable.string_);
    }

    public void visit(ProbabilityRelation pr) {
        this.visit((LiteralTerms) pr);
    }

    public void visit(Relation r) {
        this.visit((LiteralTerms) r);
    }

    public void visit(Integer i) {
    }

    public void visit(Double d) {
    }

    public void visit(Character c) {
    }

    public void visit(String s) {
    }
}
