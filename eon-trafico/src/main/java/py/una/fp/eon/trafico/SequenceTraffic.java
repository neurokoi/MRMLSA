package py.una.fp.eon.trafico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import py.una.fp.eon.core.Grupo;
import py.una.fp.eon.core.Secuencia;

public class SequenceTraffic<V> {

	// private Integer MAX_NRO_SECUENCIA = 1000;

	private List<Secuencia> secuenciaList = new ArrayList<>();

	public List<Secuencia> getSecuenciaList() {
		return secuenciaList;
	}

	/**
	 * Metodo que se encarga de generar X secuencia de solicitudes del grupo
	 * 
	 * @param grupo
	 */
	public void generarSecuencia(Grupo<String> grupo, Boolean distintaSecuencias, Integer nroSecuencia) {
		secuenciaList = new ArrayList<>();
		Integer c = 1;
		List<Integer> secuencia = inicializarSecuencia(grupo.getSolicitudes().size());
		while (c < nroSecuencia) {
			secuencia = cambiarSecuencia(grupo.getSolicitudes().size(), secuencia);
			Secuencia model = new Secuencia();
			model.setSecuencia(secuencia);
			if (distintaSecuencias) {
				Boolean exist = false;
				for (Secuencia sec : secuenciaList) {
					if (sec.equals(model)) {
						System.out.println(sec + "igual a" + model);
						exist = true;
						break;
					}
				}
				if (!exist) {
					secuenciaList.add(model);
					c++;
				}
			} else {
				secuenciaList.add(model);
				c++;
			}
		}
	}
        
        public void generarPoblacion(Grupo<String> grupo, Boolean distintaSecuencias, Integer nroSecuencia) {
            secuenciaList = new ArrayList<>();
            Integer c = 1;
            List<Integer> secuencia = inicializarSecuencia(grupo.getSolicitudes().size());
            while (c < nroSecuencia) {
                    secuencia = cambiarSecuencia(grupo.getSolicitudes().size(), secuencia);
                    Secuencia model = new Secuencia();
                    model.setSecuencia(secuencia);
                    if (distintaSecuencias) {
                            Boolean exist = false;
                            for (Secuencia sec : secuenciaList) {
                                    if (sec.equals(model)) {
                                            //System.out.println(sec + "igual a" + model);
                                            exist = true;
                                            break;
                                    }
                            }
                            if (!exist) {
                                    secuenciaList.add(model);
                                    c++;
                            }
                    } else {
                            secuenciaList.add(model);
                            c++;
                    }
            }
	}

	private List<Integer> inicializarSecuencia(Integer sizeGrupo) {
		List<Integer> secuencia = new ArrayList<Integer>();
		for (Integer i = 0; i < sizeGrupo; i++) {
			secuencia.add(i);
		}
		Secuencia model = new Secuencia();
		model.setSecuencia(secuencia);
		secuenciaList.add(model);
		return secuencia;
	}

	private List<Integer> cambiarSecuencia(Integer sizeGrupo, List<Integer> orderRequest) {
		List<Integer> lista = new ArrayList<>(orderRequest);
		Collections.shuffle(lista);
		return lista;
	}
}
