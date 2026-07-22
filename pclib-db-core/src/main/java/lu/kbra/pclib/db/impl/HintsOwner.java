package lu.kbra.pclib.db.impl;

import java.util.Collections;
import java.util.Map;

import lu.kbra.pclib.db.utils.DelegatingHintOwner;

public interface HintsOwner {

	final HintsOwner EMPTY = new DelegatingHintOwner(Collections.emptyMap());

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

	default boolean getBooleanHint(final String key) {
		return getBooleanHint(key, false);
	}

	default boolean getBooleanHint(final String key, final boolean default_) {
		final Object value = this.getHints().get(key);

		if (value == null) {
			return default_;
		}

		if (value instanceof Boolean) {
			return ((Boolean) value);
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue() != 0.0;
		}

		if (value instanceof CharSequence) {
			final String str = ((CharSequence) value).toString().trim();

			return !str.isEmpty() && !str.equalsIgnoreCase("false");
		}

		return true;
	}

	default String getStringHint(final String key) {
		return getStringHint(key, null);
	}

	default String getStringHint(final String key, final String default_) {
		final Object value = this.getHints().get(key);
		return value == null ? default_ : value.toString();
	}

	default int getIntHint(final String key) {
		return getIntHint(key, 0);
	}

	default int getIntHint(final String key, final int default_) {
		final Object value = this.getHints().get(key);

		if (value == null) {
			return default_;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		if (value instanceof CharSequence) {
			try {
				return Integer.parseInt(((CharSequence) value).toString().trim());
			} catch (NumberFormatException ignored) {
			}
		}

		return default_;
	}

	default long getLongHint(final String key) {
		return getLongHint(key, 0L);
	}

	default long getLongHint(final String key, final long default_) {
		final Object value = this.getHints().get(key);

		if (value == null) {
			return default_;
		}

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		if (value instanceof CharSequence) {
			try {
				return Long.parseLong(((CharSequence) value).toString().trim());
			} catch (NumberFormatException ignored) {
			}
		}

		return default_;
	}

	default float getFloatHint(final String key) {
		return getFloatHint(key, 0f);
	}

	default float getFloatHint(final String key, final float default_) {
		final Object value = this.getHints().get(key);

		if (value == null) {
			return default_;
		}

		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}

		if (value instanceof CharSequence) {
			try {
				return Float.parseFloat(((CharSequence) value).toString().trim());
			} catch (NumberFormatException ignored) {
			}
		}

		return default_;
	}

	default double getDoubleHint(final String key) {
		return getDoubleHint(key, 0d);
	}

	default double getDoubleHint(final String key, final double default_) {
		final Object value = this.getHints().get(key);

		if (value == null) {
			return default_;
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		if (value instanceof CharSequence) {
			try {
				return Double.parseDouble(((CharSequence) value).toString().trim());
			} catch (NumberFormatException ignored) {
			}
		}

		return default_;
	}

}
