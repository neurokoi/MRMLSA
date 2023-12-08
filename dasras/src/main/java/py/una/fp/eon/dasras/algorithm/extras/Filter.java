package py.una.fp.eon.dasras.algorithm.extras;

import org.jgrapht.Graph;

import com.google.common.base.Function;

public interface Filter<V, E> extends Function<Graph<V,E>, Graph<V,E>> {

}
