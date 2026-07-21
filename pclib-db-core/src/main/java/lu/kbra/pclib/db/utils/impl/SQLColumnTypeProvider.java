package lu.kbra.pclib.db.utils.impl;

import java.lang.reflect.AnnotatedType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.utils.registry.ColumnTypeFactory;

public interface SQLColumnTypeProvider {

	Optional<AnnotatedType> EMPTY_OPTIONAL = Optional.empty();
	HintsOwner EMPTY_MAP = new HintsOwner() {

		final Map<String, Object> hints = Collections.emptyMap();

		public Map<String, Object> getHints() {
			return hints;
		}

	};

	default ColumnType<?, ?> getTypeFor(final AnnotatedType annotatedType, final HintsOwner typeHints) {
		return this.getTypeFor(PCUtils.getRawClass(annotatedType.getType()), Optional.of(annotatedType), typeHints);
	}

	ColumnType<?, ?> getTypeFor(Class<?> clazz, Optional<AnnotatedType> type, HintsOwner typeHints);

	List<ColumnTypeFactory<?>> getColumnTypeFactories();

	default ColumnType<?, ?> getTypeFor(final Class<?> clazz) {
		return this.getTypeFor(clazz, SQLColumnTypeProvider.EMPTY_OPTIONAL, SQLColumnTypeProvider.EMPTY_MAP);
	}

	Stream<ColumnTypeFactory<?>> computeType(final Class<?> rawType, final HintsOwner typeHints);

}
