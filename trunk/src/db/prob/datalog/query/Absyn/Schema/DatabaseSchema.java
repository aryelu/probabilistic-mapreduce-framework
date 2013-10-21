package db.prob.datalog.query.Absyn.Schema;

import db.prob.datalog.query.FunctionalDependency.FunctionalDependency;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents schema for Database
 */
public class DatabaseSchema {
    private Set<Relation> relation_set;

    public DatabaseSchema(Set<Relation> relation_set) {
        this.relation_set = relation_set;
    }

    /*
        traverse DB schema's relations and produces all functional dependencies
        from relations,
     */
    public Set<FunctionalDependency> get_fd_set() {
        Set<FunctionalDependency> fd_set = new HashSet<FunctionalDependency>();
        for (Relation relation : this.relation_set) {
            Set<FunctionalDependency> relationfunctionalDependencySet =
                    FunctionalDependency.functional_dependency_from_attribute(this, relation.getAttribute_set());
            fd_set.addAll(relationfunctionalDependencySet);
        }
        return fd_set;
    }

    public Set<RelationAttribute> get_relation_attribute() {
        Set<RelationAttribute> attr_set = new HashSet<RelationAttribute>();
        for (Relation relation : this.relation_set) {
            attr_set.addAll(relation.get_attr());
        }
        return attr_set;
    }
}
