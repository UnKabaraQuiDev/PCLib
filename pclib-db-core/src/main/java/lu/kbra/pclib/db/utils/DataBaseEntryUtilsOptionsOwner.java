package lu.kbra.pclib.db.utils;

import java.util.Map;
import java.util.function.Supplier;

import lu.kbra.pclib.PCUtils;

public interface DataBaseEntryUtilsOptionsOwner {

	String FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY = DataBaseEntryUtils.class.getSimpleName() + ".force_default_value_on_non_null";
	boolean FORCE_DEFAULT_VALUE_ON_NON_NULL = PCUtils.getBoolean(DataBaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY,
			true);

	Map<String, Object> getOptions();

	default boolean isForceDefaultValueOnNonNull() {
		return this.<Boolean>getOptionOrDefault(DataBaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY,
				DataBaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL);
	}

	default <T> Object setOption(final String key, final T value) {
		return getOptions().put(key, value);
	}

	default boolean hasOption(final String key) {
		return getOptions().containsKey(key);
	}

	default <T> T getOption(final String key) {
		return (T) this.getOptions().get(key);
	}

	default <T> T getOptionOrDefault(final String key, final T value) {
		return (T) this.getOptions().getOrDefault(key, value);
	}

	default <T> T getOptionOrDefault(final String key, final Supplier<T> value) {
		if (!this.getOptions().containsKey(key)) {
			return value.get();
		}
		return (T) this.getOptions().get(key);
	}

}
