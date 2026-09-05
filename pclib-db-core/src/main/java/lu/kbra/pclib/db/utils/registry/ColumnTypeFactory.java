package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.Optional;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.utils.impl.EncodingTypeProvider;

public interface ColumnTypeFactory<T extends ColumnType<?, ?>> {

	Integer eval(Class<?> clazz, HintsOwner map, EncodingTypeProvider encodingTypeProvider);

	T get(Optional<AnnotatedType> annotatedType, HintsOwner typeHints, EncodingTypeProvider encodingTypeProvider);

	Class<T> getCreatedType();

}
