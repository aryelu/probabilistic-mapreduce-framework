package db.prob.datalog.query.Absyn;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: arye
 * Date: 29/06/13
 * Time: 13:16
 * To change this template use File | Settings | File Templates.
 */
public class FunctionalDependencyGroup extends IFunctionalDependency implements IFunctionalDependencyOperator {

    private Set<FunctionalDependency> fd_set;

    public FunctionalDependencyGroup(ISchema schema, Set<FunctionalDependency> fd_set) {
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
        DirectedGraph<Set<String>, DefaultEdge> dg =
                new DefaultDirectedGraph<Set<String>, DefaultEdge>(DefaultEdge.class);
        Set<Set<String>> p = this.schema_attr_power();

        for (Set<String> x : p) {
            dg.addVertex(x);
        }
        for (FunctionalDependency fd : this.fd_set) {
            Set<String> left = fd.get_left();
            Set<String> right = fd.get_right();
            dg.addEdge(left, right);
        }
        FloydWarshallShortestPaths<Set<String>, DefaultEdge> fwsp = new FloydWarshallShortestPaths(dg);
        for (Set<String> origin : dg.vertexSet()) {
            List<GraphPath<Set<String>, DefaultEdge>> dest_list = fwsp.getShortestPaths(origin);
            for (GraphPath<Set<String>, DefaultEdge> dest_path : dest_list) {
                Set<String> dest = dest_path.getEndVertex();
                fd_transitive.add(new FunctionalDependency(this.get_schema(), origin, dest));
            }
        }
        return fd_transitive;
    }

    public Set<FunctionalDependency> f_closure() {
        Set<FunctionalDependency> fd_closure_before = Collections.emptySet();
        Set<FunctionalDependency> fd_closure_curr = this.fd_set;
        while (! fd_closure_before.equals(fd_closure_curr)) {
            fd_closure_before = new HashSet<FunctionalDependency>(this.fd_set);
            this.fd_set.addAll(this.reflexivity());
            this.fd_set.addAll(this.augmentation());
            this.fd_set.addAll(this.transitivity());
            fd_closure_curr = this.fd_set;
        }
        return this.fd_set;
    }

}
