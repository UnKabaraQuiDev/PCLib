package lu.kbra.pclib.db.utils.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import lu.kbra.pclib.datastructure.tuple.Pairs;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyPair;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.impl.HintsOwner;

public interface EncodingTypeRegistry {

	String DEBUG_TYPE_NAMES_PROPERTY = EncodingTypeRegistry.class.getSimpleName() + ".debug_type_names";
	boolean DEBUG_TYPE_NAMES = Boolean.getBoolean(EncodingTypeRegistry.DEBUG_TYPE_NAMES_PROPERTY);

	Integer PERFECT_MATCH_SCORE = 200;
	Integer MAP_MATCH_SCORE = 150;
	Integer STORED_TYPE_MATCH_SCORE = 100;
	Integer TYPE_CATCH_ALL_SCORE = 50;
	Integer EXCLUDE = null;

	public static final Map<ReadOnlyPair<Class<? extends EncodingType<?>>, Object>, EncodingType<?>> FIXED_ENCODING_TYPES = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <Tjdbc, Tec extends EncodingType<Tjdbc>> Tec
			getFixedEncodingType(final Class<? extends Tec> clazz, final Supplier<? extends Tec> supplier) {
		return (Tec) FIXED_ENCODING_TYPES.computeIfAbsent(Pairs.readOnly(clazz, null), c -> supplier.get());
	}

	@SuppressWarnings("unchecked")
	public static <Tjdbc, Tec extends EncodingType<Tjdbc>, Tparam> Tec
			getFixedEncodingType(final Class<? extends Tec> clazz, final Tparam param, final Function<Tparam, ? extends Tec> supplier) {
		return (Tec) FIXED_ENCODING_TYPES.computeIfAbsent(Pairs.readOnly(clazz, param), c -> supplier.apply(param));
	}

	static <T extends EncodingType<V>, V> void registerType(
			final Class<T> createdTypeClass,
			final Class<V> storedTypeClass,
			final BiFunction<Class<?>, HintsOwner, Integer> biasFunction,
			final Function<HintsOwner, T> provideFunction,
			final List<EncodingTypeFactory<?, ?>> typeMap) {

		if (EncodingTypeRegistry.DEBUG_TYPE_NAMES) {
			final BiFunction<Class<?>, HintsOwner, Integer> biasFunctionRepl = new BiFunction<Class<?>, HintsOwner, Integer>() {

				@Override
				public Integer apply(final Class<?> t, final HintsOwner u) {
					return biasFunction.apply(t, u);
				}

				@Override
				public String toString() {
					return createdTypeClass.toString() + " (" + storedTypeClass.toString() + ")";
				}

			};
			final BiFunction<Class<?>, HintsOwner, Integer> biasTypeFunctionRepl = new BiFunction<Class<?>, HintsOwner, Integer>() {

				@Override
				public Integer apply(final Class<?> clazz, final HintsOwner map) {
					return clazz == createdTypeClass ? EncodingTypeRegistry.PERFECT_MATCH_SCORE : EncodingTypeRegistry.EXCLUDE;
				}

				@Override
				public String toString() {
					return createdTypeClass.toString() + " (" + storedTypeClass.toString() + ") [PERFECT TYPE MATCH]";
				}

			};
			final Function<HintsOwner, T> provideFunctionRepl = new Function<HintsOwner, T>() {

				@Override
				public T apply(final HintsOwner u) {
					return provideFunction.apply(u);
				}

				@Override
				public String toString() {
					return createdTypeClass.toString() + " (" + storedTypeClass.toString() + ")";
				}

			};
			typeMap.add(new DelegatingEncodingTypeFactory<>(createdTypeClass, storedTypeClass, biasFunctionRepl, provideFunctionRepl));
			if (typeMap.stream().filter(c -> c.getCreatedType() == createdTypeClass).count() == 1) { // 1 = is the first time
				typeMap.add(
						new DelegatingEncodingTypeFactory<>(createdTypeClass, storedTypeClass, biasTypeFunctionRepl, provideFunctionRepl));
			}
		} else {
			typeMap.add(new DelegatingEncodingTypeFactory<>(createdTypeClass, storedTypeClass, biasFunction, provideFunction));
			if (typeMap.stream().filter(c -> c.getCreatedType() == createdTypeClass).count() == 1) {
				typeMap.add(new DelegatingEncodingTypeFactory<>(createdTypeClass,
						storedTypeClass,
						(clazz, map) -> clazz == createdTypeClass ? EncodingTypeRegistry.PERFECT_MATCH_SCORE : EncodingTypeRegistry.EXCLUDE,
						provideFunction));
			}
		}
	}

	static <T extends EncodingType<V>, V> void registerTypeSimple(
			final Class<T> createdTypeClass,
			final Class<V> storedTypeClass,
			final Function<HintsOwner, T> provideFunction,
			final List<EncodingTypeFactory<?, ?>> typeMap) {

		if (EncodingTypeRegistry.DEBUG_TYPE_NAMES) {
			final BiFunction<Class<?>, HintsOwner, Integer> biasTypeFunctionRepl = new BiFunction<Class<?>, HintsOwner, Integer>() {

				@Override
				public Integer apply(final Class<?> clazz, final HintsOwner map) {
					return clazz == createdTypeClass ? EncodingTypeRegistry.PERFECT_MATCH_SCORE : EncodingTypeRegistry.EXCLUDE;
				}

				@Override
				public String toString() {
					return createdTypeClass.toString() + " (" + storedTypeClass.toString() + ") [PERFECT TYPE MATCH]";
				}

			};
			final Function<HintsOwner, T> provideFunctionRepl = new Function<HintsOwner, T>() {

				@Override
				public T apply(final HintsOwner u) {
					return provideFunction.apply(u);
				}

				@Override
				public String toString() {
					return createdTypeClass.toString() + " (" + storedTypeClass.toString() + ")";
				}

			};
			typeMap.add(new DelegatingEncodingTypeFactory<>(createdTypeClass, storedTypeClass, biasTypeFunctionRepl, provideFunctionRepl));
		} else {
			typeMap.add(new DelegatingEncodingTypeFactory<>(createdTypeClass,
					storedTypeClass,
					(clazz, map) -> clazz == createdTypeClass ? EncodingTypeRegistry.PERFECT_MATCH_SCORE : EncodingTypeRegistry.EXCLUDE,
					provideFunction));
		}
	}

	void registerEncodingTypes(List<EncodingTypeFactory<?, ?>> typeMap);

}
