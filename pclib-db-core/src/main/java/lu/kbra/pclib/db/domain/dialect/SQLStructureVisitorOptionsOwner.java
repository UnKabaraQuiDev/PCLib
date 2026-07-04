package lu.kbra.pclib.db.domain.dialect;

import java.util.Map;
import java.util.function.Supplier;

public interface SQLStructureVisitorOptionsOwner {

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

	default <T> Object setOption(final String key, final T value) {
		return this.getOptions().put(key, value);
	}

	default <T> Object unsertOption(final String key, final T value) {
		return this.getOptions().remove(key);
	}

}
