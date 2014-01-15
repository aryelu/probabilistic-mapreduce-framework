package db.prob.mr.plan.ra.operators;

import db.prob.datalog.query.Absyn.Schema.Relation;
import db.prob.mr.plan.ra.RAOperator;
import db.prob.mr.plan.ra.ResultSize;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Arye on 15/01/14.
 */
public class CartesianProduct extends RAOperator {
    private List<String> relationSetNames = new LinkedList<String>();

    public CartesianProduct(Set<Relation> relationSet) {
        for (Relation r: relationSet){
            relationSetNames.add(r.getName());
        }
    }

    @Override
    public int getEstimatedResultSize(ResultSize size) {
        return 0;
    }

    @Override
    public String toLatex() {
        String ans = "";
        for (String name : relationSetNames){
            if (ans.equals("")){
                ans = name;
            }
            else{
                ans = String.format("%s \\times %s", ans, name );
            }
        }
        return ans;
    }
}
