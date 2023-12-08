package py.una.fp.eon.dasras.algorithms;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Before;
import org.junit.Test;

import py.una.fp.eon.core.FrecuencySlot;
import py.una.fp.eon.core.Network;
import py.una.fp.eon.core.Solicitud;
import py.una.fp.eon.dasras.algorithm.KSPTAlgorithm;
import py.una.fp.eon.dasras.precalculate.KShortestPath;
import py.una.fp.eon.trafico.property.ApplicationParams;

public class KSPTAlgorithmTest {

	ApplicationParams params;
	KSPTAlgorithm<String, DefaultWeightedEdge> algorithm;
	KSPTAlgorithm<String, DefaultWeightedEdge> algorithmDos;

	@Before
	public void init() {
		params = new ApplicationParams();
		params.init();

	}

	// {"secuencia":[0,1,2,3,4]}
	// "solicitudes":[{"source":"6","target":["3","4","5"],"capRequired":185.0,"bloqued":false},{"source":"1","target":["4","2","3"],"capRequired":196.0,"bloqued":false},{"source":"1","target":["6","5","4"],"capRequired":19.0,"bloqued":false},{"source":"3","target":["4","5","2"],"capRequired":184.0,"bloqued":false},{"source":"5","target":["6","4","1"],"capRequired":31.0,"bloqued":false}]
	// "source":"1","target":["6","5","4"],"capRequired":19.0
	@Test
	public void testCalculate() {
		String fileName = "networkUSNET";
		String filePath = params.getText("trafico.path.network");
		Network network = new Network(filePath, fileName);
		System.out.println(network.getGraph().toString());

		Map<String, List<List<DefaultWeightedEdge>>> shortestPaths = KShortestPath.load(network);

		Integer slotsSize = Integer.parseInt(params.getText("trafico.fs.size"));
		// Integer gamma =
		// Integer.parseInt(params.getText("trafico.gamma.value"));
		Integer guardBand = Integer.parseInt(params.getText("trafico.guardBand.size"));
		Double capacityNetwork = Double.parseDouble(params.getText("trafico.network.capacity"));
		List<String> target = new ArrayList<>();
		target.add("6");
		target.add("5");
		target.add("4");
		Double capRequired = new Double("19.0");

		Map<String, List<FrecuencySlot<String>>> frecuencySlots = new HashMap<>();
		for (DefaultWeightedEdge de : (Set<DefaultWeightedEdge>) network.getGraph().edgeSet()) {
			List<FrecuencySlot<String>> fs = Arrays.asList(new FrecuencySlot[slotsSize]);
			frecuencySlots.put(de.toString(), fs);
		}

		Solicitud<String> request = new Solicitud<String>("1", target, capRequired);
		algorithm = new KSPTAlgorithm<>(network.getGraph(), frecuencySlots, request, capacityNetwork, guardBand,
				slotsSize);
		algorithm.calculate(shortestPaths, algorithm.getIndexFS());
		System.out.println(algorithm.getSubtree());
		System.out.println(algorithm.getFrecuencySlots());
		System.out.println(algorithm.getIndexFS());

		// {"source":"1","target":["4","5","3"],"capRequired":21.0,"bloqued":false}
		List<String> targetDos = new ArrayList<>();
		targetDos.add("4");
		targetDos.add("5");
		targetDos.add("3");
		Double capRequiredDos = new Double("21.0");

		Solicitud<String> requestDos = new Solicitud<String>("1", targetDos, capRequiredDos);
		Integer current = algorithm.getIndexFS();
		algorithm = new KSPTAlgorithm<>(network.getGraph(), algorithm.getFrecuencySlots(), requestDos,
				capacityNetwork, guardBand, slotsSize);
		algorithm.calculate(shortestPaths, current);
		System.out.println(algorithm.getSubtree());
		System.out.println(algorithm.getFrecuencySlots());

		assertNotNull(algorithm.getSubtree());
	}

}
