package lu.kbra.pclib.parser.impl;

import lu.kbra.pclib.parser.token.Token;

public interface LexerIterator {

	Token consume();

	Token consume(TokenType type);

	Token consume(TokenType... type);

	boolean hasNext();

	TokenType peek();

	TokenType peek(int i);

	boolean peek(int i, TokenType type);

	boolean peek(int i, TokenType... types);

	boolean peek(TokenType type);

	boolean peek(TokenType... types);

}
