package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.Optional;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.utils.impl.EncodingTypeProvider;
import lu.kbra.pclib.impl.function.TriFunction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class DelegatingColumnTypeFactory<T extends ColumnType<?, ?>> implements ColumnTypeFactory<T> {

	@Getter
	protected final Class<T> createdType;
	protected final TriFunction<Class<?>, HintsOwner, EncodingTypeProvider, Integer> weight;
	protected final TriFunction<Optional<AnnotatedType>, HintsOwner, EncodingTypeProvider, T> create;

	@Override
	public Integer eval(final Class<?> typeClazz, final HintsOwner typeHints, final EncodingTypeProvider encodingTypeProvider) {
		return this.weight.apply(typeClazz, typeHints, encodingTypeProvider);
	}

	@Override
	public T get(final Optional<AnnotatedType> annotatedType, final HintsOwner typeHints, final EncodingTypeProvider encodingTypeProvider) {
		return this.create.apply(annotatedType, typeHints, encodingTypeProvider);
	}

}
