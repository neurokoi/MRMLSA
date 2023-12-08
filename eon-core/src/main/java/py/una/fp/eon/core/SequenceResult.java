package py.una.fp.eon.core;

import java.util.ArrayList;
import java.util.List;

    public class SequenceResult<V, E> {
            private List<FrecuencySlotStatus<V, E>> statusFSs;
            private Integer bloquedRequest;
            private Double timeRunning;
            private Integer currentIndexFS;
            private Integer numberSuccess;
            private Double bvtSequence;

            private Integer numeroDeCorrida;
            private Integer numeroDeGrupo;



            public SequenceResult() {
                    super();
                    this.currentIndexFS = -1;
                    this.numberSuccess = 0;
                    this.bvtSequence = new Double(0);
                    this.statusFSs = new ArrayList<>();
                    this.timeRunning = -0.1;
                    this.bloquedRequest = 0;
                    this.numeroDeCorrida = 0; // AGREGADO PARA GA parche
                    this.numeroDeGrupo = 0; // AGREGADO PARA GA parche
            }

	public SequenceResult(List<FrecuencySlotStatus<V, E>> statusFSs, Integer currentIndexFS, Integer numberSuccess,
			Double bvtSequence) {
		super();
		this.statusFSs = statusFSs;
		this.currentIndexFS = currentIndexFS;
		this.numberSuccess = numberSuccess;
		this.bvtSequence = bvtSequence;
	}

	public List<FrecuencySlotStatus<V, E>> getStatusFSs() {
		return statusFSs;
	}
        
        public void setBloquedRequest(Integer value) {
            this.bloquedRequest = value;
        }
        
        public void addBloquedRequest() {
            this.bloquedRequest++;
        }
        
        public Integer getBloquedRequest() {
            return this.bloquedRequest;
        }

	public void setStatusFSs(List<FrecuencySlotStatus<V, E>> statusFSs) {
		this.statusFSs = statusFSs;
	}
        
        // In milisecond
        public void setTimeRunning(Double time) {
            this.timeRunning = time;
	}
        
        public Double getTimeRunning() {
            return this.timeRunning;
	}

	public Integer getCurrentIndexFS() {
		return currentIndexFS;
	}
        
        public Integer getNumeroDeCorrida() {
		return numeroDeCorrida;
	}
        
        public void setNumeroDeCorrida(Integer v) {
		this.numeroDeCorrida =  v;
	}
        
        public Integer getNumeroDeGrupo() {
		return numeroDeGrupo;
	}
        
        public void setNumeroDeGrupo(Integer v) {
		this.numeroDeGrupo =  v;
	}
	public void setCurrentIndexFS(Integer currentIndexFS) {
		this.currentIndexFS = currentIndexFS;
	}

	public Integer getNumberSuccess() {
		return numberSuccess;
	}

	public void setNumberSuccess(Integer numberSuccess) {
		this.numberSuccess = numberSuccess;
	}

	public void addNumberSuccess() {
		this.numberSuccess++;
	}

	public Double getBvtSequence() {
		return bvtSequence;
	}

	public void setBvtSequence(Double bvtSequence) {
		this.bvtSequence = bvtSequence;
	}

	public void addBvtSequence(Double bvtSequence) {
		this.bvtSequence = this.bvtSequence + bvtSequence;
	}

	public void addStatusFSs(FrecuencySlotStatus<V, E> statusFSs) {
		this.statusFSs.add(statusFSs);
	}

	//@Override
	//public String toString() {
	//	return "SequenceResult [statusFSs=" + statusFSs + ", bloquedRequest=" + bloquedRequest + ", timeRunning=" + timeRunning + ", currentIndexFS=" + currentIndexFS + ", numberSuccess="
	//			+ numberSuccess + ", bvtSequence=" + bvtSequence + "]";
	//}
        
        @Override
	public String toString() {
		return "SequenceResult [bloquedRequest=" + bloquedRequest + ", timeRunning=" + timeRunning + ", currentIndexFS=" + currentIndexFS + ", numberSuccess="
				+ numberSuccess + ", bvtSequence=" + bvtSequence + "]";
	}
        
	public String toStringCSV(char separator) {
		return new StringBuilder().append(getStatusFSs()).append(separator).append(getBloquedRequest()).append(separator)
				.append(getTimeRunning()).append(separator).append(getCurrentIndexFS()).append(separator).append(getNumberSuccess()).append(separator).append(getBvtSequence()).toString();
	}
	
}
