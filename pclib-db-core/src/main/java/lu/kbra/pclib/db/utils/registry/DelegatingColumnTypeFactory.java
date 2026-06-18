package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import lu.kbra.pclib.db.autobuild.column.type.ColumnType;

public class DelegatingColumnTypeFactory implements ColumnTypeFactory {

	protected final Class<? extends ColumnType> createdType;
	protected final BiFunction<Class<?>, Map<String, Object>, Integer> weight;
	protected final BiFunction<Optional<AnnotatedType>, Map<String, Object>, ColumnType> create;

	public DelegatingColumnTypeFactory(
			Class<? extends ColumnType> createdType,
			BiFunction<Class<?>, Map<String, Object>, Integer> weight,
			BiFunction<Optional<AnnotatedType>, Map<String, Object>, ColumnType> create) {
		this.createdType = createdType;
		this.weight = weight;
		this.create = create;
	}

	@Override
	public Integer eval(final Class<?> typeClazz, final Map<String, Object> typeHints) {
		return this.weight.apply(typeClazz, typeHints);
	}

	@Override
	public ColumnType get(final Optional<AnnotatedType> annotatedType, final Map<String, Object> typeHints) {
		return this.create.apply(annotatedType, typeHints);
	}

	public Class<? extends ColumnType> getCreatedType() {
		return createdType;
	}

	@Override
	public String toString() {
		return "DelegatingColumnTypeFactory@" + System.identityHashCode(this) + " [weight=" + weight + ", create=" + create + "]";
	}

}
