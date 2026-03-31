package lu.kbra.pclib.parser;

import static lu.kbra.pclib.parser.data.TokenTypes.AND;
import static lu.kbra.pclib.parser.data.TokenTypes.ARROW;
import static lu.kbra.pclib.parser.data.TokenTypes.BIN_NUM_LIT;
import static lu.kbra.pclib.parser.data.TokenTypes.BIT_AND;
import static lu.kbra.pclib.parser.data.TokenTypes.BIT_AND_ASSIGN;
import static lu.kbra.pclib.parser.data.TokenTypes.BIT_NOT;
import static lu.kbra.pclib.parser.data.TokenTypes.BIT_NOT_ASSIGN;
import static lu.kbra.pclib.parser.data.TokenTypes.BIT_OR;
import static lu.kbra.pclib.parser.data.TokenTypes.BIT_OR_ASSIGN;
import static lu.kbra.pclib.parser.data.TokenTypes.BIT_XOR;
import static lu.kbra.pclib.parser.data.TokenTypes.BIT_XOR_ASSIGN;
import static lu.kbra.pclib.parser.data.TokenTypes.BRACKET_CLOSE;
import static lu.kbra.pclib.parser.data.TokenTypes.BRACKET_OPEN;
import static lu.kbra.pclib.parser.data.TokenTypes.CHAR_LIT;
import static lu.kbra.pclib.parser.data.TokenTypes.COLON;
import static lu.kbra.pclib.parser.data.TokenTypes.COMMA;
import static lu.kbra.pclib.parser.data.TokenTypes.COMMENT;
import static lu.kbra.pclib.parser.data.TokenTypes.COMMENT_BLOCK;
import static lu.kbra.pclib.parser.data.TokenTypes.CURLY_CLOSE;
import static lu.kbra.pclib.parser.data.TokenTypes.CURLY_OPEN;
import static lu.kbra.pclib.parser.data.TokenTypes.DEC_NUM_LIT;
import static lu.kbra.pclib.parser.data.TokenTypes.DIV;
import static lu.kbra.pclib.parser.data.TokenTypes.DIV_ASSIGN;
import static lu.kbra.pclib.parser.data.TokenTypes.DOLLAR;
import static lu.kbra.pclib.parser.data.TokenTypes.DOT;
import static lu.kbra.pclib.parser.data.TokenTypes.EQUALS;
import static lu.kbra.pclib.parser.data.TokenTypes.FALSE;
import static lu.kbra.pclib.parser.data.TokenTypes.GREATER;
import static lu.kbra.pclib.parser.data.TokenTypes.GREATER_EQUALS;
import static lu.kbra.pclib.parser.data.TokenTypes.HASH;
import static lu.kbra.pclib.parser.data.TokenTypes.HEX_NUM_LIT;
import static lu.kbra.pclib.parser.data.TokenTypes.IDENT;
import static lu.kbra.pclib.parser.data.TokenTypes.LESS;
import static lu.kbra.pclib.parser.data.TokenTypes.LESS_EQUALS;
import static lu.kbra.pclib.parser.data.TokenTypes.MINUS;
import static lu.kbra.pclib.parser.data.TokenTypes.MINUS_ASSIGN;
import static lu.kbra.pclib.parser.data.TokenTypes.MINUS_MINUS;
import static lu.kbra.pclib.parser.data.TokenTypes.MODULO;
import static lu.kbra.pclib.parser.data.TokenTypes.MODULO_ASSIGN;
import static lu.kbra.pclib.parser.data.TokenTypes.MUL;
import static lu.kbra.pclib.parser.data.TokenTypes.MUL_ASSIGN;
import static lu.kbra.pclib.parser.data.TokenTypes.NOT;
import static lu.kbra.pclib.parser.data.TokenTypes.NOT_EQUALS;
import static lu.kbra.pclib.parser.data.TokenTypes.NUM_LIT;
import static lu.kbra.pclib.parser.data.TokenTypes.OCT_NUM_LIT;
import static lu.kbra.pclib.parser.data.TokenTypes.OR;
import static lu.kbra.pclib.parser.data.TokenTypes.PAREN_CLOSE;
import static lu.kbra.pclib.parser.data.TokenTypes.PAREN_OPEN;
import static lu.kbra.pclib.parser.data.TokenTypes.PLUS;
import static lu.kbra.pclib.parser.data.TokenTypes.PLUS_ASSIGN;
import static lu.kbra.pclib.parser.data.TokenTypes.PLUS_PLUS;
import static lu.kbra.pclib.parser.data.TokenTypes.SEMICOLON;
import static lu.kbra.pclib.parser.data.TokenTypes.SIGNED_BIT_SHIFT_LEFT;
import static lu.kbra.pclib.parser.data.TokenTypes.SIGNED_BIT_SHIFT_RIGHT;
import static lu.kbra.pclib.parser.data.TokenTypes.STRICT_ASSIGN;
import static lu.kbra.pclib.parser.data.TokenTypes.STRING_LIT;
import static lu.kbra.pclib.parser.data.TokenTypes.TRUE;
import static lu.kbra.pclib.parser.data.TokenTypes.UNSIGNED_BIT_SHIFT_LEFT;
import static lu.kbra.pclib.parser.data.TokenTypes.UNSIGNED_BIT_SHIFT_RIGHT;
import static lu.kbra.pclib.parser.data.TokenTypes.XOR;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import lu.kbra.pclib.parser.data.TokenTypes;
import lu.kbra.pclib.parser.exception.TokenizerException;
import lu.kbra.pclib.parser.impl.LexerIterator;
import lu.kbra.pclib.parser.impl.TokenType;
import lu.kbra.pclib.parser.token.CommentToken;
import lu.kbra.pclib.parser.token.IdentifierToken;
import lu.kbra.pclib.parser.token.NumericLiteralToken;
import lu.kbra.pclib.parser.token.StringLiteralToken;
import lu.kbra.pclib.parser.token.Token;

