package py.una.fp.eon.core.utils;

/**
 * Definición de la velocidad de transmisión según la distancia
 * 
 * <pre>
 * &#64;Article{ YANG,
 * 	author = {Lulu Yang, Long Gong, Fen Zhou, Bernard Cousin, Mikl ́os Moln ́ar, and Zuqing Zhu},
 * 	title = {{Leveraging Light Forest With Rateless Network Coding to Design Efficient All-Optical Multicast Schemes for Elastic Optical Networks}},
 * 	journal = {JOURNAL OF LIGHTWAVE TECHNOLOGY},
 * 	volume = {33},
 * 	number = {18},
 * 	month = {sept},
 * 	year = {2015},
 * 	pages = {3945--3955}
 * }
 * </pre>
 * <pre>
 * [23]
 * &#64;Article{ WANG,
 * author = {C. Wang, G. Shen, and S. K. Bose},
 * title = {{“Distance adaptive dynamic routing and spectrum allocation in elastic optical networks with shared backup path protection”}},
 * journal = {JOURNAL OF LIGHTWAVE TECHNOLOGY},
 * volume = {33},
 * number = {14},
 * month = {jul},
 * year = {2015},
 * pages = {2955–-2964}
 * </pre>
 * @author evazquez
 *
 */
public class Modulation {
	private static int MAX_BPSK = 4000; // 5000; 
	private static int MAX_QPSK = 2000; // 2500;
	private static int MAX_8QAM = 1000; // 1250;
	private static int MAX_16QAM = 500; // 625;
	public static int NRO_BPSK = 1;
	public static int NRO_QPSK = 2;
	public static int NRO_8QAM = 3;
	public static int NRO_16QAM = 4;

	private static boolean isBPSK(double distance) {
		return distance <= MAX_BPSK ? true : false;
	}

	private static boolean isQPSK(double distance) {
		return distance <= MAX_QPSK ? true : false;
	}

	private static boolean is8QAM(double distance) {
		return distance <= MAX_8QAM ? true : false;
	}

	private static boolean is16QAM(double distance) {
		return distance <= MAX_16QAM ? true : false;
	}

	public static boolean isApropiateModul(int modulation, double distance) {
		if (modulation == NRO_16QAM) {
			return is16QAM(distance);
		}
		if (modulation == NRO_8QAM) {
			return is8QAM(distance);
		}
		if (modulation == NRO_QPSK) {
			return isQPSK(distance);
		}
		if (modulation == NRO_BPSK) {
			return isBPSK(distance);
		}

		return false;
	}

}
