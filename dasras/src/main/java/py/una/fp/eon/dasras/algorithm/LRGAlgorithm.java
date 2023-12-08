package py.una.fp.eon.dasras.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;

import py.una.fp.eon.core.FrecuencySlot;
import py.una.fp.eon.core.Solicitud;
import py.una.fp.eon.core.Subtree;

/**
 * <pre>
 * 1: Given a multicast request r (s, D, C)
 * 2: run Algorithm 1 for multicast request r
 * 3: if Algorithm 1 fails then
 * 4:	for each unassigned destination d1 in D do
 * 5:		for each destination d2 ≠ d1 in D do
 * 6:			calculate LR between the shortest path from s to d1 and the
 * 7:			shortest path from s to d2 using (13)
 * 8:			if LR ≥ γ then
 * 9:				assign d1 and d2 to one group and goto line 4 =>OJO: TOME EL SGTE d1 desasignado sin el d2 asignado a un grupo <=
 * 10:			end if
 * 11:		end for
 * 12:	end for
 * 13:	assign each unassigned destination d in D to individual group
 * 14:	run Algorithm 1 to build a subtree for each group
 * 15:	if Algorithm 1 fails then
 * 16:		release all allocated subtrees
 * 17:		block multicast request r and return
 * 18:	end if
 * 19: end if
 * </pre>
 * 
 * @author evazquez
 *
 */
public class LRGAlgorithm<V, E> extends AlgorithmBase<V, E> {
	// La PRIMERA POSICION es la ruta más corta
	private final int POS_SHORTESTPATH = 0;
	private List<List<V>> grups;
	private Double threshold;
	private List<Subtree<V, E>> subtreeList;
	private String methodList;

	public LRGAlgorithm(Graph<V, E> graph, Map<String, List<FrecuencySlot<V>>> frecuencySlots, Solicitud<V> request,
			double capacityNetwork, int guardBand, Double threshold, int countfs) {
		super(graph, frecuencySlots, request, capacityNetwork, guardBand, countfs);
		this.grups = new ArrayList<>();
		this.subtreeList = new ArrayList<>();
		this.threshold = threshold;
		this.methodList = getMethod();
	}

	/**
	 * 
	 * @param capTotal
	 * @param guardBand
	 * @param fs
	 * @param shortestPaths
	 * @param gamma
	 */
	public void calculate(Map<String, List<List<E>>> shortestPaths, Integer indexCurrentFS) {
		// 2: run Algorithm 1 for multicast request r
		KSPTAlgorithm<V, E> algorithmKSPT = new KSPTAlgorithm<V, E>(this.graph, frecuencySlots, request,
				capacityNetwork, guardBand, countfs);
		algorithmKSPT.calculate(shortestPaths, indexCurrentFS);
		// Asumi que el algoritmo falla cuando la solicitud esta bloqueada
		/*
		 * en la tesis considera que falla el algoritmo cuando: if it cannot
		 * return a solution that does not increase the current value of the
		 * highest index of the used FS
		 */
		// 3: if Algorithm 1 fails then
		if (isFailed(algorithmKSPT.getRequest().isBloqued(), algorithmKSPT.getIndexFS(), indexCurrentFS)) {
			Map<V, V> destinosSinasignar = new HashMap<>();
			for (V destino : request.getTarget()) {
				destinosSinasignar.put(destino, destino);
			}
			for (V dUno : request.getTarget()) {
				if (destinosSinasignar.containsKey(dUno)) {
					// se remueve el d1, para obtener siempre d2<>d1
					boolean asignoGrupo = false;
					destinosSinasignar.remove(dUno);
					for (Iterator<V> iterator = destinosSinasignar.keySet().iterator(); iterator.hasNext();) {
						V dDos = iterator.next();
						// calculate LR between the shortestpath from s to d1
						// and the shortestpath from s to d2 using (13)
						String keyDUno = request.getSource() + "_" + dUno;
						String keyDDos = request.getSource() + "_" + dDos;
						double LR = calculateLR(shortestPaths.get(keyDUno).get(POS_SHORTESTPATH),
								shortestPaths.get(keyDDos).get(POS_SHORTESTPATH));
						if (LR >= threshold) {
							List<V> newGrupo = new ArrayList<>();
							newGrupo.add(dUno);
							newGrupo.add(dDos);
							// se remueve destinos del nuevo grupo
							iterator.remove();
							grups.add(newGrupo);
							asignoGrupo = true;
							break; // GOTO LINE 4
						}
					}
					/* cuando el dUNo sea el unico destino sin asignar */
					if (!asignoGrupo) {
						if (!destinosSinasignar.containsKey(dUno))
							destinosSinasignar.put(dUno, dUno);
					}
				}
			}
			// 13: assign each unassigned destination d in D to individual group
			for (V destino : destinosSinasignar.keySet()) {
				List<V> newGrupo = new ArrayList<>();
				newGrupo.add(destino);
				grups.add(newGrupo);
			}
			this.subtreeList = new ArrayList<>();
			Integer aux = -1;
			List<Subtree<V, E>> subtreeListGroup = new ArrayList<>();
			for (List<V> grupo : grups) {
				Solicitud<V> requestNewGrupo = new Solicitud<V>(request.getSource(), grupo, request.getCapRequired());
				algorithmKSPT = new KSPTAlgorithm<V, E>(this.graph, this.frecuencySlots, requestNewGrupo, capacityNetwork,
						guardBand, countfs);
				algorithmKSPT.calculate(shortestPaths, null);
				if (algorithmKSPT.getRequest().isBloqued()) {
					initSubtreeToList();
					this.request.setBloqued(true);
					this.methodList = null;
					releaseFrecuencySlot(subtreeListGroup); //liberar FS ocupados
					break;
				} else {
					if (algorithmKSPT.getIndexFS().compareTo(aux) > 0) {
						aux = algorithmKSPT.getIndexFS();
					}
					subtreeListGroup.add(algorithmKSPT.getSubtree());
				}
			}
			if (!this.request.isBloqued()) {
				if (!isFailed(this.request.isBloqued(), aux, indexCurrentFS)) {
					for (Subtree<V, E> subtree : subtreeListGroup) {
						this.addSubtreeToList(subtree, aux);
					}
					this.methodList = getMethod();
				} else {
					initSubtreeToList();
					this.request.setBloqued(true);
					this.methodList = null; 
					this.releaseFrecuencySlot(subtreeListGroup); //liberar FS ocupados
				}
			}
		} else {
			this.subtreeList = new ArrayList<>();
			this.addSubtreeToList(algorithmKSPT.getSubtree(), algorithmKSPT.getIndexFS());
			this.methodList = algorithmKSPT.getMethod();
			this.request.setBloqued(false);
		}
	}

	@Override
	public String getMethod() {
		return this.getClass().getSimpleName();
	}

	@Override
	public List<Subtree<V, E>> getSubtreeList() {
		return subtreeList;
	}

	public void setSubtreeList(List<Subtree<V, E>> subtreeList) {
		this.subtreeList = subtreeList;
	}

	private void addSubtreeToList(Subtree<V, E> subtree, Integer indexSubtree) {
		subtreeList.add(subtree);
		for (E edge : subtree.getEdges()) {
			setFrecuencySlotStatus(subtree.getRequest(), edge, subtree.getFromFS(), subtree.getToFS());
		}
		this.indexFS = indexSubtree;
	}

	private void initSubtreeToList() {
		this.subtreeList = new ArrayList<>();
		this.indexFS = -1;
	}

	@Override
	public String getMethodList() {
		return methodList;
	}

}
