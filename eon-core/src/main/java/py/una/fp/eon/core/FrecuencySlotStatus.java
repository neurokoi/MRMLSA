package py.una.fp.eon.core;

import java.util.List;

public class FrecuencySlotStatus<V, E> {

	private Solicitud<V> originalrequest;
	private List<Subtree<V, E>> subtrees;
	private String method;

	public FrecuencySlotStatus() {
		super();
	}

	public FrecuencySlotStatus(List<Subtree<V, E>> subtrees, Solicitud<V> originalrequest, String method) {
		super();
		this.subtrees = subtrees;
		this.originalrequest = originalrequest;
		this.method = method;
	}

	public List<Subtree<V, E>> getSubtrees() {
		return subtrees;
	}

	public Solicitud<V> getOriginalrequest() {
		return originalrequest;
	}

	public String getMethod() {
		return method;
	}

	@Override
	public String toString() {
		return "{originalrequest:" + originalrequest + ", subtrees:" + subtrees + ", method:" + method + "}";
	}
}
