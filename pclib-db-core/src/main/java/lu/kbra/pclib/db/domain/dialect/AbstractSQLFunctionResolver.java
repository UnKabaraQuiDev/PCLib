package lu.kbra.pclib.db.domain.dialect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lu.kbra.pclib.db.utils.FunctionNotFoundException;

public class AbstractSQLFunctionResolver implements SQLFunctionResolver {

	protected final Map<String, String> functions = new HashMap<>();

	public AbstractSQLFunctionResolver() {
	}

	public AbstractSQLFunctionResolver(final Map<String, String> others) {
		this.functions.putAll(others);
	}

	@Override
	public String apply(final String str) throws FunctionNotFoundException {
		final String result = this.functions.get(str);

		if (result == null) {
			throw new FunctionNotFoundException("Function not found: " + str);
		}

		return result;
	}

	@Override
	public String applyOrDefault(final String key, final String default_) {
		return this.functions.getOrDefault(key, default_);
	}

	public boolean isEmpty() {
		return this.functions.isEmpty();
	}

	public void put(final String key, final String value) {
		this.functions.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
	}

	public void putAll(final SQLFunctionResolver other) {
		if (other instanceof AbstractSQLFunctionResolver) {
			this.functions.putAll(((AbstractSQLFunctionResolver) other).functions);
		} else {
			throw new IllegalArgumentException("Incompatible resolver type: " + other.getClass());
		}
	}

}
