package lu.kbra.pclib.db.utils.impl;

import java.util.Map;
import java.util.function.Supplier;

import lu.kbra.pclib.PCUtils;

public interface DatabaseEntryUtilsOptionsOwner {

	String FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY = DatabaseEntryUtils.class.getSimpleName() + ".force_default_value_on_non_null";
	boolean FORCE_DEFAULT_VALUE_ON_NON_NULL = PCUtils.getBoolean(DatabaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY,
			false);

	String FAIL_ON_DUPLICATE_FACTORY_METHOD_PROPERTY = DatabaseEntryUtils.class.getSimpleName() + ".fail_on_duplicate_factory_method";
	boolean FAIL_ON_DUPLICATE_FACTORY_METHOD = PCUtils.getBoolean(DatabaseEntryUtilsOptionsOwner.FAIL_ON_DUPLICATE_FACTORY_METHOD_PROPERTY,
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

	void setOptions(Map<String, Object> options);

	default boolean hasOption(final String key) {
		return this.getOptions().containsKey(key);
	}

	default boolean isFailOnDuplicateFactoryMethod() {
		return this.getOptionOrDefault(DatabaseEntryUtilsOptionsOwner.FAIL_ON_DUPLICATE_FACTORY_METHOD_PROPERTY,
				DatabaseEntryUtilsOptionsOwner.FAIL_ON_DUPLICATE_FACTORY_METHOD);
	}

	default boolean isForceDefaultValueOnNonNull() {
		return this.<Boolean>getOptionOrDefault(DatabaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY,
				DatabaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL);
	}

	default <T> Object setOption(final String key, final T value) {
		return this.getOptions().put(key, value);
	}

	default <T> Object unsertOption(final String key, final T value) {
		return this.getOptions().remove(key);
	}

}
