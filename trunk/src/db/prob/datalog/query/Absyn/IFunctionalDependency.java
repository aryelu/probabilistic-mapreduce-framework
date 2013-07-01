package db.prob.datalog.query.Absyn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: arye
 * Date: 29/06/13
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class IFunctionalDependency {
    private ISchema schema;
    private Set<Set<String>> schema_attr_power;


    public IFunctionalDependency(ISchema schema) {
        this.schema = schema;
    }

    protected Set<Set<String>> schema_attr_power(){
        Set<Set<String>> schema_attr_power = this.schema_attr_power;
        if (schema_attr_power == null){
            this.schema_attr_power = powerSet(this.schema.get_attr());
            schema_attr_power = this.schema_attr_power;
        }
        return schema_attr_power;
    }

    public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
        for (Set<T> set : powerSet(rest)) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }
    public Set<String> schema_get_attr(){
        return schema.get_attr();
    }
    public ISchema get_schema(){
        return this.schema;
    }
}
