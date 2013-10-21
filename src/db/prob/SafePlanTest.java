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
        //Selection R1 = new Selection("R1", new HashSet<String>(Arrays.asList("a","b")), false);
        //Selection R2 = new Selection("R2", new HashSet<String>(Arrays.asList("c","d")), false);
        //Selection R3 = new Selection("R3", new HashSet<String>(Arrays.asList("e","f")), false);
        //Join leq = new Join("b","c");
        //List<? extends Literal> body = Arrays.asList(R1, R2, leq, R3);
        //q = new Query("moshe",head,body);
    }

    @Test
    public void test_simple_case() {
        //SafePlan.safeplan(schema, q);
    }

}
