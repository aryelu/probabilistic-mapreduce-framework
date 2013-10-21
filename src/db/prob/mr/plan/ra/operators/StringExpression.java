package db.prob.mr.plan.ra.operators;

import db.prob.mr.plan.ra.RAExpression;
import db.prob.mr.plan.ra.RAOperator;
import db.prob.mr.plan.ra.ResultSize;

/**
 * empty class for inserting string output
 */
public class StringExpression implements RAExpression {
    private String text;

    public StringExpression(String text){
        this.text = new String(text);
    }
    @Override
    public int getEstimatedResultSize(ResultSize size) {
        return 0;
    }

    @Override
    public String getName() {
        return this.text;
    }

    @Override
    public String toLatex() {
        return null;
    }
}
