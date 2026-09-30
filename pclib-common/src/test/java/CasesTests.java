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

	@Test
	public void constantToLowerCamelCase() {
		Assertions.assertEquals("apiAccessLog", PCUtils.constantToLowerCamelCase("API_ACCESS_LOG"));
		Assertions.assertEquals("auditLog", PCUtils.constantToLowerCamelCase("AUDIT_LOG"));
		Assertions.assertEquals("person", PCUtils.constantToLowerCamelCase("PERSON"));
		Assertions.assertEquals("urlValue", PCUtils.constantToLowerCamelCase("URL_VALUE"));
		Assertions.assertEquals("myUrlValue", PCUtils.constantToLowerCamelCase("MY_URL_VALUE"));
		Assertions.assertEquals("xmlParser", PCUtils.constantToLowerCamelCase("XML_PARSER"));
		Assertions.assertEquals("simpleTest", PCUtils.constantToLowerCamelCase("SIMPLE_TEST"));
		Assertions.assertEquals("", PCUtils.constantToLowerCamelCase(""));
		Assertions.assertEquals(null, PCUtils.constantToLowerCamelCase(null));
	}

	@Test
	public void constantToUpperCamelCase() {
		Assertions.assertEquals("ApiAccessLog", PCUtils.constantToUpperCamelCase("API_ACCESS_LOG"));
		Assertions.assertEquals("AuditLog", PCUtils.constantToUpperCamelCase("AUDIT_LOG"));
		Assertions.assertEquals("Person", PCUtils.constantToUpperCamelCase("PERSON"));
		Assertions.assertEquals("UrlValue", PCUtils.constantToUpperCamelCase("URL_VALUE"));
		Assertions.assertEquals("MyUrlValue", PCUtils.constantToUpperCamelCase("MY_URL_VALUE"));
		Assertions.assertEquals("XmlParser", PCUtils.constantToUpperCamelCase("XML_PARSER"));
		Assertions.assertEquals("SimpleTest", PCUtils.constantToUpperCamelCase("SIMPLE_TEST"));
		Assertions.assertEquals("", PCUtils.constantToUpperCamelCase(""));
		Assertions.assertEquals(null, PCUtils.constantToUpperCamelCase(null));
	}

	@Test
	public void snakeCaseToLowerCamelCase() {
		Assertions.assertEquals("apiAccessLog", PCUtils.snakeCaseToLowerCamelCase("api_access_log"));
		Assertions.assertEquals("auditLog", PCUtils.snakeCaseToLowerCamelCase("audit_log"));
		Assertions.assertEquals("person", PCUtils.snakeCaseToLowerCamelCase("person"));
		Assertions.assertEquals("urlValue", PCUtils.snakeCaseToLowerCamelCase("url_value"));
		Assertions.assertEquals("myUrlValue", PCUtils.snakeCaseToLowerCamelCase("my_url_value"));
		Assertions.assertEquals("xmlParser", PCUtils.snakeCaseToLowerCamelCase("xml_parser"));
		Assertions.assertEquals("simpleTest", PCUtils.snakeCaseToLowerCamelCase("simple_test"));
		Assertions.assertEquals("", PCUtils.snakeCaseToLowerCamelCase(""));
		Assertions.assertEquals(null, PCUtils.snakeCaseToLowerCamelCase(null));
	}

	@Test
	public void snakeCaseToUpperCamelCase() {
		Assertions.assertEquals("ApiAccessLog", PCUtils.snakeCaseToUpperCamelCase("api_access_log"));
		Assertions.assertEquals("AuditLog", PCUtils.snakeCaseToUpperCamelCase("audit_log"));
		Assertions.assertEquals("Person", PCUtils.snakeCaseToUpperCamelCase("person"));
		Assertions.assertEquals("UrlValue", PCUtils.snakeCaseToUpperCamelCase("url_value"));
		Assertions.assertEquals("MyUrlValue", PCUtils.snakeCaseToUpperCamelCase("my_url_value"));
		Assertions.assertEquals("XmlParser", PCUtils.snakeCaseToUpperCamelCase("xml_parser"));
		Assertions.assertEquals("SimpleTest", PCUtils.snakeCaseToUpperCamelCase("simple_test"));
		Assertions.assertEquals("", PCUtils.snakeCaseToUpperCamelCase(""));
		Assertions.assertEquals(null, PCUtils.snakeCaseToUpperCamelCase(null));
	}

}
