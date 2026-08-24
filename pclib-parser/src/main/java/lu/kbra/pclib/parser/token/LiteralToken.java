package lu.kbra.pclib.parser.token;

import lu.kbra.pclib.parser.impl.TokenType;

public class LiteralToken extends Token {

	public LiteralToken(final TokenType token, final int line, final int column) {
		super(token, line, column);
	}

}
