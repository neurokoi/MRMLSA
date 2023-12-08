package py.una.fp.eon.core;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ApplicationParams {

	Properties prop;
	InputStream input;

	// @Inject
	// Logger logger;

	private static final String PROPERTIES_FILE_NAME = "trafico_parameters.properties";
	private static final String DEFAULT_PROPERTIES_FILE_NAME = "default_provisioning_assemble_parameters.properties";

	@PostConstruct
	public void init() {
		// logger.info("Se inicializaran los parametros de
		// provisioning-assemble");
		prop = new Properties();
		input = null;
		try {
			try {

				// Verificamos si se definió en el classpath
				// logger.info("Se busca el archivo de configuración en el
				// classpath");
				input = ApplicationParams.class.getResourceAsStream("/" + PROPERTIES_FILE_NAME);
				prop.load(input);
				// logger.info("Properties de atlas-core inicializado");
			} catch (Exception e) {
				// logger.warn("No se ha encontrado archivo de parametros, se
				// buscara default", e);
				input = ApplicationParams.class.getResourceAsStream("/" + DEFAULT_PROPERTIES_FILE_NAME);
				prop.load(input);
				// logger.info("Properties default de atlas-core inicializado");
			}

		} catch (Exception e) {
			// logger.error("No se ha podido cargar default params en
			// provisioning-assemble", e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// logger.error("Error al cerrar properties de parametros");
				}
			}
		}
	}

	public String getText(final String key, final Object... params) {
		String value = null;
		try {
			value = (prop.getProperty(key) != null) ? MessageFormat.format(prop.getProperty(key), params) : null;
		} catch (final MissingResourceException e) {
			value = "!" + key;
		}
		return value;
	}

	public HashMap<String, String> getAll() {
		HashMap<String, String> hash = new HashMap<>();
		for (Object k : prop.keySet()) {
			hash.put(k.toString(), getText(k.toString()));
		}
		return hash;
	}

}
