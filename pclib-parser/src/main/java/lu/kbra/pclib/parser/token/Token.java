package lu.kbra.pclib.parser.token;

import lu.kbra.pclib.parser.impl.TokenType;

public class Token {

	protected int line;
	protected int column;
	protected TokenType type;

	public Token(final TokenType tokenType, final int line, final int column) {
		this.type = tokenType;
		this.line = line + 1;
		this.column = column + 1;
	}

	public int getColumn() {
		return this.column;
	}

	public int getLine() {
		return this.line;
	}

	public String getPosition() {
		return this.line + ":" + this.column;
	}

	public TokenType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return "Token [line=" + this.line + ", column=" + this.column + ", type=" + this.type + "]";
	}

	public String toString(final int i) {
		return "'" + this.type.name() + "' at " + this.getPosition();
	}

}
