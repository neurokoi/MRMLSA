package py.una.fp.eon.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Secuencia{
	//private List<Integer> secuencia = Arrays.asList(0, 0, 0, 0, 0);
            
        private List<Integer> secuencia = new ArrayList<>();

	public Secuencia(int tamano) {
		super();
                for (int i=0; i < tamano;i++){
                    secuencia.add(-1);
                }       
	}
        
        public Secuencia() {
		super();      
	}
        
        // Equivalente a GetGene
        public Integer getValue(Integer i) {
            return secuencia.get(i);
        }
        
        // Equivalente a setGene
        public void setValue(Integer i, Integer value) {
            secuencia.set(i, value);
        }
        
        public boolean containSecuencia(Integer i) {
            return secuencia.contains(i);
        }
        
        public Integer size() {
            return secuencia.size();
        }

	public List<Integer> getSecuencia() {
		return secuencia;
	}

	public void setSecuencia(List<Integer> secuencia) {
		this.secuencia = secuencia;
	}

	@Override
	public String toString() {
		return secuencia.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((secuencia == null) ? 0 : secuencia.hashCode());
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
		Secuencia other = (Secuencia) obj;
		if (secuencia == null) {
			if (other.secuencia != null)
				return false;
		} else if (!secuencia.equals(other.secuencia))
			return false;
		return true;
	}
	
}
