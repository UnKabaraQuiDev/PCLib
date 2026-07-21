package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import lu.kbra.pclib.db.domain.column.type.ColumnType;

public interface ColumnTypeRegistry {

	String DEBUG_TYPE_NAMES_PROPERTY = ColumnTypeRegistry.class.getSimpleName() + ".debug_type_names";
	boolean DEBUG_TYPE_NAMES = Boolean.getBoolean(ColumnTypeRegistry.DEBUG_TYPE_NAMES_PROPERTY);

	Integer PERFECT_MATCH_SCORE = 200;
	Integer MAP_MATCH_SCORE = 100;
	Integer TYPE_CATCH_ALL_SCORE = 50;
	Integer EXCLUDE = null;

	static <T extends ColumnType<?>> void registerType(
			final Class<? extends T> createdTypeClass,
			final BiFunction<Class<?>, Map<String, Object>, Integer> biasFunction,
			final BiFunction<Optional<AnnotatedType>, Map<String, Object>, T> provideFunction,
			final List<ColumnTypeFactory> typeMap) {

		if (ColumnTypeRegistry.DEBUG_TYPE_NAMES) {
			final BiFunction<Class<?>, Map<String, Object>, Integer> biasFunctionRepl = new BiFunction<Class<?>, Map<String, Object>, Integer>() {

				@Override
				public Integer apply(final Class<?> t, final Map<String, Object> u) {
					return biasFunction.apply(t, u);
				}

				@Override
				public String toString() {
					return createdTypeClass.toString();
				}

			};
			final BiFunction<Class<?>, Map<String, Object>, Integer> biasTypeFunctionRepl = new BiFunction<Class<?>, Map<String, Object>, Integer>() {

				@Override
				public Integer apply(final Class<?> clazz, final Map<String, Object> map) {
					return clazz == createdTypeClass ? ColumnTypeRegistry.PERFECT_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE;
				}

				@Override
				public String toString() {
					return createdTypeClass.toString() + " [PERFECT TYPE MATCH]";
				}

			};
			final BiFunction<Optional<AnnotatedType>, Map<String, Object>, T> provideFunctionRepl = new BiFunction<Optional<AnnotatedType>, Map<String, Object>, T>() {

				@Override
				public T apply(final Optional<AnnotatedType> t, final Map<String, Object> u) {
					return provideFunction.apply(t, u);
				}

				@Override
				public String toString() {
					return createdTypeClass.toString();
				}

			};
			typeMap.add(new DelegatingColumnTypeFactory<>(createdTypeClass, biasFunctionRepl, provideFunctionRepl));
			if (typeMap.stream().filter(c -> c.getCreatedType() == createdTypeClass).count() == 1) {
				typeMap.add(new DelegatingColumnTypeFactory<>(createdTypeClass, biasTypeFunctionRepl, provideFunctionRepl));
			}
		} else {
			typeMap.add(new DelegatingColumnTypeFactory<>(createdTypeClass, biasFunction, provideFunction));
			if (typeMap.stream().filter(c -> c.getCreatedType() == createdTypeClass).count() == 1) {
				typeMap.add(new DelegatingColumnTypeFactory<>(createdTypeClass,
						(clazz, map) -> clazz == createdTypeClass ? ColumnTypeRegistry.PERFECT_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
						provideFunction));
			}
		}
	}

	void registerTypes(List<ColumnTypeFactory> typeMap);

}
