package lu.kbra.pclib.db.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.NTSQLQueryable;

public interface ProxyDataBaseEntryUtils extends DataBaseEntryUtils {

	<T extends DataBaseEntry> void initQueries(NTSQLQueryable<T> instance);

	<T extends DataBaseEntry> Object buildTableQueryFunction(
			Class<? extends NTSQLQueryable<T>> tableClazz,
			String tableName,
			NTSQLQueryable<T> instance,
			Type type,
			Query query);

	<T extends DataBaseEntry> Function<List<Object>, ?> buildMethodQueryFunction(String tableName, NTSQLQueryable<T> instance, Method method);

}
