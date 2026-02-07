package lu.kbra.pclib.db.config;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ObjectMapperHolder {

	private static ObjectMapper mapper;

	public ObjectMapperHolder(ObjectMapper mapper) {
		ObjectMapperHolder.mapper = mapper;
	}

	public static ObjectMapper get() {
		return mapper;
	}
}
