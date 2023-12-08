package py.una.fp.eon.dasras.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
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
 * 2: run Algorithm 1 for multicast request r
 * 3: if Algorithm 1 fails then
 * 4:	for m=4 to 1 do
 * 5:		calculate the required bandwidth BW for m using (1)
 * 6:		for f=1 to (F-BW+1) do
 * 7:			for each unassigned destination d in D do
 * 8:				find the first feasible path pk from Psd with (f, m)
 * 9:				if pk cannot be found or pk is spectrum overlapped with existing subtrees then
 * 10:					find next pk and goto line 8
 * 11:				end if
 * 12:				for each existing subtree t with modulation level m and starting FS f do
 * 13:					calculate LR between pk and the selected path of every d2 in subtree t
 * 14:					if LR ≥ γ then
 * 15:						assign d to subtree t and select pk for d
 * 16:						reserve the FSs along pk and goto line 7
 * 17:					end if
 * 18:				end for
 * 19:				for each unassigned destination d’≠ d in D do
 * 20:					find the first feasible path pk’ from Psd’ with (f, m)
 * 21:					if pk cannot be found or pk’ is spectrum overlapped with existing subtrees then
 * 22:						find next pk’ and goto line 20
 * 23:					end if
 * 24:					calculate LR between pk and pk’
 * 25:					if LR ≥ γ then
 * 26:						assign d and d’ to a new subtree
 * 27:						select pk for d and pk’ for d’
 * 28:						reserve the FSs along pk, pk’ and goto line 7
 * 29:					end if
 * 30:				end for
 * 31:			end for
 * 32:		end for
 * 33:	end for
 * 34:	allocate reserved FSs for subtrees
 * 35:	allocate the path with minimum FS consumption for every unassigned destination d in D
 * 36:	if d cannot be served then
 * 37:		release all allocated FSs
 * 38:		block multicast request r and return
 * 39:	end if
 * 40: end if
 * </pre>
 * 
 * @author evazquez
 *
 * @param <V>
 *            vertices
 * @param <E>
 *            enlaces
 */
public class SALRGAlgorithm<V, E> extends AlgorithmBase<V, E> {

	private List<Subtree<V, E>> subtreeList;
	private String methodList;
	private double gamma;

	public SALRGAlgorithm(Graph<V, E> graph, Map<String, List<FrecuencySlot<V>>> frecuencySlots, Solicitud<V> request,
			double capacity, int guardBand, int countfs, double gamma) {
		super(graph, frecuencySlots, request, capacity, guardBand, countfs);
		this.subtreeList = new ArrayList<>();
		this.gamma = gamma;
		this.methodList = getMethod();
	}

