package db.prob.datalog.query.Absyn.Schema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import db.prob.datalog.query.FunctionalDependency.FunctionalDependency;

/**
 * Represents schema for Database
 */
public class DatabaseSchema {
    private Set<Relation> relations;
    private Set<FunctionalDependency> fdSet;

    /**
     * Constructor of database schema
     * 
     * @param relations set of relations
     * @param fdSet functional dependencies set.
     */
    public DatabaseSchema(Set<Relation> relations, Set<FunctionalDependency> fdSet) {
        this.relations = relations;
        this.fdSet = fdSet;
    }
    
    public DatabaseSchema(Relation[] relations, Set<FunctionalDependency> fdSet) {
    	this(new HashSet<Relation>(Arrays.asList(relations)), fdSet);
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
                    FunctionalDependency.functional_dependency_from_attribute(this, relation.getAttributesSet());
            fdSet.addAll(relationfunctionalDependencySet);
        }
        fdSet.addAll(this.fdSet);
        return fdSet;
    }

    public Set<RelationAttribute> get_relation_attribute() {
        Set<RelationAttribute> attr_set = new HashSet<RelationAttribute>();
        for (Relation relation : this.relations) {
            attr_set.addAll(relation.getAttributesSet());
        }
        return attr_set;
    }
}
