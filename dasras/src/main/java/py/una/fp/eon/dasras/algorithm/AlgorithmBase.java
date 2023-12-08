package py.una.fp.eon.dasras.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import py.una.fp.eon.core.FrecuencySlot;
import py.una.fp.eon.core.ShortestPathFM;
import py.una.fp.eon.core.Solicitud;
import py.una.fp.eon.core.Subtree;
import py.una.fp.eon.core.utils.Modulation;

/**
 * <pre>
 * 1: Given a multicast request r (s, D, C)
 * 2: for m=4 to 1 do
 * 3: 	calculate the required bandwidth BW for m using (1)
 * 4:	for f=1 to (F-BW+1) do
 * 5:		for each destination d in D do
 * 6:			find the first feasible path pk from Psd with (f, m)
 * 7:			if pk can be found then
 * 8:				add pk into path set O
 * 9:			else
 * 10:				clear path set O and break
 * 11:			end if
 * 12:		end for
 * 13:		if |O| = |D| then
 * 14:			merge the paths in set O into a tree t
 * 15:			allocate FSs (f to f+BW-1) to t and return
 * 16:		end if
 * 17:	end for
 * 18: end for
 * 19: block multicast request r
 * </pre>
 * 
 * @author evazquez
 *
 */
public abstract class AlgorithmBase<V, E> {
	protected Graph<V, E> graph;
	protected Map<String, List<FrecuencySlot<V>>> frecuencySlots;
	protected Solicitud<V> request;
	protected double capacityNetwork;
	protected int guardBand;
	protected int countfs;
	protected Integer indexFS;

	protected Subtree<V, E> subtree;

	public AlgorithmBase(Graph<V, E> graph, Map<String, List<FrecuencySlot<V>>> frecuencySlots, Solicitud<V> request,
			double capacityNetwork, int guardBand, int countfs) {
		super();
		this.graph = graph;
		this.frecuencySlots = frecuencySlots;
		this.request = new Solicitud<V>(request.getSource(), request.getTarget(), request.getCapRequired());
		this.capacityNetwork = capacityNetwork;
		this.guardBand = guardBand;
		this.countfs = countfs;
		this.subtree = null;
		this.indexFS = -1;
	}

	public Graph<V, E> getGraph() {
		return graph;
	}

	public Map<String, List<FrecuencySlot<V>>> getFrecuencySlots() {
		return frecuencySlots;
	}

	public Solicitud<V> getRequest() {
		return request;
	}

	public Subtree<V, E> getSubtree() {
		return subtree;
	}

	public int getCountfs() {
		return countfs;
	}

	/**
	 * 
	 * @param frecuencySlots
	 * @param shortestPaths
	 */
	public abstract void calculate(Map<String, List<List<E>>> shortestPaths, Integer highestIndexFS);

	/**
	 * Condiciones especiales:
	 * <ul>
	 * <li>Cuando todos los caminos tienen al menos un enlace que no cumple la
	 * condicion de la modulacion, se retorna un indicador de que la causa fue
	 * el no cumpliemiento de modulacion</li>
	 * </ul>
	 * 
	 * @param shortestPaths
	 * @param modulation
	 * @param fromFS
	 *            indica la posición donde debe iniciar
	 * @param BW
	 * @return
	 */
	protected ShortestPathFM<E> findShortestPathByFM(List<List<E>> shortestPaths, int modulation, int fromFS,
			double BW) {
		boolean cumple_modulation = true;
		boolean cumple_fs_libre = true;
		ShortestPathFM<E> shortestPath;
		List<Boolean> allPathsModulationCondition = Arrays.asList(new Boolean[shortestPaths.size()]);
		int pos = 0;
		for (List<E> sp : shortestPaths) {
			cumple_modulation = true;
			cumple_fs_libre = true;
			for (E edge : sp) {
				if (!Modulation.isApropiateModul(modulation, this.graph.getEdgeWeight(edge))) {
					cumple_modulation = false;
					allPathsModulationCondition.set(pos, cumple_modulation);
					break;// romper for del path analizado
				}
				if ((fromFS + BW) >= countfs) {
					cumple_fs_libre = false;
					System.out.println("Cantidad de fs requerida sobrepasa");
					break;// romper for del path analizado
				}
				// estado de FS -> NULL SIN UTILIZAR , contiene el rqequest caso
				// contrario
				for (int i = fromFS; i < (fromFS + BW); i++) {
					if (frecuencySlots.get(edge.toString()) != null
							&& frecuencySlots.get(edge.toString()).get(i) != null) {
						cumple_fs_libre = false;
						break; // rompe for de FS del enlace
					}
				}
				if (!cumple_fs_libre) {
					break; // romper for del path analizado
				}
			}
			if (cumple_fs_libre && cumple_modulation) {
				shortestPath = new ShortestPathFM<>(sp, !cumple_modulation);
				return shortestPath;
			}
			pos++;
		}
		if (allPathsModulationCondition.contains(true) || allPathsModulationCondition.contains(null)) {
			return null;
		}
		// porque todos los path no cumplen la condicion de modulación
		return new ShortestPathFM<>(null, true);
	}

