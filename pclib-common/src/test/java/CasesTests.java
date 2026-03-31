import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.PCUtils;

public class CasesTests {

	@Test
	public void camelCase() {
		Assertions.assertEquals("api_access_log", PCUtils.camelCaseToSnakeCase("APIAccessLog"));
		Assertions.assertEquals("audit_log", PCUtils.camelCaseToSnakeCase("AuditLog"));
		Assertions.assertEquals("person", PCUtils.camelCaseToSnakeCase("Person"));
		Assertions.assertEquals("url_value", PCUtils.camelCaseToSnakeCase("URLValue"));
		Assertions.assertEquals("my_url_value", PCUtils.camelCaseToSnakeCase("MyURLValue"));
		Assertions.assertEquals("xml_parser", PCUtils.camelCaseToSnakeCase("XMLParser"));
		Assertions.assertEquals("simple_test", PCUtils.camelCaseToSnakeCase("SimpleTest"));
		Assertions.assertEquals("already_snake_case", PCUtils.camelCaseToSnakeCase("already_snake_case"));
		Assertions.assertEquals("", PCUtils.camelCaseToSnakeCase(""));
		Assertions.assertEquals(null, PCUtils.camelCaseToSnakeCase(null));
	}

	@Test
	public void constantToCamelCase() {
		Assertions.assertEquals("apiAccessLog", PCUtils.constantToCamelCase("API_ACCESS_LOG"));
		Assertions.assertEquals("auditLog", PCUtils.constantToCamelCase("AUDIT_LOG"));
		Assertions.assertEquals("person", PCUtils.constantToCamelCase("PERSON"));
		Assertions.assertEquals("urlValue", PCUtils.constantToCamelCase("URL_VALUE"));
		Assertions.assertEquals("myUrlValue", PCUtils.constantToCamelCase("MY_URL_VALUE"));
		Assertions.assertEquals("xmlParser", PCUtils.constantToCamelCase("XML_PARSER"));
		Assertions.assertEquals("simpleTest", PCUtils.constantToCamelCase("SIMPLE_TEST"));
		Assertions.assertEquals("", PCUtils.constantToCamelCase(""));
		Assertions.assertEquals(null, PCUtils.constantToCamelCase(null));
	}

	@Test
	public void camelCaseToConstant() {
		Assertions.assertEquals("API_ACCESS_LOG", PCUtils.camelCaseToConstant("apiAccessLog"));
		Assertions.assertEquals("AUDIT_LOG", PCUtils.camelCaseToConstant("auditLog"));
		Assertions.assertEquals("PERSON", PCUtils.camelCaseToConstant("person"));
		Assertions.assertEquals("URL_VALUE", PCUtils.camelCaseToConstant("urlValue"));
		Assertions.assertEquals("MY_URL_VALUE", PCUtils.camelCaseToConstant("myUrlValue"));
		Assertions.assertEquals("XML_PARSER", PCUtils.camelCaseToConstant("xmlParser"));
		Assertions.assertEquals("SIMPLE_TEST", PCUtils.camelCaseToConstant("simpleTest"));
		Assertions.assertEquals("API_ACCESS_LOG", PCUtils.camelCaseToConstant("APIAccessLog"));
		Assertions.assertEquals("", PCUtils.camelCaseToConstant(""));
		Assertions.assertEquals(null, PCUtils.camelCaseToConstant(null));
	}

}
