package py.una.fp.eon.core;

import java.util.List;

/**
 * Clase que representa a una solictud Multicast.
 * <p>
 * Esta compuesto por:
 * </p>
 * <ul>
 * <li>un origen: desde donde parte</li>
 * <li>varios destinos: hacia donde debe ir, puede ser uno o más.</li>
 * <li>capacidad requerida:</li>
 * <li>bloqueado:indica si la solicitud se encuentra bloqueada (atributo que yo
 * decidi agregar)</li>
 * </ul>
 * 
 * @author evazquez
 *
 */
public class Solicitud<V> {

	private V source;
	private List<V> target;
	private Double capRequired;
	private boolean bloqued = false;

	public Solicitud() {
		super();
	}

	public Solicitud(V source, List<V> target, Double capRequired) {
		super();
		this.source = source;
		this.target = target;
		this.capRequired = capRequired;
		this.setBloqued(false);
	}

	public V getSource() {
		return source;
	}

	public void setSource(V source) {
		this.source = source;
	}

	public List<V> getTarget() {
		return target;
	}

	public void setTarget(List<V> target) {
		this.target = target;
	}

	public Double getCapRequired() {
		return capRequired;
	}

	public void setCapRequired(Double capRequired) {
		this.capRequired = capRequired;
	}

	public boolean isBloqued() {
		return bloqued;
	}

	public void setBloqued(boolean bloqued) {
		this.bloqued = bloqued;
	}

	/**
	 * El numero requerido de FS contiguos para un k path en Psd es f:
	 * 
	 * <pre>
	 * f=redondeo hacia arriba(Cr/(mrd * C)) + GB
	 * Donde:
	 * Cr : capacidad requerida por la solicitud r
	 * mrd: nivel de modulación que el path k 
	 * C: capacidad total de la red
	 * GB: Banda de guarda
	 * </pre>
	 * 
	 * @param modulation
	 * @return
	 */
	public int calculateBW(double capacityNetwork, int guardBand, int modulation) {
		return (int) (Math.ceil(this.getCapRequired() / (modulation * capacityNetwork)) + guardBand);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((capRequired == null) ? 0 : capRequired.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Solicitud other = (Solicitud) obj;
		if (capRequired == null) {
			if (other.capRequired != null)
				return false;
		} else if (!capRequired.equals(other.capRequired))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "{source:" + source + ", target:" + target + ", capRequired:" + capRequired + ", bloqued:"
				+ bloqued + "}";
	}
}
