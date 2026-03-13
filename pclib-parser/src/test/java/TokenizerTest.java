
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.parser.Tokenizer;
import lu.kbra.pclib.parser.data.TokenTypes;
import lu.kbra.pclib.parser.token.CommentToken;
import lu.kbra.pclib.parser.token.IdentifierToken;
import lu.kbra.pclib.parser.token.NumericLiteralToken;
import lu.kbra.pclib.parser.token.StringLiteralToken;
import lu.kbra.pclib.parser.token.Token;

public class TokenizerTest {

	@Test
	void shouldTokenizeDefaultTokenizer() {
		final Tokenizer tokenizer = new Tokenizer("true false abc 123 45.6 0xFF 0b1010 0o17 \"hello\" 'x' //comment\n"
				+ "( ) [ ] { } , . : ; $ + - * / % ++ -- += -= *= /= %= == != < <= > >=\n"
				+ "&& || ! ^^ ^ &= |= ^= ~= = << >> <<< >>> #");

		tokenizer.lexe();

		final List<Token> tokens = tokenizer.getTokens();

		tokens.forEach(System.out::println);

		assertEquals(55, tokens.size());

		assertEquals(TokenTypes.TRUE, tokens.get(0).getType());
		assertInstanceOf(NumericLiteralToken.class, tokens.get(0));

		assertEquals(TokenTypes.FALSE, tokens.get(1).getType());
		assertInstanceOf(NumericLiteralToken.class, tokens.get(1));

		assertEquals(TokenTypes.IDENT, tokens.get(2).getType());
		assertInstanceOf(IdentifierToken.class, tokens.get(2));

		assertEquals(TokenTypes.NUM_LIT, tokens.get(3).getType());
		assertInstanceOf(NumericLiteralToken.class, tokens.get(3));

		assertEquals(TokenTypes.DEC_NUM_LIT, tokens.get(4).getType());
		assertInstanceOf(NumericLiteralToken.class, tokens.get(4));

		assertEquals(TokenTypes.HEX_NUM_LIT, tokens.get(5).getType());
		assertInstanceOf(NumericLiteralToken.class, tokens.get(5));

		assertEquals(TokenTypes.BIN_NUM_LIT, tokens.get(6).getType());
		assertInstanceOf(NumericLiteralToken.class, tokens.get(6));

		assertEquals(TokenTypes.OCT_NUM_LIT, tokens.get(7).getType());
		assertInstanceOf(NumericLiteralToken.class, tokens.get(7));

		assertEquals(TokenTypes.STRING_LIT, tokens.get(8).getType());
		assertInstanceOf(StringLiteralToken.class, tokens.get(8));

		assertEquals(TokenTypes.CHAR_LIT, tokens.get(9).getType());
		assertInstanceOf(NumericLiteralToken.class, tokens.get(9));

		assertEquals(TokenTypes.COMMENT, tokens.get(10).getType());
		assertInstanceOf(CommentToken.class, tokens.get(10));

		assertEquals(TokenTypes.PAREN_OPEN, tokens.get(11).getType());
		assertEquals(TokenTypes.PAREN_CLOSE, tokens.get(12).getType());
		assertEquals(TokenTypes.BRACKET_OPEN, tokens.get(13).getType());
		assertEquals(TokenTypes.BRACKET_CLOSE, tokens.get(14).getType());
		assertEquals(TokenTypes.CURLY_OPEN, tokens.get(15).getType());
		assertEquals(TokenTypes.CURLY_CLOSE, tokens.get(16).getType());
		assertEquals(TokenTypes.COMMA, tokens.get(17).getType());
		assertEquals(TokenTypes.DOT, tokens.get(18).getType());
		assertEquals(TokenTypes.COLON, tokens.get(19).getType());
		assertEquals(TokenTypes.SEMICOLON, tokens.get(20).getType());
		assertEquals(TokenTypes.DOLLAR, tokens.get(21).getType());
		assertEquals(TokenTypes.PLUS, tokens.get(22).getType());
		assertEquals(TokenTypes.MINUS, tokens.get(23).getType());
		assertEquals(TokenTypes.MUL, tokens.get(24).getType());
		assertEquals(TokenTypes.DIV, tokens.get(25).getType());
		assertEquals(TokenTypes.MODULO, tokens.get(26).getType());
		assertEquals(TokenTypes.PLUS_PLUS, tokens.get(27).getType());
		assertEquals(TokenTypes.MINUS_MINUS, tokens.get(28).getType());
		assertEquals(TokenTypes.PLUS_ASSIGN, tokens.get(29).getType());
		assertEquals(TokenTypes.MINUS_ASSIGN, tokens.get(30).getType());
		assertEquals(TokenTypes.MUL_ASSIGN, tokens.get(31).getType());
		assertEquals(TokenTypes.DIV_ASSIGN, tokens.get(32).getType());
		assertEquals(TokenTypes.MODULO_ASSIGN, tokens.get(33).getType());
		assertEquals(TokenTypes.EQUALS, tokens.get(34).getType());
		assertEquals(TokenTypes.NOT_EQUALS, tokens.get(35).getType());
		assertEquals(TokenTypes.LESS, tokens.get(36).getType());
		assertEquals(TokenTypes.LESS_EQUALS, tokens.get(37).getType());
		assertEquals(TokenTypes.GREATER, tokens.get(38).getType());
		assertEquals(TokenTypes.GREATER_EQUALS, tokens.get(39).getType());
		assertEquals(TokenTypes.AND, tokens.get(40).getType());
		assertEquals(TokenTypes.OR, tokens.get(41).getType());
		assertEquals(TokenTypes.NOT, tokens.get(42).getType());
		assertEquals(TokenTypes.XOR, tokens.get(43).getType());
		assertEquals(TokenTypes.BIT_XOR, tokens.get(44).getType());
		assertEquals(TokenTypes.BIT_AND_ASSIGN, tokens.get(45).getType());
		assertEquals(TokenTypes.BIT_OR_ASSIGN, tokens.get(46).getType());
		assertEquals(TokenTypes.BIT_XOR_ASSIGN, tokens.get(47).getType());
		assertEquals(TokenTypes.BIT_NOT_ASSIGN, tokens.get(48).getType());
		assertEquals(TokenTypes.STRICT_ASSIGN, tokens.get(49).getType());
		assertEquals(TokenTypes.SIGNED_BIT_SHIFT_LEFT, tokens.get(50).getType());
		assertEquals(TokenTypes.SIGNED_BIT_SHIFT_RIGHT, tokens.get(51).getType());
		assertEquals(TokenTypes.UNSIGNED_BIT_SHIFT_LEFT, tokens.get(52).getType());
		assertEquals(TokenTypes.UNSIGNED_BIT_SHIFT_RIGHT, tokens.get(53).getType());
		assertEquals(TokenTypes.HASH, tokens.get(54).getType());
	}

	@Test
	void shouldTokenizeBlockCommentWithDefaultTokenizer() {
		final Tokenizer tokenizer = new Tokenizer("abc /* block comment */ def");

		tokenizer.lexe();

		final List<Token> tokens = tokenizer.getTokens();

		tokens.forEach(System.out::println);

		assertEquals(3, tokens.size());
		assertEquals(TokenTypes.IDENT, tokens.get(0).getType());
		assertEquals(TokenTypes.COMMENT_BLOCK, tokens.get(1).getType());
		assertEquals(TokenTypes.IDENT, tokens.get(2).getType());

		assertInstanceOf(IdentifierToken.class, tokens.get(0));
		assertInstanceOf(CommentToken.class, tokens.get(1));
		assertInstanceOf(IdentifierToken.class, tokens.get(2));
	}

}