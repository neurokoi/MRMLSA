package py.una.fp.eon.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase que se encarga de manejar operaciones sobre un archivo
 * 
 * @author Elena Vazquez <evazquez@cnc.una.py>
 * 
 */
public class FileManage {

	private FileReader fr = null;
	private String ruta;

	/**
	 * Constructor se encarga de recibir la dirección del archivo
	 * 
	 * @param ruta
	 *            dirección del archivo
	 */
	public FileManage(String ruta) {
		this.ruta = ruta;
	}

	public FileReader getFr() {
		return fr;
	}

	/**
	 * Abre el archivo
	 * 
	 * @return buffer para la lectura del archivo
	 */
	public BufferedReader openFile() {
		File archivo = null;
		BufferedReader br = null;

		String path = new java.io.File("").getAbsolutePath();
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

}
