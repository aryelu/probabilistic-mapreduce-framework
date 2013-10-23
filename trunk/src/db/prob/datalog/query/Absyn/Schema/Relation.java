package db.prob.datalog.query.Absyn.Schema;

import java.util.HashSet;
import java.util.Set;

/**
 * represents Relation
 * holds it attributes
 */
public class Relation {
    // name
    private String name;
    // set of attributes for relation
    private Set<RelationAttribute> attribute_set = new HashSet<RelationAttribute>();
    // tells if this relation is a probabilistic one
    private boolean probabilistic;
    //if it's probabilistic then this is the Event attribute
    private RelationAttribute probabilistic_attribute;

    public Relation(String name, Set<String> attribute_name_set, boolean probabilistic) {
        this.name = name;
        for (String attr_name : attribute_name_set) {
            this.attribute_set.add(new RelationAttribute(this, attr_name));
        }
        this.probabilistic = probabilistic;
        if (probabilistic) {
            this.probabilistic_attribute = new RelationAttribute(this, "Event");
        }
    }


    public Set<RelationAttribute> get_attr() {
        return getAttribute_set();
    }

    public String getName() {
        return name;
    }

    public Set<RelationAttribute> getAttribute_set() {
        return attribute_set;
    }

    public boolean is_probabilistic() {
        return probabilistic;
    }

    public RelationAttribute getProbabilistic_attribute() {
        return probabilistic_attribute;
    }

    public RelationAttribute get_attr_by_name(String attr_name) throws Exception {
        for (RelationAttribute relationAttribute : this.getAttribute_set()) {
            if (relationAttribute.name.equals(attr_name)) {
                return relationAttribute;
            }
        }
        throw new Exception("Not found");
    }
    public static Set<RelationAttribute> set_to_attr(Set<Relation> relationSet){
        Set<RelationAttribute> relationAttributeSet = new HashSet<RelationAttribute>();
        for( Relation relation: relationSet){
            relationAttributeSet.addAll(relation.attribute_set);
        }
        return relationAttributeSet;
    }
    public String  toString(){
        return this.name;
    }
}
