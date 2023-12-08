package py.una.fp.eon.core;

import java.util.List;

public class Grupo<V> {
	private Integer nro;
	private List<Solicitud<V>> solicitudes;
	private List<Secuencia> shuffledSequence;
        private List<Secuencia> geneticSequence;

	public Grupo() {
		super();
	}

	public Grupo(Integer nro, List<Solicitud<V>> solicitudes) {
		super();
		this.nro = nro;
		this.solicitudes = solicitudes;
	}

	public List<Solicitud<V>> getSolicitudes() {
		return solicitudes;
	}

	public void setSolicitudes(List<Solicitud<V>> solicitudes) {
		this.solicitudes = solicitudes;
	}

	@Override
	public String toString() {
		return "{nro:" + nro + ", solicitudes:" + solicitudes + "}";
	}

	public Integer getNro() {
		return nro;
	}

	public void setNro(Integer nro) {
		this.nro = nro;
	}

	public List<Secuencia> getShuffledSequence() {
		return shuffledSequence;
	}
        
        public List<Secuencia> getGeneticSequence() {
		return geneticSequence;
	}

	public void setShuffledSequence(List<Secuencia> secuencia) {
		this.shuffledSequence = secuencia;
	}
        
	public void setGeneticSequence(List<Secuencia> secuencia) {
		this.geneticSequence = secuencia;
	}
}
