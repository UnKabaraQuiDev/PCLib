package lu.kbra.pclib.db.utils.registry;

import java.util.function.BiFunction;
import java.util.function.Function;

import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.impl.HintsOwner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class DelegatingEncodingTypeFactory<T extends EncodingType<V>, V> implements EncodingTypeFactory<T, V> {

	@Getter
	protected final Class<T> createdType;
	@Getter
	protected final Class<V> storedType;
	protected final BiFunction<Class<?>, HintsOwner, Integer> weight;
	protected final Function<HintsOwner, T> create;

	@Override
	public Integer eval(final Class<?> typeClazz, final HintsOwner typeHints) {
		return this.weight.apply(typeClazz, typeHints);
	}

	@Override
	public T get(HintsOwner hints) {
		return create.apply(hints);
	}

}
