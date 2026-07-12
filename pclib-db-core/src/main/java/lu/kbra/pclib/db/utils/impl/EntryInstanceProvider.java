package lu.kbra.pclib.db.utils.impl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.impl.function.ThrowingFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface EntryInstanceProvider {

	public interface EntryInstanceFactories extends Map<SQLQueryable<?>, Map<Set<String>, FactoryMethod>> {

	}

	@Getter
	@AllArgsConstructor
	public static class FactoryMethod {
		final List<ArgData> args;
		final ThrowingFunction<Object[], ? extends DataBaseEntry, DBException> function;
	}

	@Getter
	@AllArgsConstructor
	public static class ArgData {
		final String name;
		final ColumnData columnData;
		final Type type;
		final Integer index;
	}

	<T extends DataBaseEntry> T instance(final SQLQueryable<T> table);

	<T extends DataBaseEntry> FactoryMethod getFactoryMethod(SQLQueryable<T> table, String[] columns);

}
