package lu.kbra.pclib.parser.token;

import lu.kbra.pclib.parser.impl.TokenType;

public class StringLiteralToken extends LiteralToken {

	protected String value;

	public StringLiteralToken(final TokenType type, final int line, final int column, final String value) {
		super(type, line, column);
		this.value = value;
	}

	public String getEscapedValue() {
		return this.getValue().replace("\\", "\\\\");
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "StringLiteralToken [value=" + this.value + "]";
	}

}
