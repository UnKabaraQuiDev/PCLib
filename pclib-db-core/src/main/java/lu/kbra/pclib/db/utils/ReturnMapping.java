package lu.kbra.pclib.db.utils;

import java.lang.reflect.AnnotatedType;

import lu.kbra.pclib.db.domain.column.type.ColumnType;

import lombok.Data;

@Data
public final class ReturnMapping {

	final AnnotatedType actualType;
	final boolean entryReturn;
	final ColumnType<?, ?> columnType;

}
