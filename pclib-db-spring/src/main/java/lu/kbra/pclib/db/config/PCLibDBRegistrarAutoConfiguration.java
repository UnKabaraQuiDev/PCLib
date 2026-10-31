package lu.kbra.pclib.db.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;

import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;
import lu.kbra.pclib.db.intercept.SharedQueryMethodInterceptor;
import lu.kbra.pclib.db.registrar.QueryableTemplateRegistrar;
import lu.kbra.pclib.db.registrar.ReflectionRegistrar;
import lu.kbra.pclib.db.registrar.SQLQueryableRegistrar;

@AutoConfiguration
@ConditionalOnProperty(prefix = "pclib.db", name = "enabled", havingValue = "true", matchIfMissing = true)
//@Import(SQLQueryableRegistrar.class)
public class PCLibDBRegistrarAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ImportRuntimeHints(ReflectionRegistrar.class)
	static SQLQueryableRegistrar defaultSQLQueryableRegistrar() {
		return new SQLQueryableRegistrar();
	}

	@Bean
	@ConditionalOnMissingBean
	static QueryableTemplateRegistrar queryableTemplateRegistrar() {
		return new QueryableTemplateRegistrar();
	}

	@Bean
	@ConditionalOnMissingBean
	static QueryMethodInterceptor queryMethodInterceptor() {
		return new SharedQueryMethodInterceptor();
	}

}
