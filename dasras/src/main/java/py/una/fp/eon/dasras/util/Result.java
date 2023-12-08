package py.una.fp.eon.dasras.util;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Result {

	private static FileWriter fw = null;

	public static void openFile(String s, String target) {
		String path = new java.io.File("").getAbsolutePath();
		path = path + "/src/main" + "/resources/" + "result_" + s + "_to_" + target;

		try {
			fw = new FileWriter(path, false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static FileWriter getFw() {
		return fw;
	}

	public static void closeFile() {
		if (null != fw) {
			try {
				fw.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

}
