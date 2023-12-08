package py.una.fp.eon.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Subtree<V, E> {
	private Solicitud<V> request;
	@JsonIgnore
	private Graph<V, E> graph;
	@JsonIgnore
	private List<E> edges;
	private List<Edge<V>> edgesRep;
	private int fromFS;
	private int toFS;
	private int modulation;
	private Map<V, List<E>> selectedPaths;
	private String method;

	public Subtree() {
		super();
	}

	public Subtree(Graph<V, E> graph, Solicitud<V> request, int fromFS, int toFS, int modulation, String method) {
		this.edges = new ArrayList<>(graph.edgeSet());
		loadEdges(graph);
		this.graph = graph;
		this.fromFS = fromFS;
		this.toFS = toFS;
		this.modulation = modulation;
		this.selectedPaths = null;
		this.method = method;
		this.setRequest(request);
	}

	public Subtree(Graph<V, E> graph, List<E> edges, Solicitud<V> request, int fromFS, int toFS, int modulation,
			String method) {
		super();
		this.edges = edges;
		loadEdges(graph);
		this.request = request;
		this.fromFS = fromFS;
		this.toFS = toFS;
		this.modulation = modulation;
		this.selectedPaths = null;
		this.method = method;
	}

	public Subtree(Graph<V, E> graph, List<E> edges, Solicitud<V> request, int fromFS, int toFS, int modulation,
			String method, Map<V, List<E>> selectedPaths) {
		super();
		this.edges = edges;
		loadEdges(graph);
		this.request = request;
		this.fromFS = fromFS;
		this.toFS = toFS;
		this.modulation = modulation;
		this.selectedPaths = selectedPaths;
		this.method = method;
	}

	public int getFromFS() {
		return fromFS;
	}

	public int getToFS() {
		return toFS;
	}

	public int getModulation() {
		return modulation;
	}

	/**
	 * @return the selectedPath
	 */
	public Map<V, List<E>> getSelectedPaths() {
		return selectedPaths;
	}

	public String getMethod() {
		return method;
	}

	public void addDestino(V destino, List<E> shpDestino) {
		selectedPaths.put(destino, shpDestino);
		this.addDestinoToRequest(destino);
		this.addEdges(shpDestino);
	}

	public Solicitud<V> getRequest() {
		return request;
	}

	public void setRequest(Solicitud<V> request) {
		this.request = request;
	}

	private void addDestinoToRequest(V destino) {
		List<V> targets = request.getTarget();
		targets.add(destino);
		request.setTarget(targets);
	}

	public List<E> getEdges() {
		return edges;
	}

	public List<Edge<V>> getEdgesRep() {
		return edgesRep;
	}

	public Graph<V, E> getGraph() {
		return graph;
	}

	private void loadEdges(Graph<V, E> graph) {
		this.edgesRep = new ArrayList<>();
		for (E edge : graph.edgeSet()) {
			Edge<V> enlace = new Edge<>(graph.getEdgeSource(edge), graph.getEdgeTarget(edge),
					graph.getEdgeWeight(edge));
			this.edgesRep.add(enlace);
		}

	}

	private void addEdges(List<E> shpDestino) {
		for (E edge : shpDestino) {
			if (!this.getEdges().contains(edge)) {
				this.edges.add(edge);
				Edge<V> enlace = new Edge<>(graph.getEdgeSource(edge), graph.getEdgeTarget(edge),
						graph.getEdgeWeight(edge));
				this.edgesRep.add(enlace);
			}

		}
	}

	@Override
	public String toString() {
		return "{request:" + request + ", edgesRep:" + edgesRep + ", fromFS:" + fromFS + ", toFS:" + toFS
				+ ", modulation:" + modulation + ", selectedPaths:" + selectedPaths + ", method:" + method + "}";
	}

}
