package py.una.fp.eon.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubtreeGroupResult<V, E> {
	private String network;
	private Integer group;
	private List<SubtreeSequenceResult<V, E>> subtreeSequence;

	public SubtreeGroupResult() {
		super();
	}

	public SubtreeGroupResult(String network, Integer group, Map<Integer, SummarySequence<V, E>> subtreeSequenceMap) {
		super();
		this.network = network;
		this.group = group;
		this.loadBySequence(subtreeSequenceMap);
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public Integer getGroup() {
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public List<SubtreeSequenceResult<V, E>> getSubtreeSequence() {
		return subtreeSequence;
	}

	public void setSubtreeSequence(List<SubtreeSequenceResult<V, E>> subtreeSequence) {
		this.subtreeSequence = subtreeSequence;
	}

	private void loadBySequence(Map<Integer, SummarySequence<V, E>> subtreeBySequence) {
		this.subtreeSequence = new ArrayList<>();

		for (Integer numberSq : subtreeBySequence.keySet()) {
			SubtreeSequenceResult<V, E> subtree = new SubtreeSequenceResult<>(numberSq,
					subtreeBySequence.get(numberSq).getStatusFSs(), subtreeBySequence.get(numberSq).getSummaryGeneral());
			subtreeSequence.add(subtree);
		}
	}

	@Override
	public String toString() {
		return "{network:" + network + ", group:" + group + ", subtreeSequence:" + subtreeSequence + "}";
	}

}
