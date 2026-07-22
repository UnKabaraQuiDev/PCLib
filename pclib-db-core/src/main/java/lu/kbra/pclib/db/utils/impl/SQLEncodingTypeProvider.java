package lu.kbra.pclib.db.utils.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.utils.registry.EncodingTypeFactory;

public interface SQLEncodingTypeProvider {

	HintsOwner EMPTY_MAP = new HintsOwner() {

		final Map<String, Object> hints = Collections.emptyMap();

		public Map<String, Object> getHints() {
			return hints;
		}

	};

	<T> EncodingType<T> getTypeFor(Class<T> storedType, HintsOwner typeHints);

	List<EncodingTypeFactory<?, ?>> getEncodingTypeFactories();

	default <T> EncodingType<T> getTypeFor(final Class<T> storedType) {
		return this.getTypeFor(storedType, SQLEncodingTypeProvider.EMPTY_MAP);
	}

	<T> Stream<EncodingTypeFactory<?, T>> computeType(final Class<T> storedType, final HintsOwner typeHints);

}
