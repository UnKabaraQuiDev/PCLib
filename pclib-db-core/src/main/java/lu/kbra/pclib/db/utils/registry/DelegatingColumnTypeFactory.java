package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.utils.impl.SQLEncodingTypeProvider;
import lu.kbra.pclib.impl.function.TriFunction;

@ToString
@RequiredArgsConstructor
public class DelegatingColumnTypeFactory<T extends ColumnType<?, ?>> implements ColumnTypeFactory<T> {

	@Getter
	protected final Class<T> createdType;
	protected final TriFunction<Class<?>, HintsOwner, SQLEncodingTypeProvider, Integer> weight;
	protected final TriFunction<Optional<AnnotatedType>, HintsOwner, SQLEncodingTypeProvider, T> create;

	@Override
	public Integer eval(final Class<?> typeClazz, final HintsOwner typeHints, SQLEncodingTypeProvider encodingTypeProvider) {
		return this.weight.apply(typeClazz, typeHints, encodingTypeProvider);
	}

	@Override
	public T get(final Optional<AnnotatedType> annotatedType, final HintsOwner typeHints, SQLEncodingTypeProvider encodingTypeProvider) {
		return this.create.apply(annotatedType, typeHints, encodingTypeProvider);
	}

}
