import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.util.List;

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
		assertTrue(iterator.hasNext());
		assertTrue(iterator.peek(TokenTypes.IDENT));
		assertTrue(iterator.peek(1, TokenTypes.NUM_LIT));
		assertFalse(iterator.peek(TokenTypes.STRING_LIT));

		final Token identifier = iterator.consume(TokenTypes.IDENT);
		assertEquals("1:1", identifier.getPosition());
		assertEquals("name", ((IdentifierToken) identifier).getValue());

		assertThrows(TokenizerException.class, () -> iterator.consume(TokenTypes.STRING_LIT));
		assertTrue(iterator.hasNext());

		final Token number = iterator.consume(TokenTypes.NUM_LIT);
		assertEquals("2:3", number.getPosition());
		assertEquals(42, ((NumericLiteralToken) number).intValue());
		assertFalse(iterator.hasNext());
		assertEquals(null, iterator.peek());
	}

	@Test
	void stringAndCharacterEscapesAreDecoded() {
		final Tokenizer tokenizer = new Tokenizer("\"hello\\nworld\" '\\t'");
		tokenizer.lexe();

		final List<Token> tokens = tokenizer.getTokens();
		assertEquals(2, tokens.size());
		assertEquals("hello\nworld", ((StringLiteralToken) tokens.get(0)).getValue());
		assertEquals('\t', ((NumericLiteralToken) tokens.get(1)).getValue());
	}

	@Test
	void numericTokensKeepTheirRadixAndValue() {
		final Tokenizer tokenizer = new Tokenizer("0x10 0b101 0o17 3.5f true false");
		tokenizer.lexe();

		final List<Token> tokens = tokenizer.getTokens();
		assertEquals(6, tokens.size());
		assertEquals((byte) 16, ((NumericLiteralToken) tokens.get(0)).byteValue());
		assertEquals((byte) 5, ((NumericLiteralToken) tokens.get(1)).byteValue());
		assertEquals((byte) 15, ((NumericLiteralToken) tokens.get(2)).byteValue());
		assertEquals(3.5f, ((NumericLiteralToken) tokens.get(3)).floatValue(), 0.0001f);
		assertTrue(((NumericLiteralToken) tokens.get(4)).booleanValue());
		assertFalse(((NumericLiteralToken) tokens.get(5)).booleanValue());
	}

	@Test
	void invalidInputProducesTokenizerExceptions() {
		assertThrows(TokenizerException.class, () -> new Tokenizer("\"unfinished").lexe());
		assertThrows(TokenizerException.class, () -> new Tokenizer("'ab'").lexe());
		assertThrows(TokenizerException.class, () -> new Tokenizer("@").lexe());
	}

	@Test
	void octalDigitsExcludeEightAndNine() {
		assertTrue(Tokenizer.isOctalDigit('0'));
		assertTrue(Tokenizer.isOctalDigit('7'));
		assertFalse(Tokenizer.isOctalDigit('8'));
		assertFalse(Tokenizer.isOctalDigit('x'));
	}

}
