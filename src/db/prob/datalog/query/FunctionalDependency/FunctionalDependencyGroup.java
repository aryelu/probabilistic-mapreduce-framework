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
 * Created with IntelliJ IDEA.
 * User: arye
 * Date: 29/06/13
 * Time: 13:16
 * To change this template use File | Settings | File Templates.
 */
public class FunctionalDependencyGroup extends IFunctionalDependency implements IFunctionalDependencyOperator {

    private Set<FunctionalDependency> fd_set;

    public FunctionalDependencyGroup(DatabaseSchema schema, Set<FunctionalDependency> fd_set) {
        super(schema);
        this.fd_set = fd_set;
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

    public Set<FunctionalDependency> f_closure() {
        Set<FunctionalDependency> fd_closure_before = Collections.emptySet();
        Set<FunctionalDependency> fd_closure_curr = this.fd_set;
        //TODO remove self refrencing
        while (!fd_closure_before.equals(fd_closure_curr)) {
            fd_closure_before = new HashSet<FunctionalDependency>(this.fd_set);
            this.fd_set.addAll(this.reflexivity());
            this.fd_set.addAll(this.augmentation());
            this.fd_set.addAll(this.transitivity());
            fd_closure_curr = this.fd_set;
        }
        return this.fd_set;
    }

    public boolean statisfy(FunctionalDependency fd) {
        Set<FunctionalDependency> closure = this.f_closure();
        return closure.contains(fd);
    }

}
