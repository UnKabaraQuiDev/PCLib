package lu.kbra.pclib.db.utils.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface QueryFunctionProvider {

	default <T extends DatabaseEntry, V> Function<Object[], V> buildMethodQueryFunction(SQLQueryable<T> instance, Method method) {
		return buildMethodQueryFunction(instance, method, new HashMap<>());
	}

	<T extends DatabaseEntry, V> Function<Object[], V>
			buildMethodQueryFunction(SQLQueryable<T> instance, Method method, Map<String, Object> customHints);

}
