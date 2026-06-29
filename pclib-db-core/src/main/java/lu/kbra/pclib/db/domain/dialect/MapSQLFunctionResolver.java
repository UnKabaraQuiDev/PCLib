package lu.kbra.pclib.db.domain.dialect;

import java.util.Map;

public interface MapSQLFunctionResolver extends SQLFunctionResolver {

	Map<String, String> getFunctions();

	void put(String key, String value);

	String remove(String key);

	boolean contains(String key);

}