	public void calculate(Map<String, List<List<E>>> shortestPaths, Integer indexCurrentFS) {
		KSPTAlgorithm<V, E> algorithmKSPT = new KSPTAlgorithm<V, E>(graph, frecuencySlots, request, capacityNetwork,
				guardBand, countfs);
		algorithmKSPT.calculate(shortestPaths, indexCurrentFS);
		if (isFailed(algorithmKSPT.getRequest().isBloqued(), algorithmKSPT.getIndexFS(), indexCurrentFS)) {
			Map<V, V> destinosSinasignar = new HashMap<>();
			Map<V, V> destinosNoAsignados = new HashMap<>();
			Map<V, V> destinosNoAsignadosSubtree = new HashMap<>();
			for (V destino : request.getTarget()) {
				destinosSinasignar.put(destino, destino);
				destinosNoAsignados.put(destino, destino);
				destinosNoAsignadosSubtree.put(destino, destino);
			}
			for (int m = Modulation.NRO_16QAM; m > 0; m--) {
				int BW = this.request.calculateBW(this.capacityNetwork, this.guardBand, m);
				for (int f = 1; f < (countfs - BW + 1); f++) {
					int fromFS = f - 1;
					// for (V destino : destinosSinasignar.keySet()) {
					List<Boolean> allDestinosBreakForMod = Arrays.asList(new Boolean[destinosSinasignar.size()]);
					int pos = 0;
					for (Iterator<V> iterator = destinosSinasignar.keySet().iterator(); iterator.hasNext();) {
						V destino = iterator.next();
						if (destinosNoAsignados.containsKey(destino)) {
							// checkAngGet: find next pk and goto line 8
							ShortestPathFM<E> shpkFM = checkAngGet(destino, shortestPaths, m, fromFS, BW);
							List<E> pk = shpkFM.getShortestPath();
							allDestinosBreakForMod.set(pos, shpkFM.getBreakForModulation());
							if (pk == null || pk.isEmpty()) {
								continue; // {FOR destinosSinAsignar}
							}
							/*---AÑADIR A UN SUBTREE SI ES POSIBLE**/
							// linea 12 al 18
							boolean isaddSubtree = tryAddSubtree(m, fromFS, pk, destino, BW);
							if (isaddSubtree) {
								iterator.remove();
								destinosNoAsignados.remove(destino);
								destinosNoAsignadosSubtree.remove(destino);
								continue; // al sgt destino sin asignar,
											// goto line 7
							}

							// linea 19 al 30
							// destinos sin d
							destinosNoAsignadosSubtree.remove(destino);
							List<V> destinosAsign = new ArrayList<>();
							for (V destinoPrima : destinosNoAsignadosSubtree.keySet()) {
								ShortestPathFM<E> shpkFMDP = checkAngGet(destinoPrima, shortestPaths, m, fromFS, BW);
								List<E> pkdp = shpkFMDP.getShortestPath();
								if (pkdp == null || pk.isEmpty()) {
									continue; // {FOR
												// destinosNoAsignadosSubtree}
								}
								double LR = calculateLR(pk, pkdp);
								if (LR >= gamma) {
									// remueve destino, pertenece a
									// un subtree
									iterator.remove();
									destinosNoAsignados.remove(destino);
									destinosNoAsignados.remove(destinoPrima);

									destinosAsign.add(destino);
									destinosAsign.add(destinoPrima);

									Map<V, List<E>> paths = new HashMap<>();
									paths.put(destino, pk);
									paths.put(destinoPrima, pkdp);
									List<V> targets = new ArrayList<>();
									targets.add(destino);
									targets.add(destinoPrima);
									Subtree<V, E> subtree = generateSubtree(paths, fromFS, BW, m, targets);
									Integer aux = subtree.getToFS() + 1;
									if (this.indexFS.compareTo(aux) < 0)
										this.indexFS = aux;
									this.addSubtreeToList(subtree, this.indexFS);
									break; // GOTO LINE 7: DEBE IR SGT destino
											// no asignado
								}
							}
							for (V destRemover : destinosAsign) {
								destinosNoAsignadosSubtree.remove(destRemover);
							}
							if (destinosAsign.size() == 0) {
								destinosNoAsignadosSubtree.put(destino, destino);
							}
						}
						pos++;
					}
					if (!allDestinosBreakForMod.contains(false) && !allDestinosBreakForMod.contains(null)) {
						break;
					}
				}
			}

			/*
			 * <pre> 34: allocate reserved FSs for subtrees 35: allocate the
			 * path with minimum FS consumption for every unassigned destination
			 * d in D 36: if d cannot be served then 37: release all allocated
			 * FSs 38: block multicast request r and return 39: end if </pre>
			 */
			Integer highestOneDest = this.indexFS;
			for (Iterator<V> iterator = destinosNoAsignados.keySet().iterator(); iterator.hasNext();) {
				V destino = iterator.next();
				Boolean incluidoSubtree = false;
				String key = this.request.getSource() + "_" + destino;
				for (int m = Modulation.NRO_16QAM; m > 0; m--) {
					int BW = this.request.calculateBW(this.capacityNetwork, this.guardBand, m);
					for (int f = 1; f < (countfs - BW + 1); f++) {
						int fromFS = f - 1;
						ShortestPathFM<E> shpFM = this.findShortestPathByFM(shortestPaths.get(key), m, fromFS, BW);
						if (shpFM != null && shpFM.getShortestPath() != null && !shpFM.getShortestPath().isEmpty()) {
							List<V> newDest = new ArrayList<>();
							newDest.add(destino);
							Map<V, List<E>> paths = new HashMap<>();
							paths.put(destino, shpFM.getShortestPath());
							Subtree<V, E> subtree = generateSubtree(paths, fromFS, BW, m, newDest);
							incluidoSubtree = true;
							Integer aux = subtree.getToFS() + 1;
							if (aux.compareTo(highestOneDest) > 0) {
								highestOneDest = aux;
							}
							this.addSubtreeToList(subtree, highestOneDest);
							iterator.remove();
							break;
						}else{
							if (shpFM != null && shpFM.getBreakForModulation()){
								break; //cambie a la siguiente modulacion
							}
						}
					}
					if (incluidoSubtree) {
						break;
					}
				}
				// si uno ya no incluye se rompe el for
				if (!incluidoSubtree) {
					break;
				}
			}
			if (destinosNoAsignados.size() > 0) {
				this.releaseFrecuencySlot(this.subtreeList);
				this.subtreeList = new ArrayList<>();
				this.request.setBloqued(true);
				this.methodList = null;
				this.indexFS = -1;
			} else {
				this.methodList = getMethod();
			}
		} else {
			Subtree<V, E> subtree = new Subtree<>(algorithmKSPT.getSubtree().getGraph(),
					algorithmKSPT.getSubtree().getEdges(), request, algorithmKSPT.getSubtree().getFromFS(),
					algorithmKSPT.getSubtree().getToFS(), algorithmKSPT.getSubtree().getModulation(),
					algorithmKSPT.getMethod(), null);
			this.addSubtreeToList(subtree, algorithmKSPT.getIndexFS());
			this.methodList = algorithmKSPT.getMethod();
			this.request.setBloqued(false);
		}
	}

