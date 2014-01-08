package db.prob.datalog.query.Absyn.Schema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * represents Relation
 * holds it attributes
 */
public class Relation {

    /**
     * Name of relation
     */
    private String name;

    /**
     * The attributes (or features) of the relation.
     */
    private Set<RelationAttribute> attributes = new HashSet<RelationAttribute>();

    /**
     * flag if this is a probabilistic relation.
     */
    private boolean isProb;

    /**
     * an attribute that is used only for probabilistic relations.
     * This attribute contains the probability itself.
     */
    private RelationAttribute probabilistic_attribute;

    /**
     * Constructor of a relation.
     *
     * @param name            Name of relation.
     * @param attributesNames set of attributes
     * @param isProb          flag whether its probabilistic
     */
    public Relation(String name, Set<String> attributesNames, boolean isProb) {
        this.name = name;
        for (String attr_name : attributesNames) {
            this.attributes.add(new RelationAttribute(this, attr_name));
        }
        this.isProb = isProb;
        if (isProb) {
            this.probabilistic_attribute = new RelationAttribute(this, "E");
        }
    }

    /**
     * Constructor for {@link Relation} instance.
     * 
     * @param name      name of the relation.
     * @param attrNames the attributes
     * @param isProb    flag whether its probabilistic
     */
    public Relation(String name, String[] attrNames, boolean isProb) {
        this(name, new HashSet<String>(Arrays.asList(attrNames)), isProb);
    }

    public String getName() {
        return name;
    }

    public Set<RelationAttribute> getAttributesSet() {
        return attributes;
    }

    public Set<RelationAttribute> getAttributesSetAndProb() {
        Set<RelationAttribute> relationAttributeSet = new HashSet<RelationAttribute>(attributes);
        relationAttributeSet.add(probabilistic_attribute);
        return relationAttributeSet;
    }

    /**
     * whether this is a probabilistic relation.
     * 
     * @return
     */
    public boolean isProb() {
        return isProb;
    }

    public RelationAttribute getProbabilistic_attribute() {
        return probabilistic_attribute;
    }

    public Set<RelationAttribute> getProbabilisticAttributeAsSet() {
        return new HashSet<RelationAttribute>(Arrays.asList(probabilistic_attribute));
    }

    public RelationAttribute getAttrByName(String attr_name) throws Exception {
        for (RelationAttribute relationAttribute : this.getAttributesSet()) {
            if (relationAttribute.name.equals(attr_name)) {
                return relationAttribute;
            }
        }
        throw new Exception("Not found");
    }

    public String toString() {
        return this.name;
    }
}
