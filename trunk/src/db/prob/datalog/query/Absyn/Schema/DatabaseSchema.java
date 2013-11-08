package db.prob.datalog.query.Absyn.Schema;

import db.prob.datalog.query.FunctionalDependency.FunctionalDependency;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents schema for Database
 */
public class DatabaseSchema {
    private Set<Relation> relations;
    private Set<FunctionalDependency> functionalDependencySet;

    public DatabaseSchema(Set<Relation> relation_set, Set<FunctionalDependency> functionalDependencySet) {
        this.relations = relation_set;
        this.functionalDependencySet = functionalDependencySet;
    }

    /**
     * Traverse schema's relations and produces all functional dependencies
     * from relations,
     *
     * @return
     */
    public Set<FunctionalDependency> getFdSet() {
        Set<FunctionalDependency> fdSet = new HashSet<FunctionalDependency>();
        for (Relation relation : this.relations) {
            Set<FunctionalDependency> relationfunctionalDependencySet =
                    FunctionalDependency.functional_dependency_from_attribute(this, relation.getAttribute_set());
            fdSet.addAll(relationfunctionalDependencySet);
        }
        fdSet.addAll(this.functionalDependencySet);
        return fdSet;
    }

    public Set<RelationAttribute> get_relation_attribute() {
        Set<RelationAttribute> attr_set = new HashSet<RelationAttribute>();
        for (Relation relation : this.relations) {
            attr_set.addAll(relation.get_attr());
        }
        return attr_set;
    }
}