public class Tokenizer {

	private static final int EOF = -1;

	private final Reader reader;
	private final Deque<Integer> lookahead = new ArrayDeque<>();
	private final List<Token> tokens = new ArrayList<>();

	private int line = 0;
	private int column = 0;

	private int currentCharLine = 0;
	private int currentCharColumn = 0;

	private TokenType type = null;
	private String strValue = "";
	private int tokenStartLine = 0;
	private int tokenStartColumn = 0;

	public Tokenizer(final String str) {
		this(new StringReader(str));
	}

	public Tokenizer(final Reader reader) {
		this.reader = reader;
	}

	public void lexe() {
		try {
			while (this.hasNext()) {
				this.currentCharLine = this.line;
				this.currentCharColumn = this.column;

				final char current = this.consume();

				switch (current) {
				case ' ':
				case '\t':
				case '\n':
				case '\r':
					if (IDENT.equals(this.type) || NUM_LIT.equals(this.type) || DEC_NUM_LIT.equals(this.type)) {
						this.flushToken();
					}
					break;

				case '+':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '=') {
						this.consume();
						this.type = PLUS_ASSIGN;
					} else if (this.peek() == '+') {
						this.consume();
						this.type = PLUS_PLUS;
					} else {
						this.type = PLUS;
					}
					this.flushToken();
					break;

				case '-':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '>') {
						this.consume();
						this.type = ARROW;
					} else if (this.peek() == '=') {
						this.consume();
						this.type = MINUS_ASSIGN;
					} else if (this.peek() == '-') {
						this.consume();
						this.type = MINUS_MINUS;
					} else {
						this.type = MINUS;
					}
					this.flushToken();
					break;

				case '*':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '=') {
						this.consume();
						this.type = MUL_ASSIGN;
					} else {
						this.type = MUL;
					}
					this.flushToken();
					break;

				case '/':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '/') {
						this.type = COMMENT;
						this.strValue = "";
						while (this.hasNext() && this.peek() != '\n') {
							this.strValue += this.consume();
						}
						this.flushToken();
					} else if (this.peek() == '*') {
						this.type = COMMENT_BLOCK;
						this.consume();
						while (this.hasNext() && this.peek() != '*' && this.peek(1) != '/') {
							this.strValue += this.consume();
						}
						this.consume();
						this.consume();
						this.flushToken();
					} else if (this.peek() == '=') {
						this.consume();
						this.type = DIV_ASSIGN;
						this.flushToken();
					} else {
						this.type = DIV;
						this.flushToken();
					}
					break;

				case '(':
					this.emitFixed(PAREN_OPEN);
					break;
				case ')':
					this.emitFixed(PAREN_CLOSE);
					break;
				case '[':
					this.emitFixed(BRACKET_OPEN);
					break;
				case ']':
					this.emitFixed(BRACKET_CLOSE);
					break;
				case '{':
					this.emitFixed(CURLY_OPEN);
					break;
				case '}':
					this.emitFixed(CURLY_CLOSE);
					break;

				case '"':
					this.readStringLiteral();
					break;

				case '\'':
					this.readCharLiteral();
					break;