	/**
	 * merge the paths in set O into a tree t
	 * 
	 * @param paths
	 * @param fromFS
	 * @param toFS
	 * @param modulation
	 */
	@SuppressWarnings("unchecked")
	protected void generateSubtree(List<List<E>> paths, int fromFS, int toFS, int modulation, Solicitud<V> request) {
		Graph<V, E> newSubtree = (Graph<V, E>) new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		for (List<E> path : paths) {
			for (E edge : path) {
				if (!newSubtree.containsVertex(this.graph.getEdgeSource(edge)))
					newSubtree.addVertex(this.graph.getEdgeSource(edge));
				if (!newSubtree.containsVertex(this.graph.getEdgeTarget(edge)))
					newSubtree.addVertex(this.graph.getEdgeTarget(edge));
				if (!newSubtree.containsEdge(this.graph.getEdgeSource(edge), this.graph.getEdgeTarget(edge))) {
					DefaultWeightedEdge e1 = (DefaultWeightedEdge) newSubtree.addEdge(this.graph.getEdgeSource(edge),
							this.graph.getEdgeTarget(edge));
					((AbstractBaseGraph<String, DefaultWeightedEdge>) newSubtree).setEdgeWeight(e1,
							this.graph.getEdgeWeight(edge));
					setFrecuencySlotStatus(request, (E) e1, fromFS, toFS);
				}
			}
		}
		this.subtree = new Subtree<V, E>(newSubtree, request, fromFS, toFS, modulation, getMethod());
		this.indexFS = toFS + 1; // sumar uno para
									// considerar el indice
									// desde uno
	}

	public Integer getIndexFS() {
		return indexFS;
	}

	/**
	 * <p>
	 * LR = (hp,p'/Hp) + (hp,p'/Hp') : p and p' do not form loop Hp is the
	 * number of hops for p Hp' is the number of hops for p' hp,p’ is the number
	 * of hops that are shared by p and p’
	 * </p>
	 * <p>
	 * LR = -1 : otherwise
	 * </p>
	 * 
	 * @param modulation
	 * @param capTotal
	 * @param guardBand
	 * @return
	 */
	public double calculateLR(List<E> pathUno, List<E> pathDos) {
		// cuando un path forma con otro path un loop =?ciclo cycle
		if (!formanLoop(pathUno, pathDos)) {
			int hpp = getSaltosComunes(pathUno, pathDos);
			// la cantidad de saltos = la cantidad de enlaces
			// que posee el path Ej: pathUno.size() o pathDos.size()
			return ((double) hpp / (double) pathUno.size()) + ((double) hpp / (double) pathDos.size());
		}
		return -1;
	}

	/**
	 * A partir del path más corto cuenta los enlaces comunes
	 * 
	 * @param pathUno
	 * @param pathDos
	 * @return
	 */
	private int getSaltosComunes(List<E> pathUno, List<E> pathDos) {
		List<String> shortestPath = new ArrayList<>();
		List<String> largePath = new ArrayList<>();
		/* se elige el camino mas corto para el for */
		if (pathUno.size() <= pathDos.size()) {
			shortestPath = convertPath(pathUno);
			largePath = convertPath(pathDos);
		} else {
			shortestPath = convertPath(pathDos);
			largePath = convertPath(pathUno);
		}
		int cantidad = 0;
		for (String edge : shortestPath) {
			if (largePath.contains(edge)) {
				++cantidad;
			}
		}
		return cantidad;
	}

	/**
	 * Verifica que uno de los enlaces del pathUno no tenga el nodo_destino como
	 * parte de los enlaces del pathDos. Es decir debe cumplir la siguiente
	 * condicion:
	 * <ul>
	 * <li>Cuando no sean iguales enlaces: verificar que el nodo_destino del
	 * path_uno no sea igual a ningún nodo del pathDos</li>
	 * </ul>
	 * 
	 * @param pathUno
	 *            camino de un destino, compuesto de enlaces
	 * @param pathDos
	 *            camino de otro destino, compuesto de enlaces.
	 * @return
	 */
	private boolean formanLoop(List<E> pathUno, List<E> pathDos) {
		boolean hasloop = false;
		List<String> pathEdge = convertPath(pathDos);
		for (E edgePU : pathUno) {
			if (!pathEdge.contains(edgePU.toString())) {
				V targetPU = graph.getEdgeTarget(edgePU);
				for (E edgePD : pathDos) {
					boolean isSameEdge = edgePU.toString() == edgePD.toString(); // enlaces
																					// iguales
					if (!isSameEdge) {
						V sourcePD = graph.getEdgeSource(edgePD);
						V targetPD = graph.getEdgeTarget(edgePD);
						boolean isVerticePathPD = (targetPU == sourcePD || targetPU == targetPD);
						if (isVerticePathPD) {
							hasloop = true;
							break; // rompe for del pathDos
						}
					}
				}
			}

		}
		return hasloop;
	}

	private List<String> convertPath(List<E> path) {
		List<String> lista = new ArrayList<>();
		for (E edge : path) {
			lista.add(edge.toString());
		}
		return lista;
	}

	public void setFrecuencySlotStatus(Solicitud<V> req, E edge, Integer from, Integer to) {
		List<FrecuencySlot<V>> fsActual = this.frecuencySlots.get(edge.toString());
		for (int i = from; i <= to; i++) {
			FrecuencySlot<V> stateActual = new FrecuencySlot<>(req);
			fsActual.set(i, stateActual);
		}
		this.frecuencySlots.replace(edge.toString(), fsActual);
	}

	public Boolean isFailed(Boolean isbloquedKSPT, Integer indexKSPT, Integer indexCurrentFS) {
		if (!isbloquedKSPT && indexKSPT.compareTo(indexCurrentFS) > 0) {
			return false;
		}
		return true;
	}

	protected void releaseFrecuencySlot(List<Subtree<V, E>> subtrees) {
		for (Subtree<V, E> subtree : subtrees) {
			for (E edge : subtree.getEdges()) {
				setFrecuencySlotStatus(null, edge, subtree.getFromFS(), subtree.getToFS());
			}
		}
	}

	public abstract String getMethod();

	public abstract String getMethodList();

	public abstract List<? extends Subtree<V, E>> getSubtreeList();

}
