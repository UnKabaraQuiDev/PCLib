package lu.kbra.pclib.parser.impl;

public interface TokenType {

	boolean matches(TokenType type);

	char getCharValue();

	String getStringValue();

	boolean isFixed();

	boolean isString();

	default Object getValue() {
		if (!isFixed()) {
			throw new UnsupportedOperationException("TokenType " + name() + " has no string value.");
		}
		return (isString() ? getStringValue() : getCharValue());
	}

	default String getValueAsString() {
		if (!isFixed()) {
			throw new UnsupportedOperationException("TokenType " + name() + " has no string value.");
		}
		return (isString() ? getStringValue() : Character.toString(getCharValue()));
	}

	String name();

}