				case '$':
					this.emitFixed(DOLLAR);
					break;
				case ':':
					this.emitFixed(COLON);
					break;
				case ';':
					this.emitFixed(SEMICOLON);
					break;
				case ',':
					this.emitFixed(COMMA);
					break;
				case '.':
					this.emitFixed(DOT);
					break;

				case '|':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '|') {
						this.consume();
						this.type = OR;
					} else if (this.peek() == '=') {
						this.consume();
						this.type = BIT_OR_ASSIGN;
					} else {
						this.type = BIT_OR;
					}
					this.flushToken();
					break;

				case '&':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '&') {
						this.consume();
						this.type = AND;
					} else if (this.peek() == '=') {
						this.consume();
						this.type = BIT_AND_ASSIGN;
					} else {
						this.type = BIT_AND;
					}
					this.flushToken();
					break;

				case '%':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '=') {
						this.consume();
						this.type = MODULO_ASSIGN;
					} else {
						this.type = MODULO;
					}
					this.flushToken();
					break;

				case '#':
					this.emitFixed(HASH);
					break;

				case '!':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '=') {
						this.consume();
						this.type = NOT_EQUALS;
					} else {
						this.type = NOT;
					}
					this.flushToken();
					break;

				case '^':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '^') {
						this.consume();
						this.type = XOR;
					} else if (this.peek() == '=') {
						this.consume();
						this.type = BIT_XOR_ASSIGN;
					} else {
						this.type = BIT_XOR;
					}
					this.flushToken();
					break;

				case '~':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '=') {
						this.consume();
						this.type = BIT_NOT_ASSIGN;
					} else {
						this.type = BIT_NOT;
					}
					this.flushToken();
					break;

				case '=':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '=') {
						this.consume();
						this.type = EQUALS;
					} else {
						this.type = STRICT_ASSIGN;
					}
					this.flushToken();
					break;

				case '<':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '=') {
						this.consume();
						this.type = LESS_EQUALS;
					} else if (this.peek() == '<') {
						this.consume();
						this.type = SIGNED_BIT_SHIFT_LEFT;
						if (this.peek() == '<') {
							this.consume();
							this.type = UNSIGNED_BIT_SHIFT_LEFT;
						}
					} else {
						this.type = LESS;
					}
					this.flushToken();
					break;

				case '>':
					this.beginToken(this.currentCharLine, this.currentCharColumn);
					if (this.peek() == '=') {
						this.consume();
						this.type = GREATER_EQUALS;
					} else if (this.peek() == '>') {
						this.consume();
						this.type = SIGNED_BIT_SHIFT_RIGHT;
						if (this.peek() == '>') {
							this.consume();
							this.type = UNSIGNED_BIT_SHIFT_RIGHT;
						}
					} else {
						this.type = GREATER;
					}
					this.flushToken();
					break;

				case '0':
					if (this.peek() == 'x') {
						this.beginToken(this.currentCharLine, this.currentCharColumn);
						this.type = HEX_NUM_LIT;
						this.strValue = "0";
						this.strValue += this.consume(); // x
						while (Character.isLetterOrDigit(this.peek()) || this.peek() == '_') {
							this.strValue += this.consume();
						}
						this.flushToken();
						break;
					} else if (this.peek() == 'b') {
						this.beginToken(this.currentCharLine, this.currentCharColumn);
						this.type = BIN_NUM_LIT;
						this.strValue = "0";
						this.strValue += this.consume(); // b
						while (this.peek() == '0' || this.peek() == '1' || this.peek() == '_') {
							this.strValue += this.consume();
						}
						this.flushToken();
						break;
					} else if (this.peek() == 'o') {
						this.beginToken(this.currentCharLine, this.currentCharColumn);
						this.type = OCT_NUM_LIT;
						this.strValue = "0";
						this.strValue += this.consume(); // o
						while (Tokenizer.isOctalDigit((char) this.peek()) || this.peek() == '_') {
							this.strValue += this.consume();
						}
						this.flushToken();
						break;
					}

					this.checkOthers(current);
					break;

				default:
					this.checkOthers(current);
					break;
				}
			}

			this.flushToken();
		} catch (final IOException e) {
			throw new TokenizerException("Error while lexing", e);
		}
	}

	protected TokenType getIdentType(final String strValue) {
		if (strValue.equals(FALSE.getStringValue())) {
			return FALSE;
		} else if (strValue.equals(TRUE.getStringValue())) {
			return TRUE;
		}
		return IDENT;
	}

	private void checkOthers(final char current) {
		if (this.type == null && (Character.isLetter(current) || current == '_')) {
			this.beginToken(this.currentCharLine, this.currentCharColumn);
			this.type = IDENT;
			this.strValue = Character.toString(current);

			while (Character.isLetterOrDigit(this.peek()) || this.peek() == '_') {
				this.strValue += this.consume();
			}

			this.type = this.getIdentType(this.strValue);
			this.flushToken();
			return;
		}

		if (this.type == null && Character.isDigit(current)) {
			this.beginToken(this.currentCharLine, this.currentCharColumn);
			this.type = NUM_LIT;
			this.strValue = Character.toString(current);

			while (Character.isLetterOrDigit(this.peek()) || this.peek() == '_' || this.peek() == '.' || this.peek() == 'f') {
				this.strValue += this.consume();
			}

			if (this.strValue.contains(".") || this.strValue.contains("f")) {
				this.type = DEC_NUM_LIT;
			}

			this.flushToken();
			return;
		}

		if (this.type == null) {
			throw new TokenizerException(
					"Unexpected character '" + current + "' at " + this.currentCharLine + ":" + this.currentCharColumn);
		}
	}

	protected Token flushToken() {
		if (this.type == null) {
			return null;
		}

		final Token token = this.createToken(this.type, this.tokenStartLine, this.tokenStartColumn, this.strValue);
		this.tokens.add(token);

		this.type = null;
		this.strValue = "";
		return token;
	}

	protected Token createToken(final TokenType type, final int line, final int column, final String strValue) {
		if (IDENT.equals(type)) {
			return new IdentifierToken(type, line, column, strValue);
		}

		if (NUM_LIT.equals(type) || CHAR_LIT.equals(type) || DEC_NUM_LIT.equals(type) || HEX_NUM_LIT.equals(type)
				|| BIN_NUM_LIT.equals(type) || TRUE.equals(type) || FALSE.equals(type) || OCT_NUM_LIT.equals(type)) {
			return NumericLiteralToken.parseNumeric((TokenTypes) type, line, column, strValue);
		}

		if (STRING_LIT.equals(type)) {
			return new StringLiteralToken(type, line, column, strValue);
		}

		if (COMMENT.equals(type) || COMMENT_BLOCK.equals(type)) {
			return new CommentToken(type, line, column, strValue);
		}

		return new Token(type, line, column);
	}

	private void emitFixed(final TokenType tokenType) {
		this.beginToken(this.currentCharLine, this.currentCharColumn);
		this.type = tokenType;
		this.flushToken();
	}

	private void beginToken(final int startLine, final int startColumn) {
		this.tokenStartLine = startLine;
		this.tokenStartColumn = startColumn;
		this.strValue = "";
	}

	private void readStringLiteral() throws IOException {
		this.beginToken(this.currentCharLine, this.currentCharColumn);
		this.type = STRING_LIT;
		this.strValue = "";

		final int startLine = this.currentCharLine;
		final int startColumn = this.currentCharColumn;

		while (this.hasNext() && this.peek() != '"') {
			if (this.peek() == '\\') {
				this.consume();

				if (!this.hasNext()) {
					throw new TokenizerException("Unterminated string, starting at: " + startLine + ":" + startColumn);
				}

				final char escaped = this.consume();
				switch (escaped) {
				case '0':
					this.strValue += '\0';
					break;
				case 'e':
					this.strValue += 'e';
					break;
				case 'f':
					this.strValue += '\f';
					break;
				case 'v':
					this.strValue += 'v';
					break;
				case 'b':
					this.strValue += '\b';
					break;
				case 't':
					this.strValue += '\t';
					break;
				case 'n':
					this.strValue += '\n';
					break;
				case 'r':
					this.strValue += '\r';
					break;
				case '\\':
					this.strValue += '\\';
					break;
				case '"':
					this.strValue += '"';
					break;
				case '\'':
					this.strValue += '\'';
					break;
				default:
					this.strValue += escaped;
					break;
				}
			} else {
				this.strValue += this.consume();
			}
		}

		if (!this.hasNext()) {
			throw new TokenizerException("Unterminated string, starting at: " + startLine + ":" + startColumn);
		}

		this.consume(); // closing "
		this.flushToken();
	}

	private void readCharLiteral() throws IOException {
		this.beginToken(this.currentCharLine, this.currentCharColumn);
		this.type = CHAR_LIT;
		this.strValue = "";

		final int startLine = this.currentCharLine;
		final int startColumn = this.currentCharColumn;

		if (!this.hasNext()) {
			throw new TokenizerException("Unterminated char literal, starting at: " + startLine + ":" + startColumn);
		}

		if (this.peek() == '\\') {
			this.consume();
			if (!this.hasNext()) {
				throw new TokenizerException("Unterminated char literal, starting at: " + startLine + ":" + startColumn);
			}

			final char escaped = this.consume();
			switch (escaped) {
			case '0':
				this.strValue = "\0";
				break;
			case 'b':
				this.strValue = "\b";
				break;
			case 't':
				this.strValue = "\t";
				break;
			case 'n':
				this.strValue = "\n";
				break;
			case 'r':
				this.strValue = "\r";
				break;
			case 'f':
				this.strValue = "\f";
				break;
			case '\'':
				this.strValue = "'";
				break;
			case '"':
				this.strValue = "\"";
				break;
			case '\\':
				this.strValue = "\\";
				break;
			default:
				this.strValue = Character.toString(escaped);
				break;
			}
		} else {
			this.strValue = Character.toString(this.consume());
		}

		if (!this.hasNext() || this.peek() != '\'') {
			throw new TokenizerException("Unterminated char literal, starting at: " + startLine + ":" + startColumn);
		}

		this.consume(); // closing '
		this.flushToken();
	}

	public boolean hasNext() {
		return this.peek() != Tokenizer.EOF;
	}

	public char consume() {
		try {
			final int value = this.readAhead(0);
			if (value == Tokenizer.EOF) {
				throw new TokenizerException("Unexpected end of input");
			}

			this.lookahead.removeFirst();

			final char c = (char) value;
			if (c == '\n') {
				this.line++;
				this.column = 0;
			} else {
				this.column++;
			}
			return c;
		} catch (final IOException e) {
			throw new TokenizerException("Error while reading input", e);
		}
	}

	public int peek() {
		try {
			return this.readAhead(0);
		} catch (final IOException e) {
			throw new TokenizerException("Error while peeking input", e);
		}
	}

	public int peek(final int offset) {
		try {
			return this.readAhead(offset);
		} catch (final IOException e) {
			throw new TokenizerException("Error while peeking input", e);
		}
	}

	public boolean peek(final String s) {
		for (int i = 0; i < s.length(); i++) {
			if (this.peek(i) != s.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean peek(final char... chars) {
		final int current = this.peek();
		for (final char c : chars) {
			if (current == c) {
				return true;
			}
		}
		return false;
	}

	public boolean peek(final int offset, final char... chars) {
		final int current = this.peek(offset);
		for (final char c : chars) {
			if (current == c) {
				return true;
			}
		}
		return false;
	}

	public boolean peek(final int offset, final String s) {
		for (int i = 0; i < s.length(); i++) {
			if (this.peek(offset + i) != s.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private int readAhead(final int offset) throws IOException {
		while (this.lookahead.size() <= offset) {
			this.lookahead.addLast(this.reader.read());
		}

		int i = 0;
		for (final int value : this.lookahead) {
			if (i == offset) {
				return value;
			}
			i++;
		}
		return Tokenizer.EOF;
	}

	public List<Token> getTokens() {
		return this.tokens;
	}

	public static boolean isOctalDigit(final char digit) {
		if (Character.isDigit(digit)) {
			final int numericValue = Character.getNumericValue(digit);
			return numericValue >= 0 && numericValue <= 7;
		}
		return false;
	}

	public LexerIterator iterator() {
		return new LexerIterator() {

			int pos = 0;

			@Override
			public TokenType peek(final int i) {
				return this.pos + i < Tokenizer.this.tokens.size() ? Tokenizer.this.tokens.get(this.pos + i).getType() : null;
			}

			@Override
			public TokenType peek() {
				return this.peek(0);
			}

			@Override
			public boolean peek(final TokenType type) {
				final TokenType current = this.peek();
				return current != null && current.matches(type);
			}

			@Override
			public boolean peek(final int i, final TokenType type) {
				final TokenType current = this.peek(i);
				return current != null && current.matches(type);
			}

			@Override
			public boolean hasNext() {
				return this.pos < Tokenizer.this.tokens.size();
			}

			@Override
			public Token consume(final TokenType type) {
				if (this.peek(type)) {
					return this.consume();
				}
				throw new TokenizerException("Expected: " + type + " but got: " + this.peek());
			}

			@Override
			public Token consume() {
				return Tokenizer.this.tokens.get(this.pos++);
			}

			@Override
			public boolean peek(final TokenType... types) {
				return Arrays.stream(types).anyMatch(this::peek);
			}

			@Override
			public boolean peek(final int i, final TokenType... types) {
				return Arrays.stream(types).anyMatch(t -> this.peek(i, t));
			}

			@Override
			public Token consume(final TokenType... types) {
				if (this.peek(types)) {
					return this.consume();
				}
				throw new TokenizerException("Expected: " + Arrays.toString(types) + " but got: " + this.peek());
			}
		};
	}

}
