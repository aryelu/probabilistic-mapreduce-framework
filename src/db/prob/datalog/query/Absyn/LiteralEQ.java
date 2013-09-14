package db.prob.datalog.query.Absyn; // Java Package generated by the BNF Converter.

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LiteralEQ implements Literal {
	public String term_left_, term_right_;
    private Set<String> terms;

	public LiteralEQ(String term_left , String term_right) {
		term_left_ = term_left;
		term_right_ = term_right;
        terms = new HashSet<String>();
        terms.add(term_left);
        terms.add(term_right);
	}

	@Override
	public Set<String> get_attr() {
		return Collections.emptySet();
	}
	
    public Set<String> get_terms(){
        return this.terms;
    }
}