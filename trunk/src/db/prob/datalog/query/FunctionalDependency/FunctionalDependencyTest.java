package db.prob.datalog.query.FunctionalDependency;

import db.prob.datalog.query.Absyn.ISchema;
import db.prob.datalog.query.Absyn.Relation;
import org.junit.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: arye
 * Date: 29/06/13
 * Time: 10:21
 * To change this template use File | Settings | File Templates.
 */
public class FunctionalDependencyTest {

    private FunctionalDependency fd;
    private FunctionalDependency fd2;
    private FunctionalDependencyGroup fdg;

    @Before
    public void setUp() {
        Set<String> left = new HashSet<String>();
        left.add("a");
        Set<String> right = new HashSet<String>();
        right.add("c");
        Set<String> schema_ = new HashSet<String>();
        schema_.add("a");
        schema_.add("b");
        schema_.add("c");
        schema_.add("d");

        Set<String> left2 = new HashSet<String>();
        left2.add("c");
        Set<String> right2 = new HashSet<String>();
        right2.add("d");
        ISchema schema = new Relation("bla",schema_,false);

        fd = new FunctionalDependency(schema, left,right);
        fd2 = new FunctionalDependency(schema, left2, right2);

        HashSet<FunctionalDependency> fd_set = new HashSet<FunctionalDependency>();
        fd_set.add(fd);
        fd_set.add(fd2);
        fdg = new FunctionalDependencyGroup(schema, fd_set);
    }

    @Test
    public void reflex(){
        Set<FunctionalDependency> power = fd.reflexivity();
    }
    @Test
    public void augment(){
        Set<FunctionalDependency> power = fd.augmentation();
    }
    @Test
    public void closure(){
        Set<FunctionalDependency> power = fdg.f_closure();
    }
}
