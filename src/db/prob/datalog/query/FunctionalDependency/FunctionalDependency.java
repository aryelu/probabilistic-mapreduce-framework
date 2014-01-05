package db.prob.datalog.query.FunctionalDependency;

import db.prob.datalog.query.Absyn.Schema.DatabaseSchema;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. User: arye Date: 26/06/13 Time: 08:08 To change
 * this template use File | Settings | File Templates.
 */
public class FunctionalDependency extends IFunctionalDependency implements IFunctionalDependencyOperator {

    private Set<RelationAttribute> left;
    private Set<RelationAttribute> right;

    public FunctionalDependency(DatabaseSchema schema, Set<RelationAttribute> left, Set<RelationAttribute> right) {
        super(schema);
        this.left = left;
        this.right = right;
    }

    /*
     * takes two groups A, B and returns all fd {a->b | a in A, b in B}
     */
    private Set<FunctionalDependency> produce_fd_from_set(
            Set<Set<RelationAttribute>> left_set, Set<Set<RelationAttribute>> right_set) {

        Set<FunctionalDependency> fd_set = new HashSet<FunctionalDependency>();
        for (Set<RelationAttribute> ls_item : left_set) {
            for (Set<RelationAttribute> rs_item : right_set) {
                fd_set.add(new FunctionalDependency(this.get_schema(), ls_item, rs_item));
            }
        }
        return fd_set;
    }

    public static Set<FunctionalDependency> pproduce_fd_from_set(DatabaseSchema schema,
                                                                 Set<Set<RelationAttribute>> left_set,
                                                                 Set<Set<RelationAttribute>> right_set) {
        Set<FunctionalDependency> fd_set = new HashSet<FunctionalDependency>();
        for (Set<RelationAttribute> ls_item : left_set) {
            for (Set<RelationAttribute> rs_item : right_set) {
                fd_set.add(new FunctionalDependency(schema, ls_item, rs_item));
            }
        }
        return fd_set;
    }

    /*
        Creates Set<FunctionalDependency> for given attribute Set
        means every subset b of A then A-->b
     */
    public static Set<FunctionalDependency> functional_dependency_from_attribute(DatabaseSchema schema,
                                                                                 Set<RelationAttribute> relationAttributeSet) {
        // create all subsets of relationAttributeSet
        Set<Set<RelationAttribute>> powerSet = powerSet(relationAttributeSet);
        Set<Set<RelationAttribute>> singleton = new HashSet<Set<RelationAttribute>>();
        singleton.add(relationAttributeSet);
        return FunctionalDependency.pproduce_fd_from_set(schema, singleton, powerSet);
    }

    /*
     * apply reflexivity rule on self and return set of all possible FD for a
     * set of attributes A, any subset b<A then A->b
     *
     */
    private Set<FunctionalDependency> reflex_group() {
        Set<Set<RelationAttribute>> group_power = this.schema_attr_power();
        Set<Set<RelationAttribute>> group_singleton = new HashSet<Set<RelationAttribute>>();
        group_singleton.add(this.schema_get_attr());

        Set<FunctionalDependency> reflex_set = pproduce_fd_from_set(this.get_schema(),
                group_singleton, group_power);
        return reflex_set;
    }

    /*
     * for each L subset of _left_ and R subset of _right_ add _left_ -> L and
     *  _right_ -> R to reflex_set
     */
    public Set<FunctionalDependency> reflexivity() {
        Set<FunctionalDependency> reflex_group = reflex_group();
        return reflex_group;
    }

    /*
      if a --> b and c is set of attr then ca --> cb
     */
    public Set<FunctionalDependency> augmentation() {
        Set<FunctionalDependency> augment_group = augment_group();
        return augment_group;
    }

    @Override
    public Set<FunctionalDependency> transitivity() {
        return new HashSet<FunctionalDependency>();
    }

    private Set<FunctionalDependency> augment_group() {
        Set<Set<RelationAttribute>> group_power = this.schema_attr_power();
        Set<FunctionalDependency> augment_set = new HashSet<FunctionalDependency>();
        // for c in group_power, add c to both sides
        for (Set<RelationAttribute> gamma : group_power) {
            Set<RelationAttribute> left_with_gamma = new HashSet<RelationAttribute>(left);
            left_with_gamma.addAll(gamma);
            Set<RelationAttribute> right_with_gamma = new HashSet<RelationAttribute>(right);
            right_with_gamma.addAll(gamma);
            FunctionalDependency fd = new FunctionalDependency(this.get_schema(), left_with_gamma, right_with_gamma);
            augment_set.add(fd);
        }
        return augment_set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionalDependency that = (FunctionalDependency) o;

        return left.equals(that.left) && right.equals(that.right);

    }

    @Override
    public int hashCode() {
        int result = left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }

    public String toString() {
        return left.toString() + " --> " + right.toString();
    }

    public Set<RelationAttribute> get_right() {
        return right;
    }

    public Set<RelationAttribute> get_left() {
        return left;
    }


}