package lu.kbra.pclib.parser.impl;

public interface TokenType {

	boolean matches(TokenType type);

	char getCharValue();

	String getStringValue();

	boolean isFixed();

	boolean isString();

	default Object getValue() {
		if (!this.isFixed()) {
			throw new UnsupportedOperationException("TokenType " + this.name() + " has no string value.");
		}
		return this.isString() ? this.getStringValue() : this.getCharValue();
	}

	default String getValueAsString() {
		if (!this.isFixed()) {
			throw new UnsupportedOperationException("TokenType " + this.name() + " has no string value.");
		}
		return this.isString() ? this.getStringValue() : Character.toString(this.getCharValue());
	}

	String name();

}
