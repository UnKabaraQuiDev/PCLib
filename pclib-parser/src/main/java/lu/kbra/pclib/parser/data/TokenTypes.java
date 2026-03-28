package lu.kbra.pclib.parser.data;

import lu.kbra.pclib.parser.impl.TokenType;

public enum TokenTypes implements TokenType {

	TRUE("true"), FALSE("false"),

	NUM_LIT(), DEC_NUM_LIT(NUM_LIT), HEX_NUM_LIT(NUM_LIT), BIN_NUM_LIT(NUM_LIT), OCT_NUM_LIT(NUM_LIT),
	CHAR_LIT(NUM_LIT),

	IDENT(),

	COMMA(','), DOT('.'), COLON(':'), SEMICOLON(';'), DOLLAR('$'),

	ARROW("->"),

	PAREN_OPEN('('), PAREN_CLOSE(')'), BRACKET_OPEN('['), BRACKET_CLOSE(']'), CURLY_OPEN('{'), CURLY_CLOSE('}'),

	STRING_LIT(),

	COMMENT("//"), COMMENT_BLOCK(),

	ASSIGN(), STRICT_ASSIGN(ASSIGN, '='),

	BIT_OR('|'), BIT_AND('&'), BIT_XOR('^'), BIT_NOT('~'),

	BIT_OR_ASSIGN(ASSIGN, "|="), BIT_AND_ASSIGN(ASSIGN, "&="), BIT_XOR_ASSIGN(ASSIGN, "^="),
	BIT_NOT_ASSIGN(ASSIGN, "~="),

	BIT_SHIFT(), SIGNED_BIT_SHIFT(BIT_SHIFT),

	UNSIGNED_BIT_SHIFT_LEFT("<<<"), UNSIGNED_BIT_SHIFT_RIGHT(">>>"), SIGNED_BIT_SHIFT_LEFT("<<"),
	SIGNED_BIT_SHIFT_RIGHT(">>"),

	OR("||"), AND("&&"), NOT('!'), XOR("^^"),

	HASH('#'),

	PLUS('+'), MINUS('-'), MUL('*'), DIV('/'), MODULO('%'),

	PLUS_PLUS("++"), MINUS_MINUS("--"),

	PLUS_ASSIGN(ASSIGN, "+="), MINUS_ASSIGN(ASSIGN, "-="), MUL_ASSIGN(ASSIGN, "*="), DIV_ASSIGN(ASSIGN, "/="),
	MODULO_ASSIGN(ASSIGN, "%="),

	EQUALS("=="), NOT_EQUALS("!="),

	LESS('<'), LESS_EQUALS("<="),

	GREATER('>'), GREATER_EQUALS(">=");

	private TokenTypes parent;
	private boolean fixed = false;
	private boolean string = false;
	private String stringValue;
	private Character charValue;

	private TokenTypes() {
		this.fixed = false;
	}

	private TokenTypes(final TokenTypes parent) {
		this.fixed = false;
		this.parent = parent;
	}

	private TokenTypes(final char cha) {
		this.fixed = true;
		this.string = false;
		this.charValue = cha;
	}

	private TokenTypes(final TokenTypes parent, final char cha) {
		this.fixed = true;
		this.string = false;
		this.charValue = cha;
		this.parent = parent;
	}

	private TokenTypes(final String str) {
		this.fixed = true;
		this.string = true;
		this.stringValue = str;
	}

	private TokenTypes(final TokenTypes parent, final String str) {
		this.fixed = true;
		this.string = true;
		this.stringValue = str;
		this.parent = parent;
	}

	@Override
	public boolean matches(final TokenType type) {
		return this.equals(type) || (this.parent != null ? this.parent.matches(type) : false);
	}

	@Override
	public boolean isFixed() {
		return this.fixed;
	}

	@Override
	public boolean isString() {
		return this.string;
	}

	@Override
	public String getStringValue() {
		return this.stringValue;
	}

	@Override
	public char getCharValue() {
		return this.charValue;
	}

	@Override
	public String toString() {
		if (this.fixed && this.string) {
			return TokenTypes.class.getSimpleName() + "[" + this.name() + ", fixed=" + this.fixed + ", string="
					+ this.string + ", stringValue=" + this.stringValue + "]";
		} else if (this.fixed && !this.string) {
			return TokenTypes.class.getSimpleName() + "[" + this.name() + ", fixed=" + this.fixed + ", string="
					+ this.string + ", charValue=" + this.charValue + "]";
		} else {
			return TokenTypes.class.getSimpleName() + "[" + this.name() + ", fixed=" + this.fixed + ", string="
					+ this.string + "]";
		}
	}

	public String toShortString() {
		return TokenTypes.class.getSimpleName() + "[" + this.name() + "]";
	}

}
