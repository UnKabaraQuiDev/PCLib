package lu.kbra.pclib.parser;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.parser.data.TokenTypes;
import lu.kbra.pclib.parser.impl.TokenType;
import lu.kbra.pclib.parser.token.CommentToken;
import lu.kbra.pclib.parser.token.IdentifierToken;
import lu.kbra.pclib.parser.token.NumericLiteralToken;
import lu.kbra.pclib.parser.token.StringLiteralToken;
import lu.kbra.pclib.parser.token.Token;

public class ModuleTokenizer extends Tokenizer {

	public static class TokenRecord {
		public final TokenType type;
		public final int line;
		public final int column;
		public final String strValue;

		public TokenRecord(TokenType type, int line, int column, String strValue) {
			this.type = type;
			this.line = line;
			this.column = column;
			this.strValue = strValue;
		}

		@Override
		public String toString() {
			return "TokenRecord@" + System.identityHashCode(this) + " [type=" + type + ", line=" + line + ", column="
					+ column + ", strValue=" + strValue + "]";
		}

	}

	private final Map<String, TokenType> identifierTypes = new HashMap<>();
	private final Map<TokenType, Function<TokenRecord, Token>> tokenFactories = new HashMap<>();

	public ModuleTokenizer(final String str) {
		super(str);
		this.registerDefaultTokenFactories();
	}

	public ModuleTokenizer(final Reader reader) {
		super(reader);
		this.registerDefaultTokenFactories();
	}

	protected void registerDefaultTokenFactories() {
		this.tokenFactories.put(TokenTypes.IDENT,
				record -> new IdentifierToken(record.type, record.line, record.column, record.strValue));

		this.tokenFactories.put(TokenTypes.STRING_LIT,
				record -> new StringLiteralToken(record.type, record.line, record.column, record.strValue));

		this.tokenFactories.put(TokenTypes.COMMENT,
				record -> new CommentToken(record.type, record.line, record.column, record.strValue));
		this.tokenFactories.put(TokenTypes.COMMENT_BLOCK,
				record -> new CommentToken(record.type, record.line, record.column, record.strValue));

		final Function<TokenRecord, Token> numericFactory = record -> NumericLiteralToken
				.parseNumeric((TokenTypes) record.type, record.line, record.column, record.strValue);

		this.tokenFactories.put(TokenTypes.NUM_LIT, numericFactory);
		this.tokenFactories.put(TokenTypes.DEC_NUM_LIT, numericFactory);
		this.tokenFactories.put(TokenTypes.HEX_NUM_LIT, numericFactory);
		this.tokenFactories.put(TokenTypes.BIN_NUM_LIT, numericFactory);
		this.tokenFactories.put(TokenTypes.OCT_NUM_LIT, numericFactory);
		this.tokenFactories.put(TokenTypes.CHAR_LIT, numericFactory);
		this.tokenFactories.put(TokenTypes.TRUE, numericFactory);
		this.tokenFactories.put(TokenTypes.FALSE, numericFactory);
	}

	public ModuleTokenizer registerIdentifierType(final String identifier, final TokenType tokenType) {
		Objects.requireNonNull(identifier, "identifier");
		Objects.requireNonNull(tokenType, "tokenType");
		this.identifierTypes.put(identifier, tokenType);
		return this;
	}

	public ModuleTokenizer registerIdentifierType(final TokenType tokenType) {
		Objects.requireNonNull(tokenType.getValueAsString(), "identifier");
		Objects.requireNonNull(tokenType, "tokenType");
		this.identifierTypes.put(tokenType.getValueAsString(), tokenType);
		return this;
	}

	public ModuleTokenizer registerIdentifierTypes(final Map<String, TokenType> identifierTypes) {
		Objects.requireNonNull(identifierTypes, "identifierTypes");
		this.identifierTypes.putAll(identifierTypes);
		return this;
	}

	public ModuleTokenizer registerTokenFactory(final TokenType tokenType,
			final Function<TokenRecord, Token> tokenFactory) {
		Objects.requireNonNull(tokenType, "tokenType");
		Objects.requireNonNull(tokenFactory, "tokenFactory");
		this.tokenFactories.put(tokenType, tokenFactory);
		return this;
	}

	public ModuleTokenizer registerTokenFactories(final Map<TokenType, Function<TokenRecord, Token>> tokenFactories) {
		Objects.requireNonNull(tokenFactories, "tokenFactories");
		this.tokenFactories.putAll(tokenFactories);
		return this;
	}

	public Map<String, TokenType> getIdentifierTypes() {
		return this.identifierTypes;
	}

	public Map<TokenType, Function<TokenRecord, Token>> getTokenFactories() {
		return this.tokenFactories;
	}

	@Override
	protected TokenType getIdentType(final String strValue) {
		return this.identifierTypes.getOrDefault(strValue, TokenTypes.IDENT);
	}

	@Override
	protected Token createToken(final TokenType type, final int line, final int column, final String strValue) {
		final TokenRecord record = new TokenRecord(type, line, column, strValue);

		Function<TokenRecord, Token> factory = this.tokenFactories.get(type);

		if (factory == null) {
			for (final Map.Entry<TokenType, Function<TokenRecord, Token>> entry : this.tokenFactories.entrySet()) {
				if (type.matches(entry.getKey())) {
					factory = entry.getValue();
					break;
				}
			}
		}

		return factory != null ? factory.apply(record) : new Token(type, line, column);
	}

}
