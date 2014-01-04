package db.prob;

import db.prob.datalog.query.Absyn.Query;
import db.prob.datalog.query.Absyn.Schema.DatabaseSchema;
import db.prob.datalog.query.Absyn.Schema.Relation;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;
import db.prob.datalog.query.Absyn.operators.Literal;
import db.prob.datalog.query.Absyn.operators.QueryJoin;
import db.prob.datalog.query.Absyn.operators.QuerySelection;
import db.prob.datalog.query.FunctionalDependency.FunctionalDependency;
import db.prob.mr.plan.ra.RAExpression;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SafePlanTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test_simple_case() throws Exception {
        Relation R1 = new Relation("R1", new HashSet<String>(Arrays.asList("a", "b")), false);
        Relation S1 = new Relation("S1", new HashSet<String>(Arrays.asList("c", "d")), false);
        Set<FunctionalDependency> functionalDependencySet = new HashSet<FunctionalDependency>();
        DatabaseSchema db = new DatabaseSchema(new HashSet<Relation>(Arrays.asList(R1, S1)), functionalDependencySet);
        HashSet<RelationAttribute> head = new HashSet<RelationAttribute>(
                Arrays.asList(
                        R1.getAttrByName("a"),
                        R1.getAttrByName("b"),
                        S1.getAttrByName("c"),
                        S1.getAttrByName("d")
                )
        );
        QuerySelection selectR = new QuerySelection(R1, new HashSet<String>(Arrays.asList("a", "b")));
        QuerySelection selectS = new QuerySelection(S1, new HashSet<String>(Arrays.asList("c", "d")));
        QueryJoin joinBC = new QueryJoin(R1.getAttrByName("b"), S1.getAttrByName("c"));
        List<Literal> body = Arrays.asList(selectR, selectS, joinBC);
        Query q = new Query(db, "shoki", head, body);
        RAExpression out = SafePlan.buildSafePlan(q);
        System.out.println(out.toLatex());
    }

    @Test
    public void test_second_case() throws Exception {
        Relation R1 = new Relation("R1", new HashSet<String>(Arrays.asList("a", "b")), true);
        Relation S1 = new Relation("S1", new HashSet<String>(Arrays.asList("c", "d")), true);
        Set<FunctionalDependency> functionalDependencySet = new HashSet<FunctionalDependency>();
        DatabaseSchema db = new DatabaseSchema(new HashSet<Relation>(Arrays.asList(R1, S1)), functionalDependencySet);
        HashSet<RelationAttribute> head = new HashSet<RelationAttribute>(Arrays.asList(
                R1.getAttrByName("b"),
                S1.getAttrByName("c"),
                S1.getAttrByName("d")
        ));
        QuerySelection select_R = new QuerySelection(R1, new HashSet<String>(Arrays.asList("a", "b")));
        QuerySelection select_S = new QuerySelection(S1, new HashSet<String>(Arrays.asList("c", "d")));
        QueryJoin join_b_c = new QueryJoin(R1.getAttrByName("b"), S1.getAttrByName("c"));
        List<Literal> body = Arrays.asList(select_R, select_S, join_b_c);
        Query q = new Query(db, "shoki", head, body);
        RAExpression out = SafePlan.buildSafePlan(q);
        System.out.println(out.toLatex());
    }

}
