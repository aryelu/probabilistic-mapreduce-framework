package db.prob.datalog.query.Absyn;

import java.util.*;

/**
 * Created with IntelliJ IDEA. User: arye Date: 26/06/13 Time: 08:08 To change
 * this template use File | Settings | File Templates.
 */
public class FunctionalDependency extends IFunctionalDependency implements IFunctionalDependencyOperator, IBinaryOP<Set<String>>  {
    private ISchema schema;
    private Set<Set<String>> schema_attr_power;

	private Set<String> left;
	private Set<String> right;

    public FunctionalDependency(ISchema schema, Set<String> left, Set<String> right) {
		super(schema);
        this.left = left;
		this.right = right;
	}

	/*
	 * takes two groups A, B and returns all fd {a->b | a in A, b in B}
	 */
	private Set<FunctionalDependency> produce_fd_from_set(
			Set<Set<String>> left_set, Set<Set<String>> right_set) {

		Set<FunctionalDependency> fd_set = new HashSet<FunctionalDependency>();
		for (Set<String> ls_item : left_set) {
			for (Set<String> rs_item : right_set) {
				fd_set.add(new FunctionalDependency(schema, ls_item, rs_item));
			}
		}
		return fd_set;
	}


	/*
	 * apply reflexivity rule on self and return set of all possible FD for a
	 * set of attributes A, any subset b<A then A->b 
	 * 
	 */
	private  Set<FunctionalDependency> reflex_group() {
        Set<Set<String>> group_power = this.schema_attr_power();
		Set<Set<String>> group_singleton = new HashSet<Set<String>>();
        group_singleton.add(this.schema_get_attr());

        Set<FunctionalDependency> reflex_set = produce_fd_from_set(
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
    public Set<FunctionalDependency> augmentation(){
        Set<FunctionalDependency> augment_group = augment_group();
        return augment_group;
    }

    @Override
    public Set<FunctionalDependency> transitivity() {
        return new HashSet<FunctionalDependency>();
    }

    private Set<FunctionalDependency> augment_group() {
        Set<Set<String>> group_power = this.schema_attr_power();
        Set<FunctionalDependency> augment_set = new HashSet<FunctionalDependency>();
        // for c in group_power, add c to both sides
        for (Set<String> gamma: group_power){
            Set<String> left_with_gamma = new HashSet<String>(left);
            left_with_gamma.addAll(gamma);
            Set<String> right_with_gamma = new HashSet<String>(right);
            right_with_gamma.addAll(gamma);
            FunctionalDependency fd = new FunctionalDependency(schema, left_with_gamma, right_with_gamma);
            augment_set.add(fd);
        }
        return augment_set;
    }

    public String toString(){
        return left.toString() + " --> " + right.toString();
    }

    @Override
    public Set<String> get_right() {
        return right;
    }

    @Override
    public Set<String> get_left() {
        return left;
    }
}
