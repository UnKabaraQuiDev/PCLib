
import java.util.List;

import org.junit.jupiter.api.Assertions;
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
				+ "( ) [ ] { } , . : ; $ + - * / % ++ -- += -= *= /= %= == != < <= > >=\n" + "&& || ! ^^ ^ &= |= ^= ~= = << >> <<< >>> #");

		tokenizer.lexe();

		final List<Token> tokens = tokenizer.getTokens();

		tokens.forEach(System.out::println);

		Assertions.assertEquals(55, tokens.size());

		Assertions.assertEquals(TokenTypes.TRUE, tokens.get(0).getType());
		Assertions.assertInstanceOf(NumericLiteralToken.class, tokens.get(0));

		Assertions.assertEquals(TokenTypes.FALSE, tokens.get(1).getType());
		Assertions.assertInstanceOf(NumericLiteralToken.class, tokens.get(1));

		Assertions.assertEquals(TokenTypes.IDENT, tokens.get(2).getType());
		Assertions.assertInstanceOf(IdentifierToken.class, tokens.get(2));

		Assertions.assertEquals(TokenTypes.NUM_LIT, tokens.get(3).getType());
		Assertions.assertInstanceOf(NumericLiteralToken.class, tokens.get(3));

		Assertions.assertEquals(TokenTypes.DEC_NUM_LIT, tokens.get(4).getType());
		Assertions.assertInstanceOf(NumericLiteralToken.class, tokens.get(4));

		Assertions.assertEquals(TokenTypes.HEX_NUM_LIT, tokens.get(5).getType());
		Assertions.assertInstanceOf(NumericLiteralToken.class, tokens.get(5));

		Assertions.assertEquals(TokenTypes.BIN_NUM_LIT, tokens.get(6).getType());
		Assertions.assertInstanceOf(NumericLiteralToken.class, tokens.get(6));

		Assertions.assertEquals(TokenTypes.OCT_NUM_LIT, tokens.get(7).getType());
		Assertions.assertInstanceOf(NumericLiteralToken.class, tokens.get(7));

		Assertions.assertEquals(TokenTypes.STRING_LIT, tokens.get(8).getType());
		Assertions.assertInstanceOf(StringLiteralToken.class, tokens.get(8));

		Assertions.assertEquals(TokenTypes.CHAR_LIT, tokens.get(9).getType());
		Assertions.assertInstanceOf(NumericLiteralToken.class, tokens.get(9));

		Assertions.assertEquals(TokenTypes.COMMENT, tokens.get(10).getType());
		Assertions.assertInstanceOf(CommentToken.class, tokens.get(10));

		Assertions.assertEquals(TokenTypes.PAREN_OPEN, tokens.get(11).getType());
		Assertions.assertEquals(TokenTypes.PAREN_CLOSE, tokens.get(12).getType());
		Assertions.assertEquals(TokenTypes.BRACKET_OPEN, tokens.get(13).getType());
		Assertions.assertEquals(TokenTypes.BRACKET_CLOSE, tokens.get(14).getType());
		Assertions.assertEquals(TokenTypes.CURLY_OPEN, tokens.get(15).getType());
		Assertions.assertEquals(TokenTypes.CURLY_CLOSE, tokens.get(16).getType());
		Assertions.assertEquals(TokenTypes.COMMA, tokens.get(17).getType());
		Assertions.assertEquals(TokenTypes.DOT, tokens.get(18).getType());
		Assertions.assertEquals(TokenTypes.COLON, tokens.get(19).getType());
		Assertions.assertEquals(TokenTypes.SEMICOLON, tokens.get(20).getType());
		Assertions.assertEquals(TokenTypes.DOLLAR, tokens.get(21).getType());
		Assertions.assertEquals(TokenTypes.PLUS, tokens.get(22).getType());
		Assertions.assertEquals(TokenTypes.MINUS, tokens.get(23).getType());
		Assertions.assertEquals(TokenTypes.MUL, tokens.get(24).getType());
		Assertions.assertEquals(TokenTypes.DIV, tokens.get(25).getType());
		Assertions.assertEquals(TokenTypes.MODULO, tokens.get(26).getType());
		Assertions.assertEquals(TokenTypes.PLUS_PLUS, tokens.get(27).getType());
		Assertions.assertEquals(TokenTypes.MINUS_MINUS, tokens.get(28).getType());
		Assertions.assertEquals(TokenTypes.PLUS_ASSIGN, tokens.get(29).getType());
		Assertions.assertEquals(TokenTypes.MINUS_ASSIGN, tokens.get(30).getType());
		Assertions.assertEquals(TokenTypes.MUL_ASSIGN, tokens.get(31).getType());
		Assertions.assertEquals(TokenTypes.DIV_ASSIGN, tokens.get(32).getType());
		Assertions.assertEquals(TokenTypes.MODULO_ASSIGN, tokens.get(33).getType());
		Assertions.assertEquals(TokenTypes.EQUALS, tokens.get(34).getType());
		Assertions.assertEquals(TokenTypes.NOT_EQUALS, tokens.get(35).getType());
		Assertions.assertEquals(TokenTypes.LESS, tokens.get(36).getType());
		Assertions.assertEquals(TokenTypes.LESS_EQUALS, tokens.get(37).getType());
		Assertions.assertEquals(TokenTypes.GREATER, tokens.get(38).getType());
		Assertions.assertEquals(TokenTypes.GREATER_EQUALS, tokens.get(39).getType());
		Assertions.assertEquals(TokenTypes.AND, tokens.get(40).getType());
		Assertions.assertEquals(TokenTypes.OR, tokens.get(41).getType());
		Assertions.assertEquals(TokenTypes.NOT, tokens.get(42).getType());
		Assertions.assertEquals(TokenTypes.XOR, tokens.get(43).getType());
		Assertions.assertEquals(TokenTypes.BIT_XOR, tokens.get(44).getType());
		Assertions.assertEquals(TokenTypes.BIT_AND_ASSIGN, tokens.get(45).getType());
		Assertions.assertEquals(TokenTypes.BIT_OR_ASSIGN, tokens.get(46).getType());
		Assertions.assertEquals(TokenTypes.BIT_XOR_ASSIGN, tokens.get(47).getType());
		Assertions.assertEquals(TokenTypes.BIT_NOT_ASSIGN, tokens.get(48).getType());
		Assertions.assertEquals(TokenTypes.STRICT_ASSIGN, tokens.get(49).getType());
		Assertions.assertEquals(TokenTypes.SIGNED_BIT_SHIFT_LEFT, tokens.get(50).getType());
		Assertions.assertEquals(TokenTypes.SIGNED_BIT_SHIFT_RIGHT, tokens.get(51).getType());
		Assertions.assertEquals(TokenTypes.UNSIGNED_BIT_SHIFT_LEFT, tokens.get(52).getType());
		Assertions.assertEquals(TokenTypes.UNSIGNED_BIT_SHIFT_RIGHT, tokens.get(53).getType());
		Assertions.assertEquals(TokenTypes.HASH, tokens.get(54).getType());
	}

	@Test
	void shouldTokenizeBlockCommentWithDefaultTokenizer() {
		final Tokenizer tokenizer = new Tokenizer("abc /* block comment */ def");

		tokenizer.lexe();

		final List<Token> tokens = tokenizer.getTokens();

		tokens.forEach(System.out::println);

		Assertions.assertEquals(3, tokens.size());
		Assertions.assertEquals(TokenTypes.IDENT, tokens.get(0).getType());
		Assertions.assertEquals(TokenTypes.COMMENT_BLOCK, tokens.get(1).getType());
		Assertions.assertEquals(TokenTypes.IDENT, tokens.get(2).getType());

		Assertions.assertInstanceOf(IdentifierToken.class, tokens.get(0));
		Assertions.assertInstanceOf(CommentToken.class, tokens.get(1));
		Assertions.assertInstanceOf(IdentifierToken.class, tokens.get(2));
	}

}
