package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;

public interface ColumnTypeRegistry {

	String DEBUG_TYPE_NAMES_PROPERTY = ColumnTypeRegistry.class.getSimpleName() + ".debug_type_names";
	boolean DEBUG_TYPE_NAMES = Boolean.getBoolean(ColumnTypeRegistry.DEBUG_TYPE_NAMES_PROPERTY);

	Integer PERFECT_MATCH_SCORE = 200;
	Integer MAP_MATCH_SCORE = 100;
	Integer TYPE_CATCH_ALL_SCORE = 50;
	Integer EXCLUDE = null;

	default void registerType(
			final Class<? extends ColumnType> createdTypeClass,
			final BiFunction<Class<?>, Map<String, Object>, Integer> biasFunction,
			final BiFunction<Optional<AnnotatedType>, Map<String, Object>, ColumnType> provideFunction,
			final Map<BiFunction<Class<?>, Map<String, Object>, Integer>, BiFunction<Optional<AnnotatedType>, Map<String, Object>, ColumnType>> typeMap) {

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
			final BiFunction<Optional<AnnotatedType>, Map<String, Object>, ColumnType> provideFunctionRepl = new BiFunction<Optional<AnnotatedType>, Map<String, Object>, ColumnType>() {

				@Override
				public ColumnType apply(final Optional<AnnotatedType> t, final Map<String, Object> u) {
					return provideFunction.apply(t, u);
				}

				@Override
				public String toString() {
					return createdTypeClass.toString();
				}

			};
			typeMap.put(biasFunctionRepl, provideFunctionRepl);
			typeMap.put(biasTypeFunctionRepl, provideFunctionRepl);
		} else {
			typeMap.put(biasFunction, provideFunction);
			typeMap.put((clazz, map) -> clazz == createdTypeClass ? ColumnTypeRegistry.PERFECT_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
					provideFunction);
		}
	}

	void registerTypes(
			Map<BiFunction<Class<?>, Map<String, Object>, Integer>, BiFunction<Optional<AnnotatedType>, Map<String, Object>, ColumnType>> typeMap);

}
