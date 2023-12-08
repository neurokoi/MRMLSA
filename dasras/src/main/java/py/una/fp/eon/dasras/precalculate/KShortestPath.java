package py.una.fp.eon.dasras.precalculate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import py.una.fp.eon.core.Network;
import py.una.fp.eon.core.ShortestPath;
import py.una.fp.eon.dasras.algorithm.extras.Yen;
import py.una.fp.eon.trafico.property.ApplicationParams;

public class KShortestPath {
	public static Map<String, List<List<DefaultWeightedEdge>>> generate(Network network, int k) {
		ApplicationParams params = new ApplicationParams();
		params.init();
		Set<String> vertexSet = network.getGraph().vertexSet();
		List<String> verticeList = new ArrayList<String>(vertexSet);
		Map<String, List<List<DefaultWeightedEdge>>> kshortest = new HashMap<>();
		int i = 0;
		Yen<String, DefaultWeightedEdge> yenAlg = new Yen<String, DefaultWeightedEdge>(network.getGraph());
		List<ShortestPath> shortestPathList = new ArrayList<>();
		for (String origen : verticeList) {
			List<String> temp = new ArrayList<>(vertexSet);
			temp.remove(i);
			for (String destino : temp) {
				if (!kshortest.containsKey(origen + "_" + destino)) {
					List<List<DefaultWeightedEdge>> pathsource = yenAlg.getShortestPaths(origen, destino, k);
					kshortest.put(origen + "_" + destino, pathsource);
					ShortestPath shortestPath = new ShortestPath(origen, destino, pathsource);
					shortestPathList.add(shortestPath);
				}
				if (!kshortest.containsKey(destino + "_" + origen)) {
					List<List<DefaultWeightedEdge>> pathdest = yenAlg.getShortestPaths(destino, origen, k);
					kshortest.put(destino + "_" + origen, pathdest);
					ShortestPath shortestPath = new ShortestPath(destino, origen, pathdest);
					shortestPathList.add(shortestPath);
				}
			}
			i = i + 1;
		}
		String path = params.getText("trafico.path.shortest");
		writeResult(path, kshortest, network.getGraph(), network.getFileName());
		writeResult(path, network, shortestPathList);
		return kshortest;
	}

	private static void writeResult(String path, Map<String, List<List<DefaultWeightedEdge>>> shortestPaths,
			Graph<String, DefaultWeightedEdge> graph, String networkName) {
		for (String key : shortestPaths.keySet()) {
			FileWriter fw = openFile(path, key, networkName);
			try {
				int k = 0;
				for (List<DefaultWeightedEdge> sps : shortestPaths.get(key)) {
					k++;
					fw.write(k + "\n");
					fw.write(sps.toString() + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				closeFile(fw);
			}
		}
	}

	private static void writeResult(String path, Network network, List<ShortestPath> shortestPathList) {
		FileWriter fw = openFile(path, network.getFileName(), network.getFileName());
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			for (ShortestPath sp : shortestPathList) {
				String grupoInString = mapper.writeValueAsString(sp);
				fw.write(grupoInString);
				fw.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeFile(fw);
		}
	}

	private static FileWriter openFile(String pathN, String key, String networkName) {
		String path;
		// = new java.io.File("").getAbsolutePath();
		path = pathN + networkName + "_sp_" + key;
		FileWriter fw = null;
		try {
			fw = new FileWriter(path, false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fw;
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

	public static Map<String, List<List<DefaultWeightedEdge>>> load(Network network) {
		ApplicationParams params = new ApplicationParams();
		params.init();

		String line;
		Map<String, List<List<DefaultWeightedEdge>>> spMap = new HashMap<>();
		BufferedReader bufferreader = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			String path = (String) new java.io.File("").getAbsolutePath();
			path = params.getText("trafico.path.shortest") + network.getFileName() + "_sp_" + network.getFileName();
			bufferreader = new BufferedReader(new FileReader(path));
			line = bufferreader.readLine();
			while (line != null) {
				// do whatever here
				ShortestPath shortestPath = mapper.readValue(line, ShortestPath.class);
				String key = shortestPath.getSource() + "_" + shortestPath.getTarget();
				spMap.put(key, shortestPath.getkShortestPath());
				line = bufferreader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferreader != null) {
				try {
					bufferreader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return spMap;
	}

}
