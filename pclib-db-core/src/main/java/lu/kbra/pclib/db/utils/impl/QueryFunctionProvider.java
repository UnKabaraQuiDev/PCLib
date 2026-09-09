package lu.kbra.pclib.db.utils.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lu.kbra.pclib.db.domain.query.QueryStructure;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface QueryFunctionProvider {

	default <T extends DatabaseEntry, V> Function<Object[], V>
			buildMethodQueryFunction(final SQLQueryable<T> instance, final Method method) {
		return this.buildMethodQueryFunction(instance, method, new HashMap<>());
	}

	<T extends DatabaseEntry, V> Function<Object[], V>
			buildMethodQueryFunction(SQLQueryable<T> instance, Method method, Map<String, Object> customHints);

	<T extends DatabaseEntry, V> Function<Object[], V>
			buildMethodQueryFunction(SQLQueryable<T> instance, Method method, QueryStructure struct);

	<T extends DatabaseEntry> QueryStructure
			buildMethodQueryStructure(final SQLQueryable<T> instance, final Map<String, Object> hints, final Method method);

}
