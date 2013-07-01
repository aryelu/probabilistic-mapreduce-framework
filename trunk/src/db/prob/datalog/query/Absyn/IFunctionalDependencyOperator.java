package db.prob.datalog.query.Absyn;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: arye
 * Date: 29/06/13
 * Time: 13:17
 * To change this template use File | Settings | File Templates.
 */
public interface IFunctionalDependencyOperator {

    /*
     * for each L subset of _left_ and R subset of _right_ add _left_ -> L and
     *  _right_ -> R to reflex_set
     */
    public Set<FunctionalDependency> reflexivity();

    /*
      if a --> b and c is set of attr then ca --> cb
     */
    public Set<FunctionalDependency> augmentation();

    /*
     * for a -> b , b -> c in Gamma
     * add a -> c
     */
    public Set<FunctionalDependency> transitivity();

}
