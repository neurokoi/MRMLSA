package py.una.fp.eon.core;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;

public class ShortestPath {
	private String source;
	private String target;
	private List<List<DefaultWeightedEdge>> kShortestPath;

	public ShortestPath() {
	}

	public ShortestPath(String source, String target, List<List<DefaultWeightedEdge>> kShortestPath) {
		super();
		this.source = source;
		this.target = target;
		this.kShortestPath = kShortestPath;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public List<List<DefaultWeightedEdge>> getkShortestPath() {
		return kShortestPath;
	}

	public void setkShortestPath(List<List<DefaultWeightedEdge>> kShortestPath) {
		this.kShortestPath = kShortestPath;
	}

	@Override
	public String toString() {
		return "ShortestPath [source=" + source + ", target=" + target + ", kShortestPath=" + kShortestPath + "]";
	}
}
