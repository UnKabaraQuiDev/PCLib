package lu.kbra.pclib.db.domain.dialect;

import java.util.HashMap;
import java.util.Map;

import lu.kbra.pclib.db.utils.FunctionNotFoundException;

public class RegexMapSQLFunctionResolver implements EditableSQLFunctionResolver, MapSQLFunctionResolver {

	protected final Map<String, String> functions = new HashMap<>();

	@Override
	public String applyOrDefault(final String key, final String default_) {
		for (final Map.Entry<String, String> entry : this.functions.entrySet()) {
			if (key.matches(entry.getKey())) {
				return entry.getValue();
			}
		}

		return default_;
	}

	@Override
	public String apply(final String key) throws FunctionNotFoundException {
		for (final Map.Entry<String, String> entry : this.functions.entrySet()) {
			if (key.matches(entry.getKey())) {
				return entry.getValue();
			}
		}

		throw new FunctionNotFoundException(key);
	}

	@Override
	public Map<String, String> getFunctions() {
		return this.functions;
	}

	@Override
	public void put(final String key, final String value) {
		this.functions.put(key, value);
	}

	@Override
	public String remove(final String key) {
		return this.functions.remove(key);
	}

	@Override
	public boolean contains(final String key) {
		return this.functions.containsKey(key);
	}

	@Override
	public boolean matches(final String key) {
		for (final String pattern : this.functions.keySet()) {
			if (key.matches(pattern)) {
				return true;
			}
		}

		return false;
	}

}