package lu.kbra.pclib.db.utils.registry;

import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.impl.HintsOwner;

public interface EncodingTypeFactory<T extends EncodingType<V>, V> {

	Integer eval(Class<?> clazz, HintsOwner hints);

	T get(HintsOwner hints);

	Class<T> getCreatedType();

	Class<V> getStoredType();

}
