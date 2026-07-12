package lu.kbra.pclib.db.base;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.pclib.db.utils.QueryableTemplate;

@Configuration
public class DBConfiguration {

	@Bean
	QueryableTemplate template2() {
		return new QueryableTemplate(TemplateTable.class).setName("templateSecond");
	}

}
