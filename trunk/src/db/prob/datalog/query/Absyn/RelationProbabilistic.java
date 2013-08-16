package db.prob.datalog.query.Absyn;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: arye
 * Date: 06/07/13
 * Time: 20:57
 * To change this template use File | Settings | File Templates.
 */
public class RelationProbabilistic extends Relation {
    public RelationProbabilistic(String name, Set<String> listterm, boolean is_probabilistic) {
        super(name, listterm, is_probabilistic);
    }
}
