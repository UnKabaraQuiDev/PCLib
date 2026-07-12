package lu.kbra.pclib.db.domain.dialect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lu.kbra.pclib.db.exception.FunctionNotFoundException;

public class StrictMapSQLFunctionResolver implements EditableSQLFunctionResolver, MapSQLFunctionResolver {

	protected final Map<String, String> functions = new HashMap<>();

	public StrictMapSQLFunctionResolver() {
	}

	public StrictMapSQLFunctionResolver(final Map<String, String> others) {
		this.functions.putAll(others);
	}

	@Override
	public String apply(final String str) throws FunctionNotFoundException {
		if (!this.contains(str)) {
			throw new FunctionNotFoundException("Function not found: " + str);
		}

		return this.functions.get(str);
	}

	@Override
	public boolean contains(final String key) {
		return this.functions.containsKey(key);
	}

	@Override
	public String remove(final String key) {
		return this.functions.remove(key);
	}

	@Override
	public boolean matches(final String key) {
		return this.contains(key);
	}

	@Override
	public String applyOrDefault(final String key, final String default_) {
		return this.functions.getOrDefault(key, default_);
	}

	public boolean isEmpty() {
		return this.functions.isEmpty();
	}

	@Override
	public void put(final String key, final String value) {
		this.functions.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
	}

	public void putAll(final SQLFunctionResolver other) {
		if (other instanceof MapSQLFunctionResolver) {
			this.functions.putAll(((MapSQLFunctionResolver) other).getFunctions());
		} else {
			throw new IllegalArgumentException("Incompatible resolver type: " + other.getClass());
		}
	}

	@Override
	public Map<String, String> getFunctions() {
		return this.functions;
	}

}
