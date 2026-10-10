package lu.kbra.pclib.db.migration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.pclib.db.hook.VersionRule;
import lu.kbra.pclib.db.rule.TraceRule;
import lu.kbra.pclib.db.utils.DatabaseQueryableHookTemplate;

import shared.CaptureRule;

@Configuration
public class DBConfiguration {

	@Bean
	DatabaseQueryableHookTemplate template() {
		return new DatabaseQueryableHookTemplate().add(TraceRule.class).add("captureRule").addBefore(TraceRule.class, new VersionRule());
	}

	@Bean
	TraceRule rule() {
		return new TraceRule();
	}

	@Bean
	CaptureRule captureRule() {
		return new CaptureRule();
	}

}
