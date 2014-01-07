package db.prob.datalog.query.FunctionalDependency;

import db.prob.datalog.query.Absyn.Schema.DatabaseSchema;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;

import java.util.HashSet;
import java.util.Set;

/**
 * a Functional dependency A -> B.
 * 
 * Created with IntelliJ IDEA. 
 * User: arye Date: 26/06/13 Time: 08:08 To change
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

    /**
     * takes two groups A, B and returns all FD {a -> b | a in A, b in B}
     * 
     * @param schema
     * @param leftSet
     * @param rightSet
     * @return
     */
    public static Set<FunctionalDependency> produceFdFromSet(
    		DatabaseSchema schema, Set<Set<RelationAttribute>> leftSet, Set<Set<RelationAttribute>> rightSet) {
        Set<FunctionalDependency> fd_set = new HashSet<FunctionalDependency>();
        for (Set<RelationAttribute> ls_item : leftSet) {
            for (Set<RelationAttribute> rs_item : rightSet) {
                fd_set.add(new FunctionalDependency(schema, ls_item, rs_item));
            }
        }
        return fd_set;
    }

    /**
     * Creates Set<FunctionalDependency> for given attribute Set
     * means every subset b of A then A-->b
     * 
     * @param schema
     * @param relationAttributeSet
     * @return
     */
    public static Set<FunctionalDependency> functional_dependency_from_attribute(DatabaseSchema schema,
                                                                                 Set<RelationAttribute> relationAttributeSet) {
        // create all subsets of relationAttributeSet
        Set<Set<RelationAttribute>> powerSet = powerSet(relationAttributeSet);
        Set<Set<RelationAttribute>> singleton = new HashSet<Set<RelationAttribute>>();
        singleton.add(relationAttributeSet);
        return FunctionalDependency.produceFdFromSet(schema, singleton, powerSet);
    }

    /**
     * apply reflexivity rule on self and return set of all possible FD for a
     * set of attributes A, any subset b<A then A->b
     *
     */
    public Set<FunctionalDependency> reflexivity() {
        Set<FunctionalDependency> allReflex = new HashSet<FunctionalDependency>();
        Set<FunctionalDependency> leftReflex = this.reflexAttribute(left);
        Set<FunctionalDependency> rightReflex = this.reflexAttribute(right);
        allReflex.addAll(leftReflex);
        allReflex.addAll(rightReflex);
        return allReflex;
    }

    private Set<FunctionalDependency> reflexAttribute(Set<RelationAttribute> attributeSet) {
        Set<Set<RelationAttribute>> power = IFunctionalDependency.powerSet(attributeSet);
        Set<Set<RelationAttribute>> singleton = new HashSet<Set<RelationAttribute>>();
        singleton.add(attributeSet);

        Set<FunctionalDependency> reflexSet = produceFdFromSet(this.get_schema(), singleton, power);
        return reflexSet;
    }


    /**
     * if a --> b and c is set of attr then ca --> cb
     */
    public Set<FunctionalDependency> augmentation() {
        Set<RelationAttribute> group_power = this.schema_get_attr();
        Set<FunctionalDependency> augment_set = new HashSet<FunctionalDependency>();
        // for c in group_power, add c to both sides
        for (RelationAttribute gamma : group_power) {
            Set<RelationAttribute> left_with_gamma = new HashSet<RelationAttribute>(left);
            left_with_gamma.add(gamma);
            Set<RelationAttribute> right_with_gamma = new HashSet<RelationAttribute>(right);
            right_with_gamma.add(gamma);
            FunctionalDependency fd = new FunctionalDependency(this.get_schema(), left_with_gamma, right_with_gamma);
            augment_set.add(fd);
        }
        return augment_set;
    }


    @Override
    public Set<FunctionalDependency> transitivity() {
        return new HashSet<FunctionalDependency>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionalDependency that = (FunctionalDependency) o;

        return left.containsAll(that.left) && right.containsAll(that.right);

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
