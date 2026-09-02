package lu.kbra.pclib.db.domain.dialect;

import java.util.Map;

import lu.kbra.pclib.PCUtils;

public interface MapSQLFunctionResolver extends SQLFunctionResolver {

	Map<String, String> getFunctions();

	void put(String key, String value);

	String remove(String key);

	boolean contains(String key);

	@Override
	default Map<String, Object> toMap() {
		return PCUtils.hashMap("functions", getFunctions());
	}

}
