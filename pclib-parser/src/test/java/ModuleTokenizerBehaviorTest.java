import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.parser.ModuleTokenizer;
import lu.kbra.pclib.parser.data.TokenTypes;
import lu.kbra.pclib.parser.token.IdentifierToken;
import lu.kbra.pclib.parser.token.StringLiteralToken;

class ModuleTokenizerBehaviorTest {

	@Test
	void registeredKeywordMapsReplaceTheDefaultIdentifierType() {
		final ModuleTokenizer tokenizer = new ModuleTokenizer("move other");
		tokenizer.registerIdentifierTypes(Collections.singletonMap("move", CustomTokenTypes.MOVE));
		tokenizer.lexe();

		assertEquals(CustomTokenTypes.MOVE, tokenizer.getTokens().get(0).getType());
		assertEquals(TokenTypes.IDENT, tokenizer.getTokens().get(1).getType());
		assertEquals("other", ((IdentifierToken) tokenizer.getTokens().get(1)).getValue());
	}

	@Test
	void tokenFactoryMapCanOverrideDefaultTokenCreation() {
		final ModuleTokenizer tokenizer = new ModuleTokenizer("custom");
		tokenizer.registerTokenFactories(Collections.singletonMap(TokenTypes.IDENT,
				record -> new StringLiteralToken(record.type, record.line, record.column, record.strValue.toUpperCase())));
		tokenizer.lexe();

		assertEquals("CUSTOM", ((StringLiteralToken) tokenizer.getTokens().get(0)).getValue());
	}

	@Test
	void invalidRegistrationArgumentsAreRejected() {
		final ModuleTokenizer tokenizer = new ModuleTokenizer("");
		assertThrows(NullPointerException.class, () -> tokenizer.registerIdentifierType(null, CustomTokenTypes.MOVE));
		assertThrows(NullPointerException.class, () -> tokenizer.registerIdentifierType("move", null));
		assertThrows(NullPointerException.class, () -> tokenizer.registerTokenFactory(TokenTypes.IDENT, null));
	}

}
