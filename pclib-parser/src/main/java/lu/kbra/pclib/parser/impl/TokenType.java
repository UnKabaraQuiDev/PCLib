package lu.kbra.pclib.parser.impl;

public interface TokenType {

	char getCharValue();

	String getStringValue();

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

	boolean isFixed();

	boolean isString();

	boolean matches(TokenType type);

	String name();

}
