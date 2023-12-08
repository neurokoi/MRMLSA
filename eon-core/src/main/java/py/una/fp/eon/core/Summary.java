package py.una.fp.eon.core;

public class Summary extends SummaryGeneral {
	private Integer number; // el grupo 0 es el total
	// private Double highestIndexFS; // un promedio de los X grupos
	// private Double timeRunning; // un promedio del tiempo( las 1000
	// secuencias)
	/*
	 * un promedio de la cantidad de subarboles generados por request multicast
	 */
	// private Double BVTUsage;

	public Summary(Integer number, Double highestIndexFS, Double BVTUsage, Double timeRunning) {
		super(highestIndexFS, BVTUsage, timeRunning);
		this.number = number;
	}
        
	public Summary(Integer number, Double highestIndexFS, Double BVTUsage, Double timeRunning, Integer numberSuccess) {
		super(highestIndexFS, BVTUsage, timeRunning, numberSuccess);
		this.number = number;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer grupo) {
		this.number = grupo;
	}

	@Override
	public String toString() {
		return "{numero:" + number + ", highestIndexFS:" + getHighestIndexFS() + ", timeRunning:" + getTimeRunning()
				+ ", BVTUsage:" + getBvtUsage() + ", numberSuccess:" + getNumberSuccess() + "}";
	}

	@Override
	public String toStringCSV(char separator) {
		return new StringBuilder().append(getNumber()).append(separator).append(getHighestIndexFS()).append(separator)
				.append(getTimeRunning()).append(separator).append(getBvtUsage()).append(separator).append(getNumberSuccess()).toString();
	}
}
