package lu.kbra.pclib.db.base;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.pclib.db.hook.VersionRule;
import lu.kbra.pclib.db.rule.TraceRule;
import lu.kbra.pclib.db.utils.DatabaseQueryableHookTemplate;
import lu.kbra.pclib.db.utils.QueryableTemplate;

@Configuration
public class DBConfiguration {

	@Bean
	QueryableTemplate template2() {
		return new QueryableTemplate(TemplateTable.class).setName("templateSecond");
	}

	@Bean
	DatabaseQueryableHookTemplate template() {
		return new DatabaseQueryableHookTemplate().add(TraceRule.class).addBefore(TraceRule.class, new VersionRule()).matchDbms("mysql");
	}

	@Bean
	TraceRule rule() {
		return new TraceRule();
	}

}
