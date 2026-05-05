package lu.kbra.pclib.db.utils.registry;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;

public interface ColumnTypeRegistry {

	void registerClassTypes(Map<Predicate<Class<?>>, Function<Column, ColumnType>> classTypeMap);

	void registerTypes(Map<Class<?>, Function<Column, ColumnType>> typeMap);

}
