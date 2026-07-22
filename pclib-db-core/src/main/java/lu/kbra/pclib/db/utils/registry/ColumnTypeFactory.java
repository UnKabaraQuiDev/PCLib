package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.Optional;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.utils.impl.SQLEncodingTypeProvider;

public interface ColumnTypeFactory<T extends ColumnType<?, ?>> {

	Integer eval(Class<?> clazz, HintsOwner map, SQLEncodingTypeProvider encodingTypeProvider);

	T get(Optional<AnnotatedType> annotatedType, HintsOwner typeHints, SQLEncodingTypeProvider encodingTypeProvider);

	Class<T> getCreatedType();

}
