package lu.kbra.pclib.db.query;

import java.lang.reflect.AnnotatedType;

import lu.kbra.pclib.datastructure.tuple.ReadOnlyPair;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.SQLQueryable;

import lombok.Data;

@Data
public final class ReturnMapping {

	final AnnotatedType actualType;
	final boolean entryReturn;
	final @Nullable ColumnType<?, ?> columnType;
	final @Nullable ReadOnlyPair<Class<? extends SQLQueryable<?>>, String> returnTypeOwnerRef;

}
