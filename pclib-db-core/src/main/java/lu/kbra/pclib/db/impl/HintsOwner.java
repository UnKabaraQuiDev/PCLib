package lu.kbra.pclib.db.impl;

import java.util.Collections;
import java.util.Map;
import java.util.function.BooleanSupplier;

import lu.kbra.pclib.db.utils.DelegatingHintOwner;

public interface HintsOwner {

	HintsOwner EMPTY = new DelegatingHintOwner(Collections.emptyMap());

	Map<String, Object> getHints();

	static <V> V getHint(final Map<String, Object> hints, final String key) {
		return (V) hints.get(key);
	}

	static <V> V getHint(final Map<String, Object> hints, final String key, final V default_) {
		final V value = HintsOwner.getHint(hints, key);
		return value == null ? default_ : value;
	}

	static boolean hasHint(final Map<String, Object> hints, final String key) {
		return hints.containsKey(key);
	}

	static boolean getBooleanHint(final Map<String, Object> hints, final String key) {
		return HintsOwner.getBooleanHint(hints, key, false);
	}

	static boolean getBooleanHint(final Map<String, Object> hints, final String key, final boolean default_) {
		final Object value = hints.get(key);

		if (value == null) {
			return default_;
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue() != 0.0;
		}

		if (value instanceof CharSequence) {
			final String str = value.toString().trim();
			return !str.isEmpty() && !"false".equalsIgnoreCase(str);
		}

		return true;
	}

	static boolean getBooleanHint(final Map<String, Object> hints, final String key, final BooleanSupplier default_) {
		final Object value = hints.get(key);

		if (value == null) {
			return default_.getAsBoolean();
		}

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue() != 0.0;
		}

		if (value instanceof CharSequence) {
			final String str = value.toString().trim();
			return !str.isEmpty() && !"false".equalsIgnoreCase(str);
		}

		return true;
	}

	static String getStringHint(final Map<String, Object> hints, final String key) {
		return HintsOwner.getStringHint(hints, key, null);
	}

	static String getStringHint(final Map<String, Object> hints, final String key, final String default_) {
		final Object value = hints.get(key);
		return value == null ? default_ : value.toString();
	}

	static int getIntHint(final Map<String, Object> hints, final String key) {
		return HintsOwner.getIntHint(hints, key, 0);
	}

	static int getIntHint(final Map<String, Object> hints, final String key, final int default_) {
		final Object value = hints.get(key);

		if (value == null) {
			return default_;
		}

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		if (value instanceof CharSequence) {
			try {
				return Integer.parseInt(value.toString().trim());
			} catch (final NumberFormatException ignored) {
			}
		}

		return default_;
	}

	static long getLongHint(final Map<String, Object> hints, final String key) {
		return HintsOwner.getLongHint(hints, key, 0L);
	}

	static long getLongHint(final Map<String, Object> hints, final String key, final long default_) {
		final Object value = hints.get(key);

		if (value == null) {
			return default_;
		}

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		if (value instanceof CharSequence) {
			try {
				return Long.parseLong(value.toString().trim());
			} catch (final NumberFormatException ignored) {
			}
		}

		return default_;
	}

	static float getFloatHint(final Map<String, Object> hints, final String key) {
		return HintsOwner.getFloatHint(hints, key, 0f);
	}

	static float getFloatHint(final Map<String, Object> hints, final String key, final float default_) {
		final Object value = hints.get(key);

		if (value == null) {
			return default_;
		}

		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}

		if (value instanceof CharSequence) {
			try {
				return Float.parseFloat(value.toString().trim());
			} catch (final NumberFormatException ignored) {
			}
		}

		return default_;
	}

	static double getDoubleHint(final Map<String, Object> hints, final String key) {
		return HintsOwner.getDoubleHint(hints, key, 0d);
	}

	static double getDoubleHint(final Map<String, Object> hints, final String key, final double default_) {
		final Object value = hints.get(key);

		if (value == null) {
			return default_;
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		if (value instanceof CharSequence) {
			try {
				return Double.parseDouble(value.toString().trim());
			} catch (final NumberFormatException ignored) {
			}
		}

		return default_;
	}

	default <V> V getHint(final String key) {
		return HintsOwner.getHint(this.getHints(), key);
	}

	default <V> V getHint(final String key, final V default_) {
		return HintsOwner.getHint(this.getHints(), key, default_);
	}

	default boolean hasHint(final String key) {
		return HintsOwner.hasHint(this.getHints(), key);
	}

	default boolean getBooleanHint(final String key) {
		return HintsOwner.getBooleanHint(this.getHints(), key);
	}

	default boolean getBooleanHint(final String key, final boolean default_) {
		return HintsOwner.getBooleanHint(this.getHints(), key, default_);
	}

	default boolean getBooleanHint(final String key, final BooleanSupplier default_) {
		return HintsOwner.getBooleanHint(this.getHints(), key, default_);
	}

	default String getStringHint(final String key) {
		return HintsOwner.getStringHint(this.getHints(), key);
	}

	default String getStringHint(final String key, final String default_) {
		return HintsOwner.getStringHint(this.getHints(), key, default_);
	}

	default int getIntHint(final String key) {
		return HintsOwner.getIntHint(this.getHints(), key);
	}

	default int getIntHint(final String key, final int default_) {
		return HintsOwner.getIntHint(this.getHints(), key, default_);
	}

	default long getLongHint(final String key) {
		return HintsOwner.getLongHint(this.getHints(), key);
	}

	default long getLongHint(final String key, final long default_) {
		return HintsOwner.getLongHint(this.getHints(), key, default_);
	}

	default float getFloatHint(final String key) {
		return HintsOwner.getFloatHint(this.getHints(), key);
	}

	default float getFloatHint(final String key, final float default_) {
		return HintsOwner.getFloatHint(this.getHints(), key, default_);
	}

	default double getDoubleHint(final String key) {
		return HintsOwner.getDoubleHint(this.getHints(), key);
	}

	default double getDoubleHint(final String key, final double default_) {
		return HintsOwner.getDoubleHint(this.getHints(), key, default_);
	}

}
