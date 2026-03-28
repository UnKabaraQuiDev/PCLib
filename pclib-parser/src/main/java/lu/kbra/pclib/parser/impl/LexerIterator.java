package lu.kbra.pclib.parser.impl;

import lu.kbra.pclib.parser.token.Token;

public interface LexerIterator {

	boolean hasNext();

	boolean peek(TokenType type);

	boolean peek(TokenType... types);

	boolean peek(int i, TokenType type);

	boolean peek(int i, TokenType... types);

	TokenType peek();

	TokenType peek(int i);

	Token consume();

	Token consume(TokenType type);

	Token consume(TokenType... type);

}
