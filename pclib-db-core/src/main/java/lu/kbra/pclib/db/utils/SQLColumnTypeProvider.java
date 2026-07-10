package lu.kbra.pclib.db.utils;

import java.lang.reflect.AnnotatedType;
import java.util.Map;
import java.util.Optional;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;

public interface SQLColumnTypeProvider {

	default ColumnType getTypeFor(AnnotatedType annotatedType, Map<String, Object> typeHints) {
		return getTypeFor(PCUtils.getRawClass(annotatedType.getType()), Optional.of(annotatedType), typeHints);
	}

	ColumnType getTypeFor(Class<?> clazz, Optional<AnnotatedType> type, Map<String, Object> typeHints);

}
