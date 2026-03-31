
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.parser.ModuleTokenizer;
import lu.kbra.pclib.parser.data.TokenTypes;
import lu.kbra.pclib.parser.token.IdentifierToken;
import lu.kbra.pclib.parser.token.NumericLiteralToken;
import lu.kbra.pclib.parser.token.Token;

public class ModuleTokenizerTest {

	@Test
	void shouldTokenizeCustomKeywordTypesWithModuleTokenizer() {
		final ModuleTokenizer tokenizer = new ModuleTokenizer("move up down rotate loop color thickness test 12");

		tokenizer.registerIdentifierType(CustomTokenTypes.MOVE);
		tokenizer.registerIdentifierType(CustomTokenTypes.UP);
		tokenizer.registerIdentifierType(CustomTokenTypes.DOWN);
		tokenizer.registerIdentifierType(CustomTokenTypes.ROTATE);
		tokenizer.registerIdentifierType(CustomTokenTypes.LOOP);
		tokenizer.registerIdentifierType(CustomTokenTypes.COLOR);
		tokenizer.registerIdentifierType(CustomTokenTypes.THICKNESS);

		tokenizer.lexe();

		final List<Token> tokens = tokenizer.getTokens();

		Assertions.assertEquals(9, tokens.size());

		Assertions.assertEquals(CustomTokenTypes.MOVE, tokens.get(0).getType());
		Assertions.assertEquals(CustomTokenTypes.UP, tokens.get(1).getType());
		Assertions.assertEquals(CustomTokenTypes.DOWN, tokens.get(2).getType());
		Assertions.assertEquals(CustomTokenTypes.ROTATE, tokens.get(3).getType());
		Assertions.assertEquals(CustomTokenTypes.LOOP, tokens.get(4).getType());
		Assertions.assertEquals(CustomTokenTypes.COLOR, tokens.get(5).getType());
		Assertions.assertEquals(CustomTokenTypes.THICKNESS, tokens.get(6).getType());
		Assertions.assertEquals(TokenTypes.IDENT, tokens.get(7).getType());
		Assertions.assertEquals(TokenTypes.NUM_LIT, tokens.get(8).getType());

		Assertions.assertInstanceOf(Token.class, tokens.get(0));
		Assertions.assertInstanceOf(Token.class, tokens.get(1));
		Assertions.assertInstanceOf(Token.class, tokens.get(2));
		Assertions.assertInstanceOf(Token.class, tokens.get(3));
		Assertions.assertInstanceOf(Token.class, tokens.get(4));
		Assertions.assertInstanceOf(Token.class, tokens.get(5));
		Assertions.assertInstanceOf(Token.class, tokens.get(6));
		Assertions.assertInstanceOf(IdentifierToken.class, tokens.get(7));
		Assertions.assertInstanceOf(NumericLiteralToken.class, tokens.get(8));
	}

	@Test
	void shouldUseCustomTokenFactoryWithModuleTokenizer() {
		final ModuleTokenizer tokenizer = new ModuleTokenizer("move test");

		tokenizer.registerIdentifierType("move", CustomTokenTypes.MOVE);

		tokenizer.registerTokenFactory(CustomTokenTypes.MOVE,
				record -> new IdentifierToken(record.type, record.line, record.column, record.strValue));

		tokenizer.lexe();

		final List<Token> tokens = tokenizer.getTokens();

		Assertions.assertEquals(2, tokens.size());

		Assertions.assertEquals(CustomTokenTypes.MOVE, tokens.get(0).getType());
		Assertions.assertEquals(TokenTypes.IDENT, tokens.get(1).getType());

		Assertions.assertInstanceOf(IdentifierToken.class, tokens.get(0));
		Assertions.assertInstanceOf(IdentifierToken.class, tokens.get(1));
	}

}
