package lu.kbra.pclib.parser.token;

import lu.kbra.pclib.parser.impl.TokenType;

public class StringLiteralToken extends LiteralToken {

	protected String value;

	public StringLiteralToken(TokenType type, int line, int column, String value) {
		super(type, line, column);
		this.value = value;
	}

	public String getEscapedValue() {
		return getValue().replace("\\", "\\\\");
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "StringLiteralToken [value=" + value + "]";
	}

}
