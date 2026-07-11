package lu.kbra.pclib.db.utils.impl;

public interface ProxyDataBaseEntryUtils extends DataBaseEntryUtils {

//	default <T extends DataBaseEntry, V> Function<List<Object>, V> buildMethodQueryFunction(SQLQueryable<T> instance, Method method) {
//		return getQueryFunctionProvider().getQueryFunctionProvider().buildMethodQueryFunction();
//	}

	SQLQueryFunctionProvider getQueryFunctionProvider();

}
