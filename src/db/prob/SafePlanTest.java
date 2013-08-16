package db.prob;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import db.prob.datalog.query.Absyn.Literal;
import db.prob.datalog.query.Absyn.LiteralEQ;
import db.prob.datalog.query.Absyn.Query;
import db.prob.datalog.query.Absyn.Relation;

public class SafePlanTest {
	Query q;
	@Before
	public void setUp() throws Exception {
		Set<String>head = new HashSet<String>(Arrays.asList("a","b","c","d","e","f"));
		Relation R1 = new Relation("R1", new HashSet<String>(Arrays.asList("a","b")), false);
		Relation R2 = new Relation("R2", new HashSet<String>(Arrays.asList("c","d")), false);
		Relation R3 = new Relation("R3", new HashSet<String>(Arrays.asList("e","f")), false);
		LiteralEQ leq = new LiteralEQ("b","c");
		List<? extends Literal> body = Arrays.asList(R1, R2, leq, R3);
		q = new Query("moshe",head,body);
	}

	@Test
	public void test_simple_case() {
		SafePlan.safeplan(q);
	}

}
