package py.una.fp.eon.trafico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.map.HashedMap;
import org.jgrapht.Graph;

import py.una.fp.eon.core.Grupo;
import py.una.fp.eon.core.Solicitud;

public class GrupoTrafico<V, E> {

	Map<String, Solicitud<V>> maprequest = new HashedMap<>();

	public List<Grupo<V>> generarGrupos(Graph<V, E> graph, Integer cantidadRequest, Integer cantidadGrupo,
			Integer nroDestino, Integer capacityFrom, Integer CapacityTo) {
		List<Grupo<V>> grupos = new ArrayList<>();

		int i = 1;
		while (i <= cantidadGrupo) {
			grupos.add(generarRequest(graph, i, cantidadRequest, nroDestino, capacityFrom, CapacityTo));
			i++;
		}
		return grupos;
	}

        // Hoy trabaja para 3 destinos
	private Grupo<V> generarRequest(Graph<V, E> graph, Integer nroGrupo, Integer nroRequest, Integer nroDestino,
			Integer capacityFrom, Integer CapacityTo) {
		Set<V> vertices = graph.vertexSet();
		List<V> VerticeList = new ArrayList<V>(vertices);
		Integer cantidad = vertices.size();

		List<Solicitud<V>> solicitudes = new ArrayList<>();

		int i = 1;
		while (i <= nroRequest) {
			int sourcepos = (int) Math.floor(Math.random() * cantidad);

			int destUnopos = (int) Math.floor(Math.random() * cantidad);
			int destDospos = (int) Math.floor(Math.random() * cantidad);
			int destTrespos = (int) Math.floor(Math.random() * cantidad);
			// Math.floor(Math.random()*(N-M+1)+M); // Valor entre M y N, ambos
			// incluidos.
			Integer capacidad = (int) Math.floor(Math.random() * (CapacityTo - capacityFrom + 1) + capacityFrom);

			while (sourcepos == destUnopos || sourcepos == destDospos || sourcepos == destTrespos) {
				sourcepos = (int) Math.floor(Math.random() * cantidad);
			}
			while (destUnopos == destDospos || destTrespos == destDospos || sourcepos == destDospos) {
				destDospos = (int) Math.floor(Math.random() * cantidad);
			}
			while (destUnopos == destTrespos || destTrespos == destDospos || sourcepos == destTrespos) {
				destTrespos = (int) Math.floor(Math.random() * cantidad);
			}

			List<Integer> destinosPos = new ArrayList<>();
			destinosPos.add(destUnopos);
			destinosPos.add(destDospos);
			destinosPos.add(destTrespos);
			Collections.sort(destinosPos);
			String key = sourcepos + "d" + destinosPos.get(0).toString() + "d" + destinosPos.get(1) + "d"
					+ destinosPos.get(2) + "c" + capacidad;
			if (!maprequest.containsKey(key)) {
				List<V> target = new ArrayList<>();
				target.add(VerticeList.get(destUnopos));
				target.add(VerticeList.get(destDospos));
				target.add(VerticeList.get(destTrespos));
				Solicitud<V> solicitud = new Solicitud<>(VerticeList.get(sourcepos), target, capacidad.doubleValue());
				maprequest.put(key, solicitud);
				solicitudes.add(solicitud);
				i++; // se busca el siguiente
			}
		}
		Grupo<V> grupo = new Grupo<V>(nroGrupo, solicitudes);

		return grupo;
	}

}
