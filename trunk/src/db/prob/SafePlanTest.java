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
    public void testSimpleCase() throws Exception {
        Relation R1 = new Relation("R1", new String[]{"a", "b"}, false);
        Relation S1 = new Relation("S1", new String[]{"c", "d"}, false);
        
        Set<FunctionalDependency> fd = new HashSet<FunctionalDependency>();
        
        DatabaseSchema db = new DatabaseSchema(new Relation[] {R1, S1}, fd);
        
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
        System.out.println(q.toString());
        RAExpression out = SafePlan.buildSafePlan(q);
        System.out.println(out.toLatex());
    }

    @Test
    public void testSecondCase() throws Exception {
        Relation R1 = new Relation("R1", new String[] {"a", "b"}, true);
        Relation S1 = new Relation("S1", new String[] {"c", "d"}, true);
        Set<FunctionalDependency> fd = new HashSet<FunctionalDependency>();
        DatabaseSchema db = new DatabaseSchema(new HashSet<Relation>(Arrays.asList(R1, S1)), fd);
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
        System.out.println(q.toString());
        RAExpression out = SafePlan.buildSafePlan(q);
        System.out.println(out.toLatex());
    }

    @Test
    public void testFromSuciuPaper() throws Exception  {
    	/*
    	 * This is the query in Suciu's paper.
    	 * We have two relations: S(A,B) and T(C,D)
    	 * the query is : q(D) := S(A,B),T(C,D),B=C
    	 */
    	Relation relationS = new Relation("S", new String[] {"A","B"}, true);
    	Relation relationT = new Relation("T", new String[] {"C","D"}, true);
    	Set<FunctionalDependency> fdSet = new HashSet<FunctionalDependency>();
    	DatabaseSchema db = new DatabaseSchema(new Relation[] {relationS, relationT}, fdSet);

        Set<RelationAttribute> left  = new HashSet<RelationAttribute>();
    	Set<RelationAttribute> right = new HashSet<RelationAttribute>();
        left.add(relationT.getAttrByName("C"));
        right.add(relationT.getAttrByName("D"));

        // C -> D
    	FunctionalDependency fd1 = new FunctionalDependency(db, left, right);
    	fdSet.add(fd1);

        // Now the query:
        HashSet<RelationAttribute> head = new HashSet<RelationAttribute>();
        head.add(relationT.getAttrByName("D"));
    	
        QuerySelection selectS = new QuerySelection(relationS, new HashSet<String>(Arrays.asList("A", "B")));
        QuerySelection selectT = new QuerySelection(relationT, new HashSet<String>(Arrays.asList("C", "D")));
        QueryJoin joinSandT = new QueryJoin(relationS.getAttrByName("B"), relationT.getAttrByName("C"));
        List<Literal> body = Arrays.asList(selectS, selectT, joinSandT);
        Query q = new Query(db, "shoki", head, body);
        System.out.println(q.toString());
        RAExpression out = SafePlan.buildSafePlan(q);
        System.out.println(out.toLatex());

    	
    }
}
