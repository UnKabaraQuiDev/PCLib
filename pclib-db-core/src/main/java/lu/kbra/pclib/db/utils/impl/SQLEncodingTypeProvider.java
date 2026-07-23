package lu.kbra.pclib.db.utils.impl;

import java.util.List;
import java.util.stream.Stream;

import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.utils.registry.EncodingTypeFactory;

public interface SQLEncodingTypeProvider {

	<T> EncodingType<T> getTypeFor(Class<T> storedType, HintsOwner typeHints);

	List<EncodingTypeFactory<?, ?>> getEncodingTypeFactories();

	default <T> EncodingType<T> getTypeFor(final Class<T> storedType) {
		return this.getTypeFor(storedType, HintsOwner.EMPTY);
	}

	<T> Stream<EncodingTypeFactory<?, T>> computeType(final Class<T> storedType, final HintsOwner typeHints);

}
