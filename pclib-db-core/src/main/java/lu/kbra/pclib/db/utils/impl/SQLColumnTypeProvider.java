package lu.kbra.pclib.db.utils.impl;

import java.lang.reflect.AnnotatedType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.utils.registry.ColumnTypeFactory;

public interface SQLColumnTypeProvider {

	Optional<AnnotatedType> EMPTY_OPTIONAL = Optional.empty();

	default ColumnType<?, ?> getTypeFor(final AnnotatedType annotatedType, final HintsOwner typeHints) {
		return this.getTypeFor(PCUtils.getRawClass(annotatedType.getType()), Optional.of(annotatedType), typeHints);
	}

	ColumnType<?, ?> getTypeFor(Class<?> clazz, Optional<AnnotatedType> type, HintsOwner typeHints);

	List<ColumnTypeFactory<?>> getColumnTypeFactories();

	default ColumnType<?, ?> getTypeFor(final Class<?> clazz) {
		return this.getTypeFor(clazz, SQLColumnTypeProvider.EMPTY_OPTIONAL, HintsOwner.EMPTY);
	}

	Stream<ColumnTypeFactory<?>> computeType(final Class<?> rawType, final HintsOwner typeHints);

}
