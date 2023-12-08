package py.una.fp.eon.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SubtreeSequence<V, E> {
	private Summary summary;
	private Map<Integer, SummarySequence<V, E>> subtreeforSequence = new HashMap<>();

	public SubtreeSequence() {
		super();
	}

	public Map<Integer, SummarySequence<V, E>> getSubtreeforSequence() {
		return subtreeforSequence;
	}

	public void setSubtreeforSequence(Map<Integer, SummarySequence<V, E>> subtreeforSequence) {
		this.subtreeforSequence = subtreeforSequence;
	}

	public void add(Integer sequence, SummarySequence<V, E> statusFS) {
		this.subtreeforSequence.put(sequence, statusFS);
	}

	@Override
	public String toString() {
		String sequenceString = "[";

		for (Entry<Integer, SummarySequence<V, E>> entry : subtreeforSequence.entrySet()) {
			sequenceString = sequenceString + "{" + entry.getKey().toString() + ", " + entry.getValue().toString()
					+ "}, ";
		}
		sequenceString = sequenceString + "]";
		return sequenceString;
	}

	public void setSummary(Integer grupo, Double BVTUsage, Double totalRunning, Integer highestIndexFS) {
		this.summary = new Summary(grupo, (double) highestIndexFS, BVTUsage, totalRunning);
	}
        
        public void setSummary(Integer grupo, Double BVTUsage, Double totalRunning, Integer highestIndexFS, Integer numberSuccess) {
		this.summary = new Summary(grupo, (double) highestIndexFS, BVTUsage, totalRunning, numberSuccess);
	}

	public Summary getSummary() {
		return summary;
	}

}
