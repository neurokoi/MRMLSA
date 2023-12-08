package py.una.fp.eon.core.utils;

import javax.enterprise.inject.Produces;
import javax.enterprise.context.ApplicationScoped;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author canetev
 */

@ApplicationScoped
public class ObjectMapperProducer {

    @Produces @EONMapperQualifier
    public ObjectMapper produceObjectMapper() {
        return new ObjectMapper();
    }
}
