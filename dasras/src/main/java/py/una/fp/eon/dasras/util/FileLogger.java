package py.una.fp.eon.dasras.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

public class FileLogger {

	private FileWriter fw;
	private String path;

	public FileLogger(String relativePath, String header) {
		String prefix = new java.io.File("").getAbsolutePath();
		path = prefix + relativePath;

		try {
			fw = new FileWriter(path, true);
			fw.write("Fecha: " + new Date() + "\n");
			fw.write(header);
		} catch (IOException e) {
			throw new RuntimeException("Error al abrir archivo: " + path);
		}

	}

	public void closeFile() {
		if (null != fw) {
			try {
				fw.close();
			} catch (IOException e) {
				throw new RuntimeException("Error al cerrar archivo: " + path);
			}
		}
	}

	public void write(String str) {
		try {
			fw.write(str);
		} catch (IOException e) {
			throw new RuntimeException("Error al escribir en el archivo: " + str);
		}
	}

	public void writeLines(Collection<String> lines) {
		for (String line : lines) {
			write(line + "\n");
		}
	}
}
