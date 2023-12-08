package py.una.fp.eon.dasras.algorithm.extras;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.google.common.base.Predicate;

public class EdgePredicateFilter<V, E> implements Filter<V, E> {

	protected Predicate<E> edge_pred;

	/**
	 * Creates an instance based on the specified edge <code>Predicate</code>.
	 * 
	 * @param edge_pred
	 *            the predicate that specifies which edges to add to the
	 *            filtered graph
	 */
	public EdgePredicateFilter(Predicate<E> edge_pred) {
		this.edge_pred = edge_pred;
	}

	@SuppressWarnings("unchecked")
	public Graph<V, E> apply(Graph<V, E> g) {
		Graph<V, E> filtered;
		filtered = (Graph<V, E>) new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		for (V v : g.vertexSet())
			filtered.addVertex(v);

		for (E e : g.edgeSet()) {
			if (edge_pred.apply(e))
				filtered.addEdge(g.getEdgeSource(e), g.getEdgeTarget(e), e);
		}

		return filtered;
	}

}
