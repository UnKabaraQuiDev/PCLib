package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.HintsOwner;

public interface ColumnTypeRegistry {

	String DEBUG_TYPE_NAMES_PROPERTY = ColumnTypeRegistry.class.getSimpleName() + ".debug_type_names";
	boolean DEBUG_TYPE_NAMES = Boolean.getBoolean(ColumnTypeRegistry.DEBUG_TYPE_NAMES_PROPERTY);

	Integer PERFECT_MATCH_SCORE = 200;
	Integer MAP_MATCH_SCORE = 100;
	Integer TYPE_CATCH_ALL_SCORE = 50;
	Integer EXCLUDE = null;

	static <T extends ColumnType<?, ?>> void registerType(
			final Class<T> createdTypeClass,
			final BiFunction<Class<?>, HintsOwner, Integer> biasFunction,
			final BiFunction<Optional<AnnotatedType>, HintsOwner, T> provideFunction,
			final List<ColumnTypeFactory<?>> typeMap) {

		if (ColumnTypeRegistry.DEBUG_TYPE_NAMES) {
			final BiFunction<Class<?>, HintsOwner, Integer> biasFunctionRepl = new BiFunction<Class<?>, HintsOwner, Integer>() {

				@Override
				public Integer apply(final Class<?> t, final HintsOwner u) {
					return biasFunction.apply(t, u);
				}

				@Override
				public String toString() {
					return createdTypeClass.toString();
				}

			};
			final BiFunction<Class<?>, HintsOwner, Integer> biasTypeFunctionRepl = new BiFunction<Class<?>, HintsOwner, Integer>() {

				@Override
				public Integer apply(final Class<?> clazz, final HintsOwner map) {
					return clazz == createdTypeClass ? ColumnTypeRegistry.PERFECT_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE;
				}

				@Override
				public String toString() {
					return createdTypeClass.toString() + " [PERFECT TYPE MATCH]";
				}

			};
			final BiFunction<Optional<AnnotatedType>, HintsOwner, T> provideFunctionRepl = new BiFunction<Optional<AnnotatedType>, HintsOwner, T>() {

				@Override
				public T apply(final Optional<AnnotatedType> t, final HintsOwner u) {
					return provideFunction.apply(t, u);
				}

				@Override
				public String toString() {
					return createdTypeClass.toString();
				}

			};
			typeMap.add(new DelegatingColumnTypeFactory<>(createdTypeClass, biasFunctionRepl, provideFunctionRepl));
			if (typeMap.stream().filter(c -> c.getCreatedType() == createdTypeClass).count() == 1) { // 1 = is the first time
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

	static <T extends ColumnType<?, ?>> void registerTypeSimple(
			final Class<T> createdTypeClass,
			final BiFunction<Optional<AnnotatedType>, HintsOwner, T> provideFunction,
			final List<ColumnTypeFactory<?>> typeMap) {

		if (ColumnTypeRegistry.DEBUG_TYPE_NAMES) {
			final BiFunction<Class<?>, HintsOwner, Integer> biasTypeFunctionRepl = new BiFunction<Class<?>, HintsOwner, Integer>() {

				@Override
				public Integer apply(final Class<?> clazz, final HintsOwner map) {
					return clazz == createdTypeClass ? ColumnTypeRegistry.PERFECT_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE;
				}

				@Override
				public String toString() {
					return createdTypeClass.toString() + " [PERFECT TYPE MATCH]";
				}

			};
			final BiFunction<Optional<AnnotatedType>, HintsOwner, T> provideFunctionRepl = new BiFunction<Optional<AnnotatedType>, HintsOwner, T>() {

				@Override
				public T apply(final Optional<AnnotatedType> t, final HintsOwner u) {
					return provideFunction.apply(t, u);
				}

				@Override
				public String toString() {
					return createdTypeClass.toString();
				}

			};
			typeMap.add(new DelegatingColumnTypeFactory<>(createdTypeClass, biasTypeFunctionRepl, provideFunctionRepl));
		} else {
			typeMap.add(new DelegatingColumnTypeFactory<>(createdTypeClass,
					(clazz, map) -> clazz == createdTypeClass ? ColumnTypeRegistry.PERFECT_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
					provideFunction));
		}
	}

	void registerTypes(List<ColumnTypeFactory<?>> typeMap);

}
