package lu.kbra.pclib.parser.token;

import lu.kbra.pclib.parser.impl.TokenType;

public class IdentifierToken extends Token {

	protected String identifier;

	public IdentifierToken(TokenType type, int line, int column, String strValue) {
		super(type, line, column);
		this.identifier = strValue;
	}

	public String getValue() {
		return identifier;
	}

	@Override
	public String toString() {
		return IdentifierToken.class.getName() + "[line=" + line + ", column=" + column + ", type=" + type
				+ ", identifier=" + identifier + "]";
	}

}
