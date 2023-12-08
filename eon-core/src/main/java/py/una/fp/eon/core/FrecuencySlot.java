package py.una.fp.eon.core;

public class FrecuencySlot<V> {
	Solicitud<V> request;

	public FrecuencySlot() {
		super();
		request = null;
	}

	public FrecuencySlot(Solicitud<V> request) {
		super();
		this.request = request;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((request == null) ? 0 : request.hashCode());
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
		FrecuencySlot<V> other = (FrecuencySlot<V>) obj;
		if (request == null) {
			if (other.request != null)
				return false;
		} else if (!request.equals(other.request))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FrecuencySlot [request=" + request + "]";
	}

}
