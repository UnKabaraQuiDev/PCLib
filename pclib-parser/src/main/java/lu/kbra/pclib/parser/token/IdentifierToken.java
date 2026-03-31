package lu.kbra.pclib.parser.token;

import lu.kbra.pclib.parser.impl.TokenType;

public class IdentifierToken extends Token {

	protected String identifier;

	public IdentifierToken(final TokenType type, final int line, final int column, final String strValue) {
		super(type, line, column);
		this.identifier = strValue;
	}

	public String getValue() {
		return this.identifier;
	}

	@Override
	public String toString() {
		return IdentifierToken.class.getName() + "[line=" + this.line + ", column=" + this.column + ", type=" + this.type + ", identifier="
				+ this.identifier + "]";
	}

}
