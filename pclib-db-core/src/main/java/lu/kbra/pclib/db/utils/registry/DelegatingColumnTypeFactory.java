package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lu.kbra.pclib.db.domain.column.type.ColumnType;

@ToString
@RequiredArgsConstructor
public class DelegatingColumnTypeFactory<T extends ColumnType<?>> implements ColumnTypeFactory {

	@Getter
	protected final Class<? extends T> createdType;
	protected final BiFunction<Class<?>, Map<String, Object>, Integer> weight;
	protected final BiFunction<Optional<AnnotatedType>, Map<String, Object>, T> create;

	@Override
	public Integer eval(final Class<?> typeClazz, final Map<String, Object> typeHints) {
		return this.weight.apply(typeClazz, typeHints);
	}

	@Override
	public T get(final Optional<AnnotatedType> annotatedType, final Map<String, Object> typeHints) {
		return this.create.apply(annotatedType, typeHints);
	}

}
