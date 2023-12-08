package py.una.fp.eon.core;

import java.util.List;

public class SummarySequence<V, E> {
	private List<FrecuencySlotStatus<V, E>> statusFSs;
	private SummaryGeneral summaryGeneral;
        Integer numberSuccess = -1;

	public SummarySequence() {
		super();
	}

	public SummarySequence(List<FrecuencySlotStatus<V, E>> statusFSs, SummaryGeneral summary) {
		super();
		this.statusFSs = statusFSs;
		this.summaryGeneral = summary;
	}
        
        public Integer getNumberSuccess() {
		return numberSuccess;
	}

	public List<FrecuencySlotStatus<V, E>> getStatusFSs() {
		return statusFSs;
	}

	public SummaryGeneral getSummaryGeneral() {
		return summaryGeneral;
	}

	@Override
	public String toString() {
		return "SummarySequence [statusFSs=" + statusFSs + ", summaryGeneral=" + summaryGeneral + "]";
	}

}
