package db.prob.datalog.query.Absyn.Schema;

import db.prob.datalog.query.FunctionalDependency.FunctionalDependency;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
            Set<RelationAttribute> relationAttributeSet = relation.getAttributesSet();
            Set<FunctionalDependency> relationfunctionalDependencySet =
                    FunctionalDependency.functional_dependency_from_attribute(this, relationAttributeSet);
            fdSet.addAll(relationfunctionalDependencySet);
            FunctionalDependency eventFD = new FunctionalDependency(this, relation.getProbabilisticAttributeAsSet(), relationAttributeSet);
            fdSet.add(eventFD);
        }
        fdSet.addAll(this.fdSet);
        return fdSet;
    }

    public Set<RelationAttribute> get_relation_attribute() {
        Set<RelationAttribute> attr_set = new HashSet<RelationAttribute>();
        for (Relation relation : this.relations) {
            attr_set.addAll(relation.getAttributesSetAndProb());
        }
        return attr_set;
    }
}
