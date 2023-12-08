package py.una.fp.eon.dasras.algorithm.extras;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;

//import org.apache.commons.collections15.Transformer;

/**
 * @author Xiaohua Qin
 * @author Michael Duelli (partial rewrite, generics)
 * 
 * @param <V>
 *            The parameter for vertices
 * @param <E>
 *            The parameter for edges
 */
public abstract class KShortestPathAlgorithm<V, E> extends ShortestPathAlgorithm<V, E> {
	protected KShortestPathAlgorithm(Graph<V, E> graph) {
		// , Transformer<E, Number> nev
		super(graph);
	}

	/**
	 * @param source
	 *            The source
	 * @param target
	 *            The destination
	 * @param k
	 *            The number of shortest paths to calculate
	 * @return a list with up to <code>k</code> shortest paths from source to
	 *         target or an EMPTY list if target is not reachable from source
	 */
	protected abstract List<List<E>> getShortestPathsIntern(final V source, final V target, int k);

	/**
	 * @param source
	 *            The source
	 * @param target
	 *            The destination
	 * @param k
	 *            The number of shortest paths to calculate
	 * @return a list with up to <code>k</code> shortest paths from source to
	 *         target or an EMPTY list if target is not reachable from source
	 */
	public final List<List<E>> getShortestPaths(final V source, final V target, int k) {
		if (k < 1)
			throw new AssertionError();

		if (check(source, target))
			return new ArrayList<List<E>>();

		// Let concrete implementation calculate paths.
		return getShortestPathsIntern(source, target, k);
	}

	/** Test whether the source is equal to the target */
	private boolean check(V source, V target) {
		if (!graph.containsVertex(source) || !graph.containsVertex(target))
			throw new AssertionError("The source or the target node does not exist!");

		return source.equals(target);
	}

	/**
	 * This can be specialized in a sub-class to introduce on-the-fly
	 * post-filtering of computed paths.
	 * 
	 * @param found_paths
	 *            The list of found paths
	 * @param curPath
	 *            The path to be validated
	 */
	protected boolean addValidPath(List<List<E>> found_paths, List<E> curPath) {
		found_paths.add(curPath);
		return true;
	}
}
