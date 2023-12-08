package py.una.fp.eon.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class Network {

	private static FileReader fr = null;
	private String pathFile;
	private String fileName;
	private SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> graph;

        public String getFilename() {
            return fileName;
	}
        
	public Network(String path, String fileName) {
		this.pathFile = path;
		this.fileName = fileName;
		loadfromfile();
	}

	public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> getGraph() {
		return graph;
	}

	/**
	 * Método que se encarga de generar la red atraves de un archivo.
	 */
	private void loadfromfile() {
		graph = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		// Apertura del fichero y creacion de BufferedReader para poder
		// hacer una lectura comoda (disponer del metodo readLine()).
		// "/src/main/" +
		BufferedReader br = openFile(pathFile + fileName);
		// Lectura del fichero
		String linea;
		try {
			while ((linea = br.readLine()) != null) {
				String[] split = linea.split("\t");
				if (!this.graph.containsVertex(split[0]))
					this.graph.addVertex(split[0]);
				if (!this.graph.containsVertex(split[1]))
					this.graph.addVertex(split[1]);
				DefaultWeightedEdge e1 = graph.addEdge(split[0], split[1]);
				this.graph.setEdgeWeight(e1, Double.parseDouble(split[2]));
				//System.out.println(e1.toString() + " PESO " + graph.getEdgeWeight(e1));

			}
			//System.out.println("##########################-----############################");
			//System.out.println("Migración de PARÁMETROS DE CONEXIÓN finalizada");
		} catch (NumberFormatException | IOException e) {
			System.out.println("Error el archivo de configuraciones: " + pathFile);
			e.printStackTrace();
		} finally {
			closeFile();
		}
	}

	/**
	 * Abre el archivo permisos
	 * 
	 * @return
	 */
	public BufferedReader openFile(String ruta) {
		File archivo = null;
		BufferedReader br = null;

		//System.out.println(new java.io.File("").getAbsolutePath());
		String path = (String) new java.io.File("").getAbsolutePath();
		path = path + ruta;
		archivo = new File(path);
		try {
			fr = new FileReader(archivo);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		br = new BufferedReader(fr);

		return br;
	}

	/**
	 * cierra el fichero
	 */
	public void closeFile() {
		if (null != fr) {
			try {
				fr.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public String getFileName() {
		return fileName;
	}

}
