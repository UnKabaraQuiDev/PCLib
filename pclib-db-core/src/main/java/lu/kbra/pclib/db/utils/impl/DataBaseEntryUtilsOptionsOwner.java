package lu.kbra.pclib.db.utils.impl;

import java.util.Map;
import java.util.function.Supplier;

import lu.kbra.pclib.PCUtils;

public interface DataBaseEntryUtilsOptionsOwner {

	String FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY = DataBaseEntryUtils.class.getSimpleName() + ".force_default_value_on_non_null";
	boolean FORCE_DEFAULT_VALUE_ON_NON_NULL = PCUtils.getBoolean(DataBaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY,
			true);

	String FAIL_ON_DUPLICATE_FACTORY_METHOD_PROPERTY = DataBaseEntryUtils.class.getSimpleName() + ".fail_on_duplicate_factory_method";
	boolean FAIL_ON_DUPLICATE_FACTORY_METHOD = PCUtils.getBoolean(DataBaseEntryUtilsOptionsOwner.FAIL_ON_DUPLICATE_FACTORY_METHOD_PROPERTY,
			true);

	String WARN_ON_DUPLICATE_FACTORY_METHOD_PROPERTY = DataBaseEntryUtils.class.getSimpleName() + ".warn_on_duplicate_factory_method";
	boolean WARN_ON_DUPLICATE_FACTORY_METHOD = PCUtils.getBoolean(DataBaseEntryUtilsOptionsOwner.WARN_ON_DUPLICATE_FACTORY_METHOD_PROPERTY,
			true);

	default <T> T getOption(final String key) {
		return (T) this.getOptions().get(key);
	}

	default <T> T getOptionOrDefault(final String key, final Supplier<T> value) {
		if (!this.getOptions().containsKey(key)) {
			return value.get();
		}
		return (T) this.getOptions().get(key);
	}

	default <T> T getOptionOrDefault(final String key, final T value) {
		return (T) this.getOptions().getOrDefault(key, value);
	}

	Map<String, Object> getOptions();

	default boolean hasOption(final String key) {
		return this.getOptions().containsKey(key);
	}

	default boolean isFailOnDuplicateFactoryMethod() {
		return this.getOptionOrDefault(DataBaseEntryUtilsOptionsOwner.FAIL_ON_DUPLICATE_FACTORY_METHOD_PROPERTY,
				DataBaseEntryUtilsOptionsOwner.FAIL_ON_DUPLICATE_FACTORY_METHOD);
	}

	default boolean isForceDefaultValueOnNonNull() {
		return this.<Boolean>getOptionOrDefault(DataBaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY,
				DataBaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL);
	}

	default boolean isWarnOnDuplicateFactoryMethod() {
		return this.getOptionOrDefault(DataBaseEntryUtilsOptionsOwner.WARN_ON_DUPLICATE_FACTORY_METHOD_PROPERTY,
				DataBaseEntryUtilsOptionsOwner.WARN_ON_DUPLICATE_FACTORY_METHOD);
	}

	default <T> Object setOption(final String key, final T value) {
		return this.getOptions().put(key, value);
	}

}
