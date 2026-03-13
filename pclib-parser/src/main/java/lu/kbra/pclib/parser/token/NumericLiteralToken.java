package lu.kbra.pclib.parser.token;

import static lu.kbra.pclib.parser.data.TokenTypes.NUM_LIT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lu.kbra.pclib.parser.data.TokenTypes;
import lu.kbra.pclib.parser.data.ValueType;
import lu.kbra.pclib.parser.impl.TokenType;

public class NumericLiteralToken extends LiteralToken {

	public static enum NumericValueType {
		//@formatter:off
		BOOL_1("bool", 1, ValueType.BOOLEAN, false),

		FLOAT_64("double", 8, ValueType.DOUBLE, true),
		FLOAT_32("float", 4, ValueType.FLOAT, true),

		INT_128("int128", 16, ValueType.INT_128, false),
		INT_64("int64", 8, ValueType.INT_64, false),
		INT_32("int32", 4, ValueType.INT_32, false),
		INT_16("int16", 2, ValueType.INT_16, false),


		INT_128_SIGNED("int128s", 16, ValueType.INT_128_SIGNED, true),
		INT_64_SIGNED("int64s", 8, ValueType.INT_64_SIGNED, true),
		INT_32_SIGNED("int32s", 4, ValueType.INT_32_SIGNED, true),
		INT_16_SIGNED("int16s", 2, ValueType.INT_16_SIGNED, true),

		CHAR("char", 1, ValueType.CHAR, false),
		INT_8("int8", 1, ValueType.INT_8, false),

		INT_8_SIGNED("int8s", 1, ValueType.INT_8_SIGNED, true);
		//@formatter:on

		private final String name;
		private final int bytes;
		private final ValueType tt;
		private final boolean signed;

		private NumericValueType(final String name, final int bytes, final ValueType tt, final boolean signed) {
			this.name = name;
			this.bytes = bytes;
			this.tt = tt;
			this.signed = signed;
		}

		public String getName() {
			return this.name;
		}

		public int getBytes() {
			return this.bytes;
		}

		public ValueType getTokenType() {
			return this.tt;
		}

		public boolean isSigned() {
			return this.signed;
		}

		public boolean isUnsigned() {
			return !this.signed;
		}

		public static NumericValueType byTokenType(final ValueType tt) {
			for (final NumericValueType v : NumericValueType.values()) {
				if (v.tt == tt) {
					return v;
				}
			}
			return null;
		}

	}

	protected String literal;
	protected Object value;
	protected NumericValueType valueType;

	public NumericLiteralToken(final TokenType type, final int line, final int column, final String literal,
			final NumericValueType valueType, final Object value) {
		super(type, line, column);
		this.valueType = valueType;
		this.value = value;
		this.literal = literal;
	}

	public static NumericLiteralToken parseNumeric(final TokenTypes tokenType, final int line, final int column,
			final String literal) {
		final String intPattern = "^(\\d+)([bBsSlLdDfF]{0,2})$";
		final String floatPattern = "^(\\d+\\.?\\d+)([dDfF]?)$";
		final String boolPattern = "^(true|false)$";

		final Matcher matcher;
		if (literal.matches(boolPattern)) {
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.BOOL_1,
					Boolean.parseBoolean(literal));
		}

