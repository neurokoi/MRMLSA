package py.una.fp.eon.core;

import java.util.List;

public class SubtreeSequenceResult<V, E> {
	private Integer sequence;
	private List<FrecuencySlotStatus<V, E>> subtrees;
	private SummaryGeneral summaryGeneral;

	public SubtreeSequenceResult() {
		super();
	}

	public SubtreeSequenceResult(Integer sequence, List<FrecuencySlotStatus<V, E>> subtrees, SummaryGeneral summary) {
		super();
		this.sequence = sequence;
		this.subtrees = subtrees;
		this.summaryGeneral = summary;
	}

	public Integer getSequence() {
		return sequence;
	}

	public List<FrecuencySlotStatus<V, E>> getSubtrees() {
		return subtrees;
	}

	public SummaryGeneral getSummaryGeneral() {
		return summaryGeneral;
	}

	@Override
	public String toString() {
		return "{sequence:" + sequence + ", subtrees:" + subtrees + ", summary:" + summaryGeneral + "}";
	}

}
