import lu.kbra.pclib.parser.impl.TokenType;

public enum CustomTokenTypes implements TokenType {

	MOVE("move"),
	UP("up"),
	DOWN("down"),
	ROTATE("rotate"),
	LOOP("loop"),
	COLOR("color"),
	THICKNESS("thickness");

	private final String stringValue;

	CustomTokenTypes(final String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public boolean matches(final TokenType type) {
		return this == type;
	}

	@Override
	public char getCharValue() {
		throw new UnsupportedOperationException("Custom token type does not have a char value: " + this.name());
	}

	@Override
	public String getStringValue() {
		return this.stringValue;
	}

	@Override
	public boolean isFixed() {
		return true;
	}

	@Override
	public boolean isString() {
		return true;
	}

}