		switch (tokenType) {
		case NUM_LIT:
			matcher = Pattern.compile(intPattern).matcher(literal);
			if (matcher.matches()) {
				final String number = matcher.group(1);
				final String suffix = matcher.group(2).toLowerCase();
				return parseInteger(tokenType, line, column, literal, number, suffix);
			}
			break;

		case DEC_NUM_LIT:
			matcher = Pattern.compile(floatPattern).matcher(literal);
			if (matcher.matches()) {
				final String number = matcher.group(1);
				final String suffix = matcher.group(2).toLowerCase();
				if ("f".equals(suffix)) {
					return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.FLOAT_32,
							Float.parseFloat(number));
				} else {
					return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.FLOAT_64,
							Double.parseDouble(number));
				}
			}
			break;

		case HEX_NUM_LIT:
			if (literal.startsWith("0x")) {
				final String number = literal.substring(2);
				final long parsedValue = Long.parseUnsignedLong(number, 16);
				return parseHexOrBinValue(tokenType, line, column, literal, parsedValue);
			}
			break;

		case BIN_NUM_LIT:
			if (literal.startsWith("0b")) {
				final String number = literal.substring(2);
				final long parsedValue = Long.parseUnsignedLong(number, 2);
				return parseHexOrBinValue(tokenType, line, column, literal, parsedValue);
			}
			break;

		case OCT_NUM_LIT:
			if (literal.startsWith("0o")) {
				final String number = literal.substring(2);
				final long parsedValue = Long.parseUnsignedLong(number, 8);
				return parseHexOrBinValue(tokenType, line, column, literal, parsedValue);
			}
			break;

		case CHAR_LIT:
			if (literal.length() != 1) {
				throw new IllegalArgumentException(
						"Only one character allowed for type " + tokenType + " (" + literal + ")");
			}
			return new NumericLiteralToken(tokenType, line, column, literal, NumericValueType.CHAR, literal.charAt(0));

		default:
			throw new IllegalArgumentException("Invalid numeric literal: " + literal + " for type " + tokenType);
		}

		throw new IllegalArgumentException("Invalid numeric literal: " + literal + " for type " + tokenType);
	}

	private static NumericLiteralToken parseInteger(final TokenType TokenType, final int line, final int column,
			final String literal, final String number, final String suffix) {
		switch (suffix) {
		case "b":
			return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.INT_8,
					Byte.parseByte(number));
		case "s":
			return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.INT_16,
					Short.parseShort(number));
		case "l":
			return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.INT_64,
					Long.parseLong(number));
		case "ll":
			return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.INT_128,
					new java.math.BigInteger(number));
		case "d":
			return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.FLOAT_64,
					Double.parseDouble(number));
		case "f":
			return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.FLOAT_32,
					Float.parseFloat(number));
		default:
			// Default to INT_32 unless it's too large
			final long longValue = Long.parseLong(number);
			if (longValue <= Integer.MAX_VALUE) {
				return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.INT_32,
						(int) longValue);
			} else if (longValue <= Long.MAX_VALUE) {
				return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.INT_64, longValue);
			} else {
				return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.INT_128,
						new java.math.BigInteger(number));
			}
		}
	}

	private static NumericLiteralToken parseHexOrBinValue(final TokenType TokenType, final int line, final int column,
			final String literal, final long value) {
		if (value <= Byte.MAX_VALUE) {
			return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.INT_8, (byte) value);
		} else if (value <= Short.MAX_VALUE) {
			return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.INT_16, (short) value);
		} else if (value <= Integer.MAX_VALUE) {
			return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.INT_32, (int) value);
		} else if (value <= Long.MAX_VALUE) {
			return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.INT_64, value);
		} else {
			return new NumericLiteralToken(TokenType, line, column, literal, NumericValueType.INT_128,
					new java.math.BigInteger(String.valueOf(value)));
		}
	}

	public String getLiteral() {
		return this.literal;
	}

	public boolean isDouble() {
		return this.valueType == NumericValueType.FLOAT_64;
	}

	public boolean isFloat() {
		return this.valueType == NumericValueType.FLOAT_32;
	}

	public boolean isDecimal() {
		return this.isDouble() || this.isFloat();
	}

	public boolean isInteger() {
		return !this.isDecimal();
	}

	public boolean isBool() {
		return this.valueType == NumericValueType.BOOL_1;
	}

	public byte byteValue() {
		return (byte) this.value;
	}

	public short shortValue() {
		return (short) this.value;
	}

	public int intValue() {
		return (int) this.value;
	}

	public long longValue() {
		return (long) this.value;
	}

	public float floatValue() {
		return (float) this.value;
	}

	public double doubleValue() {
		return (double) this.value;
	}

	public boolean booleanValue() {
		return (boolean) this.value;
	}

	public boolean isBoolean() {
		return this.valueType == NumericValueType.BOOL_1;
	}

	public Object getValue() {
		return this.value;
	}

	public NumericValueType getValueType() {
		return this.valueType;
	}

	@Override
	public String toString() {
		return "NumericLiteralToken [literal=" + this.literal + ", value=" + this.value + ", valueType="
				+ this.valueType + "]";
	}

}
