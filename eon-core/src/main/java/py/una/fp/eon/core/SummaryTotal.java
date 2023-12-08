package py.una.fp.eon.core;

public class SummaryTotal extends Model implements Comparable<SummaryTotal> {
	private String algoritmo;
	private Integer request;
	private Double highestIndexFS; // un promedio
	private Double timeRunning; // un promedio del tiempo
	private Double bvtUsage;
        private Integer numberSuccess;

	public SummaryTotal() {
		super();
		this.algoritmo = null;
		this.request = null;
		this.highestIndexFS=0d;
		this.timeRunning=0d;
		this.bvtUsage=0d;
                this.numberSuccess = 0;
	}

	public SummaryTotal(String algoritmo, Integer request, Summary summary) {
		super();
		this.algoritmo = algoritmo;
		this.request = request;
                
		this.setData(summary);
	}
        
        public SummaryTotal(String algoritmo, Integer request, Summary summary, Integer numberSuccess) {
		super();
		this.algoritmo = algoritmo;
		this.request = request;
                this.numberSuccess = numberSuccess;
		this.setData(summary);
	}

	private void setData(Summary summary) {
		this.highestIndexFS = summary.getHighestIndexFS();
		this.timeRunning = summary.getTimeRunning();
		this.bvtUsage = summary.getBvtUsage();
                this.numberSuccess = summary.getNumberSuccess();
	}

	public String getAlgoritmo() {
		return algoritmo;
	}
        
        public Integer getNumberSuccess() {
		return numberSuccess;
	}

	public Integer getRequest() {
		return request;
	}

	public Double getHighestIndexFS() {
		return highestIndexFS;
	}

	public Double getTimeRunning() {
		return timeRunning;
	}

	public Double getBvtUsage() {
		return bvtUsage;
	}

	@Override
	public String toStringCSV(char separator) {
		return new StringBuilder().append(getAlgoritmo()).append(separator).append(getRequest()).append(separator)
				.append(getHighestIndexFS()).append(separator).append(getTimeRunning()).append(separator)
				.append(getBvtUsage()).toString();
	}

	@Override
        // Aca compara solo el Highest Index Frequency Slot
	public int compareTo(SummaryTotal summaryOther) {
		if (highestIndexFS < summaryOther.getHighestIndexFS())
			return -1;
		else if (highestIndexFS > summaryOther.getHighestIndexFS())
			return 1;
		return 0;
	}

}
