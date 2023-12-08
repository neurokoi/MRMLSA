package py.una.fp.eon.core;

import java.util.List;

public class GroupResult<V, E> {
	private SummaryTotal summary;
	private List<SubtreeGroupResult<V, E>> subtreeGroupResult;

	public GroupResult() {
		super();
	}

	public GroupResult(SummaryTotal summary, List<SubtreeGroupResult<V, E>> subtreeGroupResult) {
		super();
		this.summary = summary;
		this.subtreeGroupResult = subtreeGroupResult;
	}

	public SummaryTotal getSummary() {
		return summary;
	}

	public List<SubtreeGroupResult<V, E>> getSubtreeGroupResult() {
		return subtreeGroupResult;
	}

}
