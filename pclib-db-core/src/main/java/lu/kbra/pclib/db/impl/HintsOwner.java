package lu.kbra.pclib.db.impl;

import java.util.Map;

public interface HintsOwner {

	Map<String, Object> getHints();

	default <V> V getHint(final String key) {
		return (V) this.getHints().get(key);
	}

	default <V> V getHint(final String key, final V default_) {
		return (V) this.getHints().getOrDefault(key, default_);
	}

	default <V> boolean hasHint(final String key) {
		return this.getHints().containsKey(key);
	}

}