	private ShortestPathFM<E> checkAngGet(V destino, Map<String, List<List<E>>> shortestPaths, int modulation,
			int fromFS, double BW) {
		String key = this.request.getSource() + "_" + destino;
		List<Boolean> breakByModulation = Arrays.asList(new Boolean[shortestPaths.get(key).size()]);
		int pos = 0;
		List<E> pk = null;
		for (List<E> shp : shortestPaths.get(key)) {
			/* CAMBIE LA CONDICIÓN PARA NO USAR CONTINUE */
			ShortestPathFM<E> shpFM = getShortestPathByFM(shp, modulation, fromFS, BW);
			if (shpFM.getShortestPath() != null && !shpFM.getShortestPath().isEmpty()
					&& shpFM.getShortestPath().size() > 0) {
				if (!overlappedSpectrumSubtree(fromFS, BW)) {
					pk = shpFM.getShortestPath();
					break; // sPath sin solapar espectro
				}
				breakByModulation.set(pos, shpFM.getBreakForModulation());
			} else {
				breakByModulation.set(pos, shpFM.getBreakForModulation());
			}
			pos++;
		}
		if (breakByModulation.contains(false) || breakByModulation.contains(null)) {
			return new ShortestPathFM<>(pk, false);
		}
		// porque todos los path no cumplen la condicion de modulación
		return new ShortestPathFM<>(pk, true);
	}

	/**
	 * <p>
	 * 8: find the first feasible path pk from Psd with (f, m)
	 * </p>
	 * Método que busca la primera ruta factible que cumpla las condiciones:
	 * <ul>
	 * <li>Modulacion</li>
	 * <li>FS disponible</li>
	 * </ul>
	 * 
	 * @param shortestPaths
	 * @param modulation
	 * @param f
	 * @param BW
	 * @return
	 */
	// private boolean isShortestPathByFM(List<E> sp, int modulation, int
	// fromFS, double BW) {
	// boolean estaDisponible = false; // se asume que no cumple inicialmente
	//
	// List<List<E>> newSPS = new ArrayList<>();
	// newSPS.add(sp);
	// ShortestPathFM<E> shpFM = this.findShortestPathByFM(newSPS, modulation,
	// fromFS, BW);
	// if (shpFM != null) {
	// List<E> shortestPath = shpFM.getShortestPath();
	// if (shortestPath != null && !shortestPath.isEmpty() &&
	// shortestPath.size() > 0) {
	// estaDisponible = true;
	// }
	// }
	// return estaDisponible;
	// }

	private ShortestPathFM<E> getShortestPathByFM(List<E> sp, int modulation, int fromFS, double BW) {

		List<List<E>> newSPS = new ArrayList<>();
		newSPS.add(sp);
		ShortestPathFM<E> shpFM = this.findShortestPathByFM(newSPS, modulation, fromFS, BW);
		if (shpFM == null) {
			shpFM = new ShortestPathFM<>(null, false);
		}
		return shpFM;
	}

	/**
	 * OJO:este debo corregir debe ser solapamiento parcial.
	 * <p>
	 * Se corrigió 24/01/2018
	 * </p>
	 * 
	 * @param fromFS
	 * @param toFS
	 * @return
	 */
	private boolean overlappedSpectrumSubtree(int fromFS, double BW) {
		boolean overlap = false;
		int toFS = (int) (fromFS + BW - 1);
		for (Subtree<V, E> sub : subtreeList) {
			// el inicio esta en el rango del otro subtree
			if (fromFS < sub.getFromFS() && toFS >= sub.getFromFS()
					&& (toFS <= sub.getToFS() || toFS > sub.getToFS())) {
				overlap = true;
				break;
			}

			if (fromFS == sub.getFromFS() && (toFS < sub.getToFS() || toFS > sub.getToFS())) {
				overlap = true;
				break;
			}

			if (fromFS > sub.getFromFS() && fromFS <= sub.getToFS()
					&& (toFS <= sub.getToFS() || toFS > sub.getToFS())) {
				overlap = true;
				break;
			}
		}
		return overlap;
	}

	/**
	 * existing subtree t with modulation level m and starting FS f
	 * 
	 * @param modulation
	 * @param fromFS
	 * @return
	 */
	private Map<Integer, Subtree<V, E>> getSubtreeByFM(int modulation, int fromFS) {
		Map<Integer, Subtree<V, E>> subtreeListByFM = new HashMap<>();
		int pos = 0;
		for (Subtree<V, E> subtree : subtreeList) {
			if (subtree.getFromFS() == fromFS && subtree.getModulation() == modulation) {
				subtreeListByFM.put(pos, subtree);
			}
			pos++;
		}

		return subtreeListByFM;

	}

	/**
	 * Método que trata de añadir un nuevo destino a un subtree existente.
	 * 
	 * @param modulation
	 * @param fromFS
	 * @param pk
	 * @param destino
	 * @param BW
	 * @return
	 */
	private Boolean tryAddSubtree(int modulation, int fromFS, List<E> pk, V destino, int BW) {
		boolean cumpleLR = false;
		// key: posicion en la lista del subtree
		Map<Integer, Subtree<V, E>> subtreeListByFM = getSubtreeByFM(modulation, fromFS);
		int pos = 0;
		for (Integer key : subtreeListByFM.keySet()) {
			Subtree<V, E> sb = subtreeListByFM.get(key);
			for (V pathSelect : sb.getSelectedPaths().keySet()) {
				double LR = this.calculateLR(pk, sb.getSelectedPaths().get(pathSelect));
				if (LR >= gamma) {
					cumpleLR = true;
					pos = key;
					break;
				} else {
					cumpleLR = false;
				}
			}
			if (cumpleLR) {
				// assign d to subtree t and select pk for d
				// sb.addDestino(destino, pk);
				// this.subtreeList.set(pos, sb);
				this.setSubtreeToList(pos, pk, destino);
				Integer aux = fromFS + BW;
				if (aux.compareTo(this.indexFS) > 0)
					this.indexFS = aux;
				break;
			}
			pos++;
		}
		return cumpleLR;
	}

	@SuppressWarnings("unchecked")
	private Subtree<V, E> generateSubtree(Map<V, List<E>> paths, int fromFS, double BW, int modulation,
			List<V> targets) {
		int toFS = (int) (fromFS + BW - 1);
		Graph<V, E> subtree = (Graph<V, E>) new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);

		for (V key : paths.keySet()) {
			for (E edge : paths.get(key)) {
				if (!subtree.containsVertex(this.graph.getEdgeSource(edge)))
					subtree.addVertex(this.graph.getEdgeSource(edge));
				if (!subtree.containsVertex(this.graph.getEdgeTarget(edge)))
					subtree.addVertex(this.graph.getEdgeTarget(edge));
				subtree.addEdge(this.graph.getEdgeSource(edge), this.graph.getEdgeTarget(edge));
			}
		}

		Solicitud<V> solicitud = new Solicitud<V>(request.getSource(), targets, request.getCapRequired());
		List<E> edges = new ArrayList<>(subtree.edgeSet());
		return new Subtree<V, E>(subtree, edges, solicitud, fromFS, toFS, modulation, getMethod(), paths);
	}

	@Override
	public String getMethod() {
		return this.getClass().getSimpleName();
	}

	private void addSubtreeToList(Subtree<V, E> subtree, Integer indexSubtree) {
		subtreeList.add(subtree);
		for (E edge : subtree.getEdges()) {
			this.setFrecuencySlotStatus(subtree.getRequest(), edge, subtree.getFromFS(), subtree.getToFS());
		}
		this.indexFS = indexSubtree;
	}

	private void setSubtreeToList(int pos, List<E> shpDestino, V destino) {
		Subtree<V, E> sb = this.subtreeList.get(pos);
		sb.addDestino(destino, shpDestino);
		for (E edge : sb.getEdges()) {
			this.setFrecuencySlotStatus(sb.getRequest(), edge, sb.getFromFS(), sb.getToFS());
		}
		this.subtreeList.set(pos, sb);
	}

	@Override
	public String getMethodList() {
		return methodList;
	}

	@Override
	public List<Subtree<V, E>> getSubtreeList() {
		return subtreeList;
	}

}
