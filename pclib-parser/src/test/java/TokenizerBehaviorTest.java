import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.parser.Tokenizer;
import lu.kbra.pclib.parser.data.TokenTypes;
import lu.kbra.pclib.parser.exception.TokenizerException;
import lu.kbra.pclib.parser.impl.LexerIterator;
import lu.kbra.pclib.parser.token.IdentifierToken;
import lu.kbra.pclib.parser.token.NumericLiteralToken;
import lu.kbra.pclib.parser.token.StringLiteralToken;
import lu.kbra.pclib.parser.token.Token;

class TokenizerBehaviorTest {

	@Test
	void lexerIteratorPeeksConsumesAndReportsPositions() {
		final Tokenizer tokenizer = new Tokenizer(new StringReader("name\n  42"));
		tokenizer.lexe();

		final LexerIterator iterator = tokenizer.iterator();
		Assertions.assertTrue(iterator.hasNext());
		Assertions.assertTrue(iterator.peek(TokenTypes.IDENT));
		Assertions.assertTrue(iterator.peek(1, TokenTypes.NUM_LIT));
		Assertions.assertFalse(iterator.peek(TokenTypes.STRING_LIT));

		final Token identifier = iterator.consume(TokenTypes.IDENT);
		Assertions.assertEquals("1:1", identifier.getPosition());
		Assertions.assertEquals("name", ((IdentifierToken) identifier).getValue());

		Assertions.assertThrows(TokenizerException.class, () -> iterator.consume(TokenTypes.STRING_LIT));
		Assertions.assertTrue(iterator.hasNext());

		final Token number = iterator.consume(TokenTypes.NUM_LIT);
		Assertions.assertEquals("2:3", number.getPosition());
		Assertions.assertEquals(42, ((NumericLiteralToken) number).intValue());
		Assertions.assertFalse(iterator.hasNext());
		Assertions.assertEquals(null, iterator.peek());
	}

	@Test
	void stringAndCharacterEscapesAreDecoded() {
		final Tokenizer tokenizer = new Tokenizer("\"hello\\nworld\" '\\t'");
		tokenizer.lexe();

		final List<Token> tokens = tokenizer.getTokens();
		Assertions.assertEquals(2, tokens.size());
		Assertions.assertEquals("hello\nworld", ((StringLiteralToken) tokens.get(0)).getValue());
		Assertions.assertEquals('\t', ((NumericLiteralToken) tokens.get(1)).getValue());
	}

	@Test
	void numericTokensKeepTheirRadixAndValue() {
		final Tokenizer tokenizer = new Tokenizer("0x10 0b101 0o17 3.5f true false");
		tokenizer.lexe();

		final List<Token> tokens = tokenizer.getTokens();
		Assertions.assertEquals(6, tokens.size());
		Assertions.assertEquals((byte) 16, ((NumericLiteralToken) tokens.get(0)).byteValue());
		Assertions.assertEquals((byte) 5, ((NumericLiteralToken) tokens.get(1)).byteValue());
		Assertions.assertEquals((byte) 15, ((NumericLiteralToken) tokens.get(2)).byteValue());
		Assertions.assertEquals(3.5f, ((NumericLiteralToken) tokens.get(3)).floatValue(), 0.0001f);
		Assertions.assertTrue(((NumericLiteralToken) tokens.get(4)).booleanValue());
		Assertions.assertFalse(((NumericLiteralToken) tokens.get(5)).booleanValue());
	}

	@Test
	void invalidInputProducesTokenizerExceptions() {
		Assertions.assertThrows(TokenizerException.class, () -> new Tokenizer("\"unfinished").lexe());
		Assertions.assertThrows(TokenizerException.class, () -> new Tokenizer("'ab'").lexe());
		Assertions.assertThrows(TokenizerException.class, () -> new Tokenizer("@").lexe());
	}

	@Test
	void octalDigitsExcludeEightAndNine() {
		Assertions.assertTrue(Tokenizer.isOctalDigit('0'));
		Assertions.assertTrue(Tokenizer.isOctalDigit('7'));
		Assertions.assertFalse(Tokenizer.isOctalDigit('8'));
		Assertions.assertFalse(Tokenizer.isOctalDigit('x'));
	}

}
