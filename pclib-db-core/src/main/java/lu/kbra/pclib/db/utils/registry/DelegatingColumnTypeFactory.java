package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.Optional;
import java.util.function.BiFunction;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.HintsOwner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class DelegatingColumnTypeFactory<T extends ColumnType<?, ?>> implements ColumnTypeFactory<T> {

	@Getter
	protected final Class<T> createdType;
	protected final BiFunction<Class<?>, HintsOwner, Integer> weight;
	protected final BiFunction<Optional<AnnotatedType>, HintsOwner, T> create;

	@Override
	public Integer eval(final Class<?> typeClazz, final HintsOwner typeHints) {
		return this.weight.apply(typeClazz, typeHints);
	}

	@Override
	public T get(final Optional<AnnotatedType> annotatedType, final HintsOwner typeHints) {
		return this.create.apply(annotatedType, typeHints);
	}

}
