package lu.kbra.pclib.db.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.impl.ThrowingFunction;

public interface ProxyDataBaseEntryUtils extends DataBaseEntryUtils {

	<T extends DataBaseEntry> void initQueries(SQLQueryable<T> instance);

	<T extends DataBaseEntry> Object buildTableQueryFunction(
			Class<? extends SQLQueryable<T>> tableClazz,
			String tableName,
			SQLQueryable<T> instance,
			Type type,
			Query query);

	<T extends DataBaseEntry> ThrowingFunction<List<Object>, ?, SQLException> buildMethodQueryFunction(
			String tableName,
			SQLQueryable<T> instance,
			Method method);

}
