package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.Map;
import java.util.Optional;

import lu.kbra.pclib.db.domain.column.type.ColumnType;

public interface ColumnTypeFactory {

	Integer eval(Class<?> clazz, Map<String, Object> map);

	ColumnType get(Optional<AnnotatedType> annotatedType, Map<String, Object> typeHints);

	Class<? extends ColumnType> getCreatedType();

}
