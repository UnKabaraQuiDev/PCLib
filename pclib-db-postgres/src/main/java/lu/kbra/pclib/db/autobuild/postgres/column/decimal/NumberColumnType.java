package lu.kbra.pclib.db.autobuild.postgres.column.decimal;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.postgres.encoding.decimal.NumericEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

@Getter
@RequiredArgsConstructor
public class NumberColumnType implements ColumnType<Number, BigDecimal> {

	private final EncodingType<BigDecimal> encodingType;

	public NumberColumnType(final int precision, final int scale) {
		this.encodingType = new NumericEncodingType(precision, scale);
	}

	@Override
	public @NonNull Number decode(@NonNull final BigDecimal value, final Type type) {
		if (!(type instanceof Class<?>)) {
			return ColumnType.unsupported(type);
		}

		final Class<?> clazz = (Class<?>) type;

		if (clazz == BigDecimal.class) {
			return value;
		}
		if (clazz == BigInteger.class) {
			return value.toBigIntegerExact();
		}
		if (clazz == Byte.class || clazz == byte.class) {
			return value.byteValueExact();
		}
		if (clazz == Short.class || clazz == short.class) {
			return value.shortValueExact();
		}
		if (clazz == Integer.class || clazz == int.class) {
			return value.intValueExact();
		}
		if (clazz == Long.class || clazz == long.class) {
			return value.longValueExact();
		}
		if (clazz == Float.class || clazz == float.class) {
			return value.floatValue();
		}
		if (clazz == Double.class || clazz == double.class) {
			return value.doubleValue();
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public @NonNull BigDecimal encode(@NonNull final Number value) {
		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}
		if (value instanceof BigInteger) {
			return new BigDecimal((BigInteger) value);
		}
		if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
			return BigDecimal.valueOf(value.longValue());
		}
		if (value instanceof Float || value instanceof Double) {
			return BigDecimal.valueOf(value.doubleValue());
		}

		return new BigDecimal(value.toString());
	}

}
