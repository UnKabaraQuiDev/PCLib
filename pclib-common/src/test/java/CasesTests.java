import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.PCUtils;

public class CasesTests {

	@Test
	public void camelCase() {
		assertEquals("api_access_log", PCUtils.camelCaseToSnakeCase("APIAccessLog"));
		assertEquals("audit_log", PCUtils.camelCaseToSnakeCase("AuditLog"));
		assertEquals("person", PCUtils.camelCaseToSnakeCase("Person"));
		assertEquals("url_value", PCUtils.camelCaseToSnakeCase("URLValue"));
		assertEquals("my_url_value", PCUtils.camelCaseToSnakeCase("MyURLValue"));
		assertEquals("xml_parser", PCUtils.camelCaseToSnakeCase("XMLParser"));
		assertEquals("simple_test", PCUtils.camelCaseToSnakeCase("SimpleTest"));
		assertEquals("already_snake_case", PCUtils.camelCaseToSnakeCase("already_snake_case"));
		assertEquals("", PCUtils.camelCaseToSnakeCase(""));
		assertEquals(null, PCUtils.camelCaseToSnakeCase(null));
	}

	@Test
	public void constantToCamelCase() {
		assertEquals("apiAccessLog", PCUtils.constantToCamelCase("API_ACCESS_LOG"));
		assertEquals("auditLog", PCUtils.constantToCamelCase("AUDIT_LOG"));
		assertEquals("person", PCUtils.constantToCamelCase("PERSON"));
		assertEquals("urlValue", PCUtils.constantToCamelCase("URL_VALUE"));
		assertEquals("myUrlValue", PCUtils.constantToCamelCase("MY_URL_VALUE"));
		assertEquals("xmlParser", PCUtils.constantToCamelCase("XML_PARSER"));
		assertEquals("simpleTest", PCUtils.constantToCamelCase("SIMPLE_TEST"));
		assertEquals("", PCUtils.constantToCamelCase(""));
		assertEquals(null, PCUtils.constantToCamelCase(null));
	}

	@Test
	public void camelCaseToConstant() {
		assertEquals("API_ACCESS_LOG", PCUtils.camelCaseToConstant("apiAccessLog"));
		assertEquals("AUDIT_LOG", PCUtils.camelCaseToConstant("auditLog"));
		assertEquals("PERSON", PCUtils.camelCaseToConstant("person"));
		assertEquals("URL_VALUE", PCUtils.camelCaseToConstant("urlValue"));
		assertEquals("MY_URL_VALUE", PCUtils.camelCaseToConstant("myUrlValue"));
		assertEquals("XML_PARSER", PCUtils.camelCaseToConstant("xmlParser"));
		assertEquals("SIMPLE_TEST", PCUtils.camelCaseToConstant("simpleTest"));
		assertEquals("API_ACCESS_LOG", PCUtils.camelCaseToConstant("APIAccessLog"));
		assertEquals("", PCUtils.camelCaseToConstant(""));
		assertEquals(null, PCUtils.camelCaseToConstant(null));
	}

}
