package py.una.fp.eon.core;

import java.util.List;

public class ShortestPathFM<E> {
	List<E> shortestPath;
	Boolean breakForModulation;

	public ShortestPathFM(List<E> shortestPath, Boolean breakForModulation) {
		super();
		this.shortestPath = shortestPath;
		this.breakForModulation = breakForModulation;
	}

	public List<E> getShortestPath() {
		return shortestPath;
	}

	public Boolean getBreakForModulation() {
		return breakForModulation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((breakForModulation == null) ? 0 : breakForModulation.hashCode());
		result = prime * result + ((shortestPath == null) ? 0 : shortestPath.hashCode());
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
		ShortestPathFM<E> other = (ShortestPathFM<E>) obj;
		if (breakForModulation == null) {
			if (other.breakForModulation != null)
				return false;
		} else if (!breakForModulation.equals(other.breakForModulation))
			return false;
		if (shortestPath == null) {
			if (other.shortestPath != null)
				return false;
		} else if (!shortestPath.equals(other.shortestPath))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ShortestPathFM [shortestPath=" + shortestPath + ", breakForModulation=" + breakForModulation + "]";
	}
}
