package lu.kbra.pclib.parser.token;

import lu.kbra.pclib.parser.impl.TokenType;

public class Token {

	protected int line;
	protected int column;
	protected TokenType type;

	public Token(final TokenType _t, final int _l, final int _c) {
		this.type = _t;
		this.line = _l + 1;
		this.column = _c + 1;
	}

	public int getColumn() {
		return this.column;
	}

	public int getLine() {
		return this.line;
	}

	public TokenType getType() {
		return this.type;
	}

	public String getPosition() {
		return this.line + ":" + this.column;
	}

	@Override
	public String toString() {
		return "Token [line=" + this.line + ", column=" + this.column + ", type=" + this.type + "]";
	}

	public String toString(final int i) {
		return "'" + this.type.name() + "' at " + this.getPosition();
	}

}
