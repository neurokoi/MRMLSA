package py.una.fp.eon.core.utils;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;

import py.una.fp.eon.core.Model;
import py.una.fp.eon.core.SequenceResult;
import py.una.fp.eon.core.SummaryTotal;

public class CSVUtils {

	private static final char DEFAULT_SEPARATOR = ',';
	private static final String NEW_LINE_SEPARATOR = "\n";
	private static final String CUSTOM_QUOTE = "'";

	public static void write(Writer w, List<? extends Model> values, Field[] fields) throws IOException {
		writeLine(w, fields, DEFAULT_SEPARATOR);
		write(w, values, DEFAULT_SEPARATOR);
	}
        
        public static void write(Writer w, List<? extends Model> values, String[] fields) throws IOException {
		writeLine(w, fields, DEFAULT_SEPARATOR);
		write(w, values, DEFAULT_SEPARATOR);
	}
       
        
	
	public static void write(Writer w, Field[] fields) throws IOException {
		writeLine(w, fields, DEFAULT_SEPARATOR);
	}
	
	public static void write(Writer w, List<? extends Model> values) throws IOException {
		write(w, values, DEFAULT_SEPARATOR);
	}
        
        
                
        public static void writeHeuristica(Writer w, Output values) throws IOException {
		writeHeuristica(w, values, DEFAULT_SEPARATOR);
	}
        
        public static void writeO(Writer w, List<Output> values) throws IOException {
		writeO(w, values, DEFAULT_SEPARATOR);
	}
        
        
        
  
        
        public static void writeGA(Writer w, List<SequenceResult> values, Field[] fields) throws IOException {
		writeLine(w, fields, DEFAULT_SEPARATOR);
		writeGA(w, values, DEFAULT_SEPARATOR);
	}
        
        public static void writeGA(Writer w, List<SequenceResult> values) throws IOException {
		writeGA(w, values, DEFAULT_SEPARATOR);
	}
        
        public static void writeGA(Writer w, List<SequenceResult> values, char separators) throws IOException {

		// default customQuote is empty
		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuilder sb = new StringBuilder();
		for (SequenceResult value : values) {
			sb.append(value.toStringCSV(separators));
			sb.append(NEW_LINE_SEPARATOR);
		}
		w.append(sb.toString());
	}
        

	public static void write(Writer w, List<? extends Model> values, char separators) throws IOException {
            // default customQuote is empty
            if (separators == ' ') {
                    separators = DEFAULT_SEPARATOR;
            }

            StringBuilder sb = new StringBuilder();
            for (Model value : values) {
                    sb.append(value.toStringCSV(separators));
                    sb.append(NEW_LINE_SEPARATOR);
            }
            w.append(sb.toString());
	}
        
        
        
        
        public static void writeHeuristica(Writer w, Output values, char separators) throws IOException {
            // default customQuote is empty
            if (separators == ' ') {
                    separators = DEFAULT_SEPARATOR;
            }

            StringBuilder sb = new StringBuilder();
            
            sb.append(values.toStringCSV2(separators));
            sb.append(NEW_LINE_SEPARATOR);
            
            w.append(sb.toString());
	}
        
        
        
        public static void writeO(Writer w, List<Output> values, char separators) throws IOException {
            // default customQuote is empty
            if (separators == ' ') {
                    separators = DEFAULT_SEPARATOR;
            }

            StringBuilder sb = new StringBuilder();
            for (Output value : values) {
                    sb.append(value.toStringCSV2(separators));
                    sb.append(NEW_LINE_SEPARATOR);
            }
            w.append(sb.toString());
	}
        
 

	public static void writeLine(Writer w, Field[] values, char separators) throws IOException {
		boolean first = true;
		// default customQuote is empty
		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuilder sb = new StringBuilder();
		for (Field value : values) {
			if (!first) {
				sb.append(separators);
			}
			sb.append(CUSTOM_QUOTE).append(value.getName()).append(CUSTOM_QUOTE);

			first = false;
		}
		sb.append(NEW_LINE_SEPARATOR);
		w.append(sb.toString());

	}
        
        public static void writeLine(Writer w, String[] values, char separators) throws IOException {
		boolean first = true;
		// default customQuote is empty
		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			if (!first) {
				sb.append(separators);
			}
			sb.append(CUSTOM_QUOTE).append(value).append(CUSTOM_QUOTE);

			first = false;
		}
		sb.append(NEW_LINE_SEPARATOR);
		w.append(sb.toString());

	}

}
