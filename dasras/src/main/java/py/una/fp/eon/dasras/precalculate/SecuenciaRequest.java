package py.una.fp.eon.dasras.precalculate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.map.HashedMap;
import org.jgrapht.Graph;

import py.una.fp.eon.core.Solicitud;
import py.una.fp.eon.core.utils.Modulation;

//fijarse en trafico ya esta implementado la generacion del trafico con la mezcla
@Deprecated
public class SecuenciaRequest<V, E> {

	public List<Solicitud<V>> generate(Graph<V, E> graph, Integer capacityTo, Integer CapacityFrom) {
		Set<V> vertices = graph.vertexSet();
		List<V> VerticeList = new ArrayList<V>(vertices);
		Integer cantidad = vertices.size();
		List<Solicitud<V>> solicitudes = new ArrayList<>();
		Map<String, Solicitud<V>> maprequest = new HashedMap<>();
		int i = 1;
		Integer cap = capacityTo;
		while (i <= 50) {
			int sourcepos = (int) Math.floor(Math.random() * cantidad);
			int destUnopos = (int) Math.floor(Math.random() * cantidad);
			int destDospos = (int) Math.floor(Math.random() * cantidad);
			int destTrespos = (int) Math.floor(Math.random() * cantidad);

			while (sourcepos == destUnopos || sourcepos == destDospos || sourcepos == destTrespos) {
				sourcepos = (int) Math.floor(Math.random() * cantidad);
			}
			while (destUnopos == destDospos || destTrespos == destDospos || sourcepos == destDospos) {
				destDospos = (int) Math.floor(Math.random() * cantidad);
			}
			while (destUnopos == destTrespos || destTrespos == destDospos || sourcepos == destTrespos) {
				destTrespos = (int) Math.floor(Math.random() * cantidad);
			}

			List<Integer> destinos = new ArrayList<>();
			destinos.add(destUnopos);
			destinos.add(destDospos);
			destinos.add(destTrespos);
			Collections.sort(destinos);
			String key = sourcepos + destinos.get(0).toString() + destinos.get(1) + destinos.get(2);
			if (!maprequest.containsKey(key)) {
				List<V> target = new ArrayList<>();
				target.add(VerticeList.get(destUnopos));
				target.add(VerticeList.get(destDospos));
				target.add(VerticeList.get(destTrespos));
				Solicitud<V> solicitud = new Solicitud<>(VerticeList.get(sourcepos), target, cap.doubleValue());
				maprequest.put(key, solicitud);
				solicitudes.add(solicitud);
				i++; // se busca el siguiente
				cap = cap + 4; // la capacidad para el siguiente
			}
		}
		return solicitudes;
	}

	/**
	 * Método que se encarga de mezclar 1000 veces la secuencia de solicitudes.
	 * Se generan dos numeros aleatorios, las cuales indican la posición de las
	 * solicitudes a analizar.
	 * 
	 * @param solicitudes
	 * @param capacity
	 * @param guardBand
	 */
	public List<Solicitud<V>> shuffle(List<Solicitud<V>> solicitudes, double capacity, int guardBand, int times) {
		Solicitud<V> temp;
		int size = solicitudes.size();
		for (int x = 1; x <= times; x++) {
			int ramd1 = (int) Math.floor(Math.random() * size);
			int ramd2 = (int) Math.floor(Math.random() * size);
			int solicitudBWUno = solicitudes.get(ramd1).calculateBW(capacity, guardBand, Modulation.NRO_BPSK);
			int solicitudBWDos = solicitudes.get(ramd2).calculateBW(capacity, guardBand, Modulation.NRO_BPSK);
			if (solicitudBWUno <= solicitudBWDos) {
				if (ramd2 < ramd1) {
					temp = solicitudes.get(ramd2);
					solicitudes.set(ramd2, solicitudes.get(ramd1));
					solicitudes.set(ramd1, temp);
				}
			} else {
				if (ramd1 < ramd2) {
					temp = solicitudes.get(ramd1);
					solicitudes.set(ramd1, solicitudes.get(ramd2));
					solicitudes.set(ramd2, temp);
				}
			}
		}
		return solicitudes;
	}

}
