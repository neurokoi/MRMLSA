package py.una.fp.eon.core;

public class SummaryGeneral extends Model{
	private Double highestIndexFS; // un promedio
	private Double timeRunning; // un promedio del tiempo
	private Double bvtUsage;
        private Integer numberSuccess;

	public SummaryGeneral() {
		super();
	}

	public SummaryGeneral(Double highestIndexFS, Double bvtUsage, Double timeRunning, Integer numberSuccess) {
		super();
		this.highestIndexFS = highestIndexFS;
		this.timeRunning = timeRunning;
		this.bvtUsage = bvtUsage;
                this.numberSuccess = numberSuccess;
	}
        
        public SummaryGeneral(Double highestIndexFS, Double bvtUsage, Double timeRunning) {
		super();
		this.highestIndexFS = highestIndexFS;
		this.timeRunning = timeRunning;
		this.bvtUsage = bvtUsage;
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
        
        public Integer getNumberSuccess() {
		return numberSuccess;
	}

	@Override
	public String toString() {
		return "{highestIndexFS:" + highestIndexFS + ", timeRunning:" + timeRunning + ", BVTUsage:" + bvtUsage + ", numberSuccess:" + numberSuccess + "}";
	}

}
