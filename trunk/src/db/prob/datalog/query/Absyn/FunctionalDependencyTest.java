package db.prob.datalog.query.Absyn;

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

        ISchema schema = new Relation("bla",schema_,false);

        fd = new FunctionalDependency(schema, left,right);
    }

    @Test
    public void reflex(){
        Set<FunctionalDependency> power = fd.reflexivity();
        System.out.println(power);
    }
    @Test
    public void augment(){
        Set<FunctionalDependency> power = fd.augmentation();
        System.out.println(power);
    }
}
