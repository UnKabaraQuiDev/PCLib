package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.Map;
import java.util.Optional;

import lu.kbra.pclib.db.autobuild.column.type.ColumnType;

public interface ColumnTypeFactory {

//	ReadOnlyPair<BiFunction<Class<?>, Map<String, Object>, Integer>, BiFunction<Optional<AnnotatedType>, Map<String, Object>, ColumnType>>

	Class<? extends ColumnType> getCreatedType();

	Integer eval(Class<?> clazz, Map<String, Object> map);

	ColumnType get(Optional<AnnotatedType> annotatedType, Map<String, Object> typeHints);

}
