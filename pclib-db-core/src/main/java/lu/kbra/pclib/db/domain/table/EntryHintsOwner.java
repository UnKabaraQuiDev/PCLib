package lu.kbra.pclib.db.domain.table;

import java.util.Map;

public interface EntryHintsOwner {

	Map<String, Object> getEntryHints();

	default <V> V getEntryHint(final String key) {
		return (V) getEntryHints().get(key);
	}

	default <V> V getEntryHint(final String key, final V default_) {
		return (V) getEntryHints().getOrDefault(key, default_);
	}

	default <V> boolean hasEntryHint(final String key) {
		return getEntryHints().containsKey(key);
	}

}
