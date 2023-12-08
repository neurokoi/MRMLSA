package py.una.fp.eon.trafico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;

import py.una.fp.eon.core.Network;
import py.una.fp.eon.trafico.property.ApplicationParams;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {
		ApplicationParams params = new ApplicationParams();
		params.init();

		GenerarTrafico<String, DefaultWeightedEdge> generadorBusiness = new GenerarTrafico<>();
		Integer cantidadRequest = Integer.parseInt(params.getText("trafico.group.request.number"));
		Integer cantidadGrupo = Integer.parseInt(params.getText("trafico.group.number"));
		Integer nroDestino = 3;
		Integer capacityFrom = Integer.parseInt(params.getText("trafico.request.capacity.min"));
		Integer capacityTo = Integer.parseInt(params.getText("trafico.request.capacity.max"));
		Integer times = Integer.parseInt(params.getText("trafico.request.secuencia.times.max"));
		String pathSecuencia = params.getText("trafico.path.secuencia");
		List<String> networksName = new ArrayList<>(Arrays.asList(params.getText("trafico.filename.network")));
		Boolean distintasSecuencias = Boolean.valueOf(params.getText("trafico.request.secuencia.distintas"));
		if (distintasSecuencias && factorial(cantidadRequest) < times) {
			times = factorial(cantidadRequest);
			System.out.println("Redefinición Numero de secuencias: "+ times);
		}
		for (String name : networksName) {
			String pathNetwork = params.getText("trafico.path.network");
			Network network = new Network(pathNetwork, name);

			System.out.println(network.getGraph().toString());
			generadorBusiness.generar(network.getGraph(), cantidadRequest, cantidadGrupo, nroDestino, capacityFrom,
					capacityTo, times, pathSecuencia, network.getFileName(), distintasSecuencias);
		}

	}

	private static int factorial(Integer numero) {
		if (numero == 0)
			return 1;
		else
			return numero * factorial(numero - 1);
	}
}
