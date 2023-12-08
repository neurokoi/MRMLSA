package py.una.fp.eon.trafico;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.jgrapht.Graph;

import com.fasterxml.jackson.databind.ObjectMapper;

import py.una.fp.eon.core.Grupo;
import py.una.fp.eon.core.Secuencia;

@ApplicationScoped
public class GenerarTrafico<V, E> {

	private GrupoTrafico<V, E> grupoBusiness = new GrupoTrafico<V, E>();

    /**
     * 
     * @param graph
     *            representa la red
     * @param cantidadRequest
     *            indica el número de solicitudes por grupo.
     * @param cantidadGrupo
     *            indica el número de grupo de solicitudes.
     * @param nroDestino
     *            indica la cantidad de destinos.
     * @param capacityFrom
     *            capacidad minima para las solicitudes.
     * @param CapacityTo
     *            capacidad máxima para las solicitudes.
     * @return 
     */
    public List<Grupo<V>> generar(Graph<V, E> graph, Integer cantidadRequest, Integer cantidadGrupo, Integer nroDestino,
                    Integer capacityFrom, Integer CapacityTo, Integer times, String pathN, String networkName,
                    Boolean distintaSecuencias) {

        // Genera solicitudes
        List<Grupo<V>> grupos = grupoBusiness.generarGrupos(graph, cantidadRequest, cantidadGrupo, nroDestino,
                        capacityFrom, CapacityTo);
        return grupos;
    }
    
    /*** Generar secuencias de manera aleatoria
    public List<Secuencia> generarShuffle(Grupo<V> grupo,  Boolean distintaSecuencias, Integer times) {
        SequenceTraffic<V> secuenciaBusiness = new SequenceTraffic<>();
        secuenciaBusiness.generarSecuencia(grupo, distintaSecuencias, times);
        return secuenciaBusiness.getSecuenciaList();
    }***/
    


	private void writeResult(Grupo<V> grupo, String path, String networkName, Integer cantidadRequest) {
		FileWriter fw = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			fw = openFile(grupo, path, networkName, cantidadRequest);
			String grupoInString = mapper.writeValueAsString(grupo);
			fw.write(grupoInString + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeFile(fw);
		}
	}

	private static void closeFile(FileWriter fw) {
		if (null != fw) {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private FileWriter openFile(Grupo<V> grupo, String pathN, String networkName, Integer cantidadRequest) {
		String path = pathN + networkName + "_grupo_" + grupo.getNro() + "_nrorequest_" + cantidadRequest;

		try {
			return new FileWriter(path, false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
