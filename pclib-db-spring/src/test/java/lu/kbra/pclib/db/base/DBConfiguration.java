package lu.kbra.pclib.db.base;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.pclib.db.hook.VersionDbRule;
import lu.kbra.pclib.db.utils.DatabaseRuleChainTemplate;
import lu.kbra.pclib.db.utils.QueryableTemplate;

@Configuration
public class DBConfiguration {

	@Bean
	QueryableTemplate template2() {
		return new QueryableTemplate(TemplateTable.class).setName("templateSecond");
	}

	@Bean
	DatabaseRuleChainTemplate template() {
		return new DatabaseRuleChainTemplate().add(PrintDbRule.class).addBefore(PrintDbRule.class, new VersionDbRule()).matchDbms("mysql");
	}

	@Bean
	PrintDbRule rule() {
		return new PrintDbRule();
	}

}
