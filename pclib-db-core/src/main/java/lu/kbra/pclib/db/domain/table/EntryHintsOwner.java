package lu.kbra.pclib.db.domain.table;

import java.util.Map;

public interface EntryHintsOwner {

	Map<String, Object> getEntryHints();

	default <V> V getEntryHint(final String key) {
		return (V) this.getEntryHints().get(key);
	}

	default <V> V getEntryHint(final String key, final V default_) {
		return (V) this.getEntryHints().getOrDefault(key, default_);
	}

	default <V> boolean hasEntryHint(final String key) {
		return this.getEntryHints().containsKey(key);
	}

}
