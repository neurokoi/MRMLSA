package py.una.fp.eon.dasras.algorithm.extras;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

/**
 * Basis for all shortest path algorithms.
 * 
 * @author Xiaohua Qin
 * @author Michael Duelli (generics, full rewrite)
 * @since 2009-04-24
 * 
 * @param <V>
 *            The parameter for vertices
 * @param <E>
 *            The parameter for edges
 */
public abstract class ShortestPathAlgorithm<V, E> {
	protected final Graph<V, E> graph;
	protected final DijkstraShortestPath<V, E> dijkstra;
	// protected final Transformer<E, Number> nev;

	protected ShortestPathAlgorithm(Graph<V, E> graph) {
		// Transformer<E, Number> nev
		if (graph == null)
			throw new IllegalArgumentException("No graph given");
		// if (nev == null)
		// throw new IllegalArgumentException();

		this.graph = graph;
		// this.nev = nev;
		this.dijkstra = new DijkstraShortestPath<V, E>(graph);
		// radius - limit on path length, or Double.POSITIVE_INFINITY for
		// unbounded search
	}

}
