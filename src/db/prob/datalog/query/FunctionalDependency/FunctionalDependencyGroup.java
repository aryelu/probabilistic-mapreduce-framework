package db.prob.datalog.query.FunctionalDependency;

import db.prob.datalog.query.Absyn.Schema.DatabaseSchema;
import db.prob.datalog.query.Absyn.Schema.RelationAttribute;
import org.jgrapht.alg.TransitiveClosure;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: arye
 *
 * Contains methods to be used on group of Functional Dependencies:
 * main purpose of this class is to compute it's closures
 */
public class FunctionalDependencyGroup extends IFunctionalDependency implements IFunctionalDependencyOperator {

    private Set<FunctionalDependency> fd_set;

    public FunctionalDependencyGroup(DatabaseSchema schema, Set<FunctionalDependency> fd_set) {
        super(schema);
        this.fd_set = new HashSet<FunctionalDependency>(fd_set);
    }

    @Override
    public Set<FunctionalDependency> reflexivity() {
        Set<FunctionalDependency> fd_reflex = new HashSet<FunctionalDependency>();
        for (FunctionalDependency fd : this.fd_set) {
            fd_reflex.addAll(fd.reflexivity());
        }
        return fd_reflex;
    }

    @Override
    public Set<FunctionalDependency> augmentation() {
        Set<FunctionalDependency> fd_augment = new HashSet<FunctionalDependency>();
        for (FunctionalDependency fd : this.fd_set) {
            fd_augment.addAll(fd.augmentation());
        }
        return fd_augment;
    }

    @Override
    public Set<FunctionalDependency> transitivity() {
        // take all rules and build graph from them.
        // now we can, BFS,DFS, Floyd, or Matrix
        Set<FunctionalDependency> fd_transitive = new HashSet<FunctionalDependency>();
        SimpleDirectedGraph<Set<RelationAttribute>, DefaultEdge> dg =
                new SimpleDirectedGraph<Set<RelationAttribute>, DefaultEdge>(DefaultEdge.class);
        Set<Set<RelationAttribute>> p = this.schema_attr_power();

        for (Set<RelationAttribute> x : p) {
            dg.addVertex(x);
        }
        for (FunctionalDependency fd : this.fd_set) {
            Set<RelationAttribute> left = fd.get_left();
            Set<RelationAttribute> right = fd.get_right();
            if (!left.equals(right)) {
                dg.addEdge(left, right);
            }
        }
        TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(dg);
        Set<DefaultEdge> edgeSet = dg.edgeSet();
        for (DefaultEdge de : edgeSet) {
            Set<RelationAttribute> source = dg.getEdgeSource(de);
            Set<RelationAttribute> target = dg.getEdgeTarget(de);
            fd_transitive.add(new FunctionalDependency(this.get_schema(), source, target));
        }
        return fd_transitive;
    }

    public void f_closure() {
        this.fd_set.addAll(this.reflexivity());
        this.fd_set.addAll(this.augmentation());
        //this.fd_set.addAll(this.transitivity());
    }

    public boolean statisfyAttributeSet(FunctionalDependency wanted_fd) {
        Set<RelationAttribute> result = new HashSet<RelationAttribute>(wanted_fd.get_left());
        Set<RelationAttribute> prev_result = Collections.emptySet();
        boolean found = false;
        while (!prev_result.equals(result) && !found) {
            prev_result = new HashSet<RelationAttribute>(result);
            for (FunctionalDependency fd : this.fd_set) {
                if (result.containsAll(fd.get_left())) {
                    result.addAll(fd.get_right());
                }

            }
            found = result.containsAll(wanted_fd.get_right());
        }
        return found;
    }

    public boolean statisfy(FunctionalDependency fd) {
        this.f_closure();
        return statisfyAttributeSet(fd);
    }

}
