package lu.kbra.pclib.parser.token;

import lu.kbra.pclib.parser.impl.TokenType;

public class CommentToken extends Token {

	protected String value;

	public CommentToken(final TokenType _t, final int _l, final int _c, final String value) {
		super(_t, _l, _c);
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return CommentToken.class.getName() + "[line=" + this.line + ", column=" + this.column + ", type=" + this.type + ", value="
				+ this.value + "]";
	}

}
