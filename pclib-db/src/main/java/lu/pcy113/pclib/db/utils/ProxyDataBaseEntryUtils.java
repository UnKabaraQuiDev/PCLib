package lu.pcy113.pclib.db.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

import lu.pcy113.pclib.db.autobuild.query.Query;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQueryable;

public interface ProxyDataBaseEntryUtils extends DataBaseEntryUtils {

	<T extends DataBaseEntry> void initQueries(SQLQueryable<T> instance);

	<T extends DataBaseEntry> Object buildTableQueryFunction(
			Class<? extends SQLQueryable<T>> tableClazz,
			String tableName,
			SQLQueryable<T> instance,
			Type type,
			Query query);

	<T extends DataBaseEntry> Function<List<Object>, ?> buildMethodQueryFunction(String tableName, SQLQueryable<T> instance, Method method);

}
