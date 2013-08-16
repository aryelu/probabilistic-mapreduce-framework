package db.prob.datalog.query.Absyn; // Java Package generated by the BNF Converter.

import java.util.Set;

public class Relation implements Literal, ISchema {
    protected String name_;
    protected Set<String> listterm_;
    protected boolean probabilistic;

    public Relation(String name, Set<String> listterm, boolean is_probabilistic) {
        name_ = name;
        listterm_ = listterm;
        probabilistic = is_probabilistic;
    }

    @Override
    public Set<String> get_attr() {
        return listterm_;
    }
    public String toString(){
		return name_;    	
    }

}
