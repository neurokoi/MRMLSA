package py.una.fp.eon.dasras.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;

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
public class KSPTAlgorithm<V, E> extends AlgorithmBase<V, E> {

	public KSPTAlgorithm(Graph<V, E> graph, Map<String, List<FrecuencySlot<V>>> frecuencySlots, Solicitud<V> request,
			double capacityNetwork, int guardBand, int countfs) {
		super(graph, frecuencySlots, request, capacityNetwork, guardBand, countfs);
	}

	/**
	 * 
	 * @param frecuencySlots
	 * @param shortestPaths
	 */
	@Override
	public void calculate(Map<String, List<List<E>>> shortestPaths, Integer indexUsedFS) {
		List<List<E>> pathO = new ArrayList<>();
		for (int m = Modulation.NRO_16QAM; m > 0; m--) {
			int BW = this.request.calculateBW(this.capacityNetwork, this.guardBand, m);
			Boolean breakForModulation = false;
			for (int f = 1; f < (countfs - BW + 1); f++) {
				int fromFS = f - 1;
				for (V target : request.getTarget()) {
					String key = this.request.getSource() + "_" + target;
					ShortestPathFM<E> spk = findShortestPathByFM(shortestPaths.get(key), m, fromFS, BW);
					if (spk != null && spk.getShortestPath() != null && !spk.getShortestPath().isEmpty()) {
						pathO.add(spk.getShortestPath());
					} else {
						pathO.clear();
						if (spk != null && spk.getBreakForModulation()) {
							breakForModulation = true; // modulation no cumple
														// en todos los caminos
														// cortos
						}
						break; // porque se necesita todos los detinos
					}
				}
				if (breakForModulation) {
					break;// cambiar a la siguiente modulacion
				}
				if (pathO.size() == request.getTarget().size()) {
					// merge path of set 0 in tree t
					// allocate FSs (f to f+BW-1) to t
					int toFS = fromFS + (BW - 1);
					generateSubtree(pathO, fromFS, toFS, m, this.request);
					return;
				}
			}
		}
		this.request.setBloqued(true);
		this.indexFS = -1;
	}

	@Override
	public String getMethod() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getMethodList() {
		return null;
	}

	@Override
	public List<? extends Subtree<V, E>> getSubtreeList() {
		return null;
	}

}
