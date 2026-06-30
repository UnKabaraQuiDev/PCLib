package lu.kbra.pclib.db.domain.dialect;

public interface EditableSQLFunctionResolver extends SQLFunctionResolver {

	void put(String key, String value);

	String remove(String key);

	boolean contains(String key);

	boolean matches(String key);

}
