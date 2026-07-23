package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.utils.impl.SQLEncodingTypeProvider;
import lu.kbra.pclib.impl.function.TriFunction;

public interface ColumnTypeRegistry {

	String DEBUG_TYPE_NAMES_PROPERTY = ColumnTypeRegistry.class.getSimpleName() + ".debug_type_names";
	boolean DEBUG_TYPE_NAMES = Boolean.getBoolean(ColumnTypeRegistry.DEBUG_TYPE_NAMES_PROPERTY);

	Integer PERFECT_MATCH_SCORE = 200;
	Integer MAP_MATCH_SCORE = 100;
	Integer TYPE_CATCH_ALL_SCORE = 50;
	Integer EXCLUDE = null;

	static <T extends ColumnType<?, ?>> void registerType(
			final Class<T> createdTypeClass,
			final TriFunction<Class<?>, HintsOwner, SQLEncodingTypeProvider, Integer> biasFunction,
			final TriFunction<Optional<AnnotatedType>, HintsOwner, SQLEncodingTypeProvider, T> provideFunction,
			final List<ColumnTypeFactory<?>> typeMap) {

		if (ColumnTypeRegistry.DEBUG_TYPE_NAMES) {
			final TriFunction<Class<?>, HintsOwner, SQLEncodingTypeProvider, Integer> biasFunctionRepl = new TriFunction<Class<?>, HintsOwner, SQLEncodingTypeProvider, Integer>() {

				@Override
				public Integer apply(final Class<?> t, final HintsOwner u, SQLEncodingTypeProvider encodingTypeProvider) {
					return biasFunction.apply(t, u, encodingTypeProvider);
				}

				@Override
				public String toString() {
					return createdTypeClass.toString();
				}

			};
			final TriFunction<Class<?>, HintsOwner, SQLEncodingTypeProvider, Integer> biasTypeFunctionRepl = new TriFunction<Class<?>, HintsOwner, SQLEncodingTypeProvider, Integer>() {

				@Override
				public Integer apply(final Class<?> clazz, final HintsOwner map, SQLEncodingTypeProvider encodingTypeProvider) {
					return clazz == createdTypeClass ? ColumnTypeRegistry.PERFECT_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE;
				}

				@Override
				public String toString() {
					return createdTypeClass.toString() + " [PERFECT TYPE MATCH]";
				}

			};
			final TriFunction<Optional<AnnotatedType>, HintsOwner, SQLEncodingTypeProvider, T> provideFunctionRepl = new TriFunction<Optional<AnnotatedType>, HintsOwner, SQLEncodingTypeProvider, T>() {

				@Override
				public T apply(final Optional<AnnotatedType> t, final HintsOwner u, SQLEncodingTypeProvider encodingTypeProvider) {
					return provideFunction.apply(t, u, encodingTypeProvider);
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
						(clazz, map, encodingTypeProvider) -> clazz == createdTypeClass ? ColumnTypeRegistry.PERFECT_MATCH_SCORE
								: ColumnTypeRegistry.EXCLUDE,
						provideFunction));
			}
		}
	}

	static <T extends ColumnType<?, ?>> void registerTypeSimple(
			final Class<T> createdTypeClass,
			final TriFunction<Optional<AnnotatedType>, HintsOwner, SQLEncodingTypeProvider, T> provideFunction,
			final List<ColumnTypeFactory<?>> typeMap) {

		if (ColumnTypeRegistry.DEBUG_TYPE_NAMES) {
			final TriFunction<Class<?>, HintsOwner, SQLEncodingTypeProvider, Integer> biasTypeFunctionRepl = new TriFunction<Class<?>, HintsOwner, SQLEncodingTypeProvider, Integer>() {

				@Override
				public Integer apply(final Class<?> clazz, final HintsOwner map, SQLEncodingTypeProvider encodingTypeProvider) {
					return clazz == createdTypeClass ? ColumnTypeRegistry.PERFECT_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE;
				}

				@Override
				public String toString() {
					return createdTypeClass.toString() + " [PERFECT TYPE MATCH]";
				}

			};
			final TriFunction<Optional<AnnotatedType>, HintsOwner, SQLEncodingTypeProvider, T> provideFunctionRepl = new TriFunction<Optional<AnnotatedType>, HintsOwner, SQLEncodingTypeProvider, T>() {

				@Override
				public T apply(final Optional<AnnotatedType> t, final HintsOwner u, SQLEncodingTypeProvider encodingTypeProvider) {
					return provideFunction.apply(t, u, encodingTypeProvider);
				}

				@Override
				public String toString() {
					return createdTypeClass.toString();
				}

			};
			typeMap.add(new DelegatingColumnTypeFactory<>(createdTypeClass, biasTypeFunctionRepl, provideFunctionRepl));
		} else {
			typeMap.add(new DelegatingColumnTypeFactory<>(createdTypeClass,
					(clazz, map, encodingTypeProvider) -> clazz == createdTypeClass ? ColumnTypeRegistry.PERFECT_MATCH_SCORE
							: ColumnTypeRegistry.EXCLUDE,
					provideFunction));
		}
	}

	void registerColumnTypes(List<ColumnTypeFactory<?>> typeMap);

}
