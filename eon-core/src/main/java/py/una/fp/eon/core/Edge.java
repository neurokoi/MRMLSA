package py.una.fp.eon.core;

public class Edge<V> {
	private V source;
	private V target;
	private double weight;

	public Edge() {
		super();
	}

	public Edge(V source, V target, double weight) {
		super();
		this.source = source;
		this.target = target;
		this.weight = weight;
	}

	public V getSource() {
		return source;
	}

	public V getTarget() {
		return target;
	}

	public double getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return "{source:" + source + ", target:" + target + ", weight:" + weight + "}";
	}

	public String edgeToString() {
		return "(" + this.getSource() + " : " + this.getTarget() + ")";
	}

}
