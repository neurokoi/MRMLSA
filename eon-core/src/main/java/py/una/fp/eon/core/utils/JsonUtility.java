package py.una.fp.eon.core.utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

@Singleton
public class JsonUtility {

	@Inject
	@EONMapperQualifier
	ObjectMapper mapper;

	@PostConstruct
	public void init() {
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE);
	}

	public String getJsonFromObject(Object list) throws JsonProcessingException {
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE);
		return mapper.writeValueAsString(list);
	}

	public ObjectMapper getObjectMapper() {
		return mapper;
	}

	public static String getJsonFromObj(Object list) throws JsonProcessingException {
		ObjectMapper staticOm = new ObjectMapper();
		staticOm.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		staticOm.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE);
		return staticOm.writeValueAsString(list);
	}

}