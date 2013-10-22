package db.prob;

import db.prob.datalog.query.Absyn.Query;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SafePlanTest {
    Query q;

    @Before
    public void setUp() throws Exception {
        Set<String> head = new HashSet<String>(Arrays.asList("a", "b", "c", "d", "e", "f"));
        //QuerySelection R1 = new QuerySelection("R1", new HashSet<String>(Arrays.asList("a","b")), false);
        //QuerySelection R2 = new QuerySelection("R2", new HashSet<String>(Arrays.asList("c","d")), false);
        //QuerySelection R3 = new QuerySelection("R3", new HashSet<String>(Arrays.asList("e","f")), false);
        //QueryJoin leq = new QueryJoin("b","c");
        //List<? extends Literal> body = Arrays.asList(R1, R2, leq, R3);
        //q = new Query("moshe",head,body);
    }

    @Test
    public void test_simple_case() {
        //SafePlan.safeplan(schema, q);
    }

}
