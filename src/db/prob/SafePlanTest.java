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
	
	private DatabaseSchema createSchemaForMyPaper() throws Exception {
        Relation emp = new Relation("Emp", new String[]{"eid", "name", "did", "rid"}, true);
        Relation dept = new Relation("Dept", new String[]{"did", "dname"}, true);
        Relation rank = new Relation("Rank", new String[]{"rid", "rname"}, true);

        Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();
        
        DatabaseSchema db = new DatabaseSchema(new Relation[]{emp, dept, rank}, fds);

        // FD1: eid -> name, did, rid
        FunctionalDependency fd1 = FunctionalDependency.createFd(db, emp, new String[]{"eid"}, new String[]{"name", "did", "rid"});
        fds.add(fd1);

        // FD2: did -> dname        
        FunctionalDependency fd2 = FunctionalDependency.createFd(db, dept, new String[]{"did"}, new String[]{"dname"});
        fds.add(fd2);

        // FD3: did -> dname        
        FunctionalDependency fd3 = FunctionalDependency.createFd(db, rank, new String[]{"rid"}, new String[]{"rname"});
        fds.add(fd3);
		
        return db;
	}

    @Before
    public void setUp() throws Exception {

    }

    public void testSimpleCase() throws Exception {
        Relation R1 = new Relation("R1", new String[]{"a", "b"}, false);
        Relation S1 = new Relation("S1", new String[]{"c", "d"}, false);

        Set<FunctionalDependency> fd = new HashSet<FunctionalDependency>();

        DatabaseSchema db = new DatabaseSchema(new Relation[]{R1, S1}, fd);

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

    public void testSecondCase() throws Exception {
        Relation R1 = new Relation("R1", new String[]{"a", "b"}, true);
        Relation S1 = new Relation("S1", new String[]{"c", "d"}, true);
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
    public void testFromSuciuPaper() throws Exception {
        /*
    	 * This is the query in Suciu's paper.
    	 * We have two relations: S(A,B) and T(C,D)
    	 * And a FD: C -> D
    	 * the query is : q(D) := S(A,B),T(C,D),B=C
    	 */
        Relation relationS = new Relation("S", new String[]{"A", "B"}, true);
        Relation relationT = new Relation("T", new String[]{"C", "D"}, true);
        Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();
        DatabaseSchema db = new DatabaseSchema(new Relation[]{relationS, relationT}, fds);

        // FD: C -> D
        FunctionalDependency fd1 = FunctionalDependency.createFd(db, relationT, new String[]{"C"}, new String[]{"D"});
        fds.add(fd1);

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
        System.out.println();
    }
    
    

    @Test
    public void testFromMyPaper() throws Exception {
    	/*
    	 * This is the query from Yaron's paper.
    	 * We have 3 relations: Emp(eid,name,did,rid), Dept(did,dname), Rank(rid,rname)
    	 * the query is : q(did,rid) := Emp(eid,name,did,rid), Dept(did,dname), Rank(rid,rname),did=did,rid=rid
    	 */
    	
    	DatabaseSchema db = createSchemaForMyPaper();

        Relation emp  = db.getRelation("Emp");
        Relation dept = db.getRelation("Dept");
        Relation rank = db.getRelation("Rank");

        // Now the query:
        HashSet<RelationAttribute> head = new HashSet<RelationAttribute>();
        head.add(emp.getAttrByName("did"));
        head.add(emp.getAttrByName("rid"));

        QuerySelection sEmp = new QuerySelection(emp, new HashSet<String>(Arrays.asList("eid", "name", "did", "rid")));
        QuerySelection sDept = new QuerySelection(dept, new HashSet<String>(Arrays.asList("did", "dname")));
        QuerySelection sRank = new QuerySelection(rank, new HashSet<String>(Arrays.asList("rid", "rname")));

        QueryJoin joinEmpAndDept = new QueryJoin(emp.getAttrByName("did"), dept.getAttrByName("did"));
        QueryJoin joinEmpAndRank = new QueryJoin(emp.getAttrByName("rid"), rank.getAttrByName("rid"));

        List<Literal> body = Arrays.asList(sEmp, sDept, sRank, joinEmpAndDept, joinEmpAndRank);

        Query q = new Query(db, "shoki", head, body);
        System.out.println(q.toString());
        RAExpression out = SafePlan.buildSafePlan(q);
        System.out.println(out.toLatex());
        System.out.println();
    }

    @Test(expected = Exception.class)
    public void testFromMyPaper2() throws Exception {
    	/*
    	 * This is the query from Yaron's paper.
    	 * We have 3 relations: Emp(eid,name,did,rid), Dept(did,dname), Rank(rid,rname)
    	 * the query is : q(dname) := Emp(eid,name,did,rid), Dept(did,dname), Rank(rid,rname),did=did,rid=rid
    	 * this should NOT yield a safe plan.
    	 */
    	DatabaseSchema db = createSchemaForMyPaper();

        Relation emp  = db.getRelation("Emp");
        Relation dept = db.getRelation("Dept");
        Relation rank = db.getRelation("Rank");

        // Now the query:
        HashSet<RelationAttribute> head = new HashSet<RelationAttribute>();
        head.add(dept.getAttrByName("dname"));
        //head.add(emp.getAttrByName("rid"));

        QuerySelection sEmp = new QuerySelection(emp, new HashSet<String>(Arrays.asList("eid", "name", "did", "rid")));
        QuerySelection sDept = new QuerySelection(dept, new HashSet<String>(Arrays.asList("did", "dname")));
        QuerySelection sRank = new QuerySelection(rank, new HashSet<String>(Arrays.asList("rid", "rname")));

        QueryJoin joinEmpAndDept = new QueryJoin(emp.getAttrByName("did"), dept.getAttrByName("did"));
        QueryJoin joinEmpAndRank = new QueryJoin(emp.getAttrByName("rid"), rank.getAttrByName("rid"));

        List<Literal> body = Arrays.asList(sEmp, sDept, sRank, joinEmpAndDept, joinEmpAndRank);

        Query q = new Query(db, "shoki", head, body);
        System.out.println(q.toString());

        RAExpression out = SafePlan.buildSafePlan(q);
        System.out.println(out.toLatex());
        System.out.println();
    }
    
    @Test(expected = Exception.class)
    public void testFromMyPaper3() throws Exception {
    	/*
    	 * This is the query from Yaron's paper.
    	 * We have 3 relations: Emp(eid,name,did,rid), Dept(did,dname), Rank(rid,rname)
    	 * the query is : q(dname,rname) := Emp(eid,name,did,rid), Dept(did,dname), Rank(rid,rname),did=did,rid=rid
    	 * this should NOT yield a safe plan.
    	 */
    	DatabaseSchema db = createSchemaForMyPaper();

        Relation emp  = db.getRelation("Emp");
        Relation dept = db.getRelation("Dept");
        Relation rank = db.getRelation("Rank");

        // Now the query:
        HashSet<RelationAttribute> head = new HashSet<RelationAttribute>();
        head.add(dept.getAttrByName("dname"));
        head.add(rank.getAttrByName("rname"));

        QuerySelection sEmp = new QuerySelection(emp, new HashSet<String>(Arrays.asList("eid", "name", "did", "rid")));
        QuerySelection sDept = new QuerySelection(dept, new HashSet<String>(Arrays.asList("did", "dname")));
        QuerySelection sRank = new QuerySelection(rank, new HashSet<String>(Arrays.asList("rid", "rname")));

        QueryJoin joinEmpAndDept = new QueryJoin(emp.getAttrByName("did"), dept.getAttrByName("did"));
        QueryJoin joinEmpAndRank = new QueryJoin(emp.getAttrByName("rid"), rank.getAttrByName("rid"));

        List<Literal> body = Arrays.asList(sEmp, sDept, sRank, joinEmpAndDept, joinEmpAndRank);

        Query q = new Query(db, "shoki", head, body);
        System.out.println(q.toString());

        RAExpression out = SafePlan.buildSafePlan(q);
        System.out.println(out.toLatex());
        System.out.println();
    }
    

    @Test
    public void testFromSuciuPaper2() throws Exception {
        /*
    	 * This is a variation on the query in Suciu's paper.
    	 *  
    	 * We have two relations: S(A,B) and T(C,D)
    	 * And a FD: C -> D
    	 * the query is : q(A) := S(A,B),T(C,D),B=C
    	 */
        Relation relationS = new Relation("S", new String[]{"A", "B"}, true);
        Relation relationT = new Relation("T", new String[]{"C", "D"}, true);
        Set<FunctionalDependency> fds = new HashSet<FunctionalDependency>();
        DatabaseSchema db = new DatabaseSchema(new Relation[]{relationS, relationT}, fds);

        // FD: C -> D
        FunctionalDependency fd1 = FunctionalDependency.createFd(db, relationT, new String[]{"C"}, new String[]{"D"});
        fds.add(fd1);

        // Now the query:
        HashSet<RelationAttribute> head = new HashSet<RelationAttribute>();
        head.add(relationS.getAttrByName("A"));

        QuerySelection selectS = new QuerySelection(relationS, new HashSet<String>(Arrays.asList("A", "B")));
        QuerySelection selectT = new QuerySelection(relationT, new HashSet<String>(Arrays.asList("C", "D")));
        QueryJoin joinSandT = new QueryJoin(relationS.getAttrByName("B"), relationT.getAttrByName("C"));
        List<Literal> body = Arrays.asList(selectS, selectT, joinSandT);
        Query q = new Query(db, "shoki", head, body);
        System.out.println(q.toString());
        RAExpression out = SafePlan.buildSafePlan(q);
        System.out.println(out.toLatex());
        System.out.println();
    }

}

