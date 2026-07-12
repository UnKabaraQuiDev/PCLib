package lu.kbra.pclib.db.utils.impl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface SQLQueryFunctionProvider {

	<T extends DataBaseEntry, V> Function<List<Object>, V> buildMethodQueryFunction(SQLQueryable<T> instance, Method method);

}
