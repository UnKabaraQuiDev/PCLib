package lu.kbra.pclib.db.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import lu.kbra.pclib.db.DeferredSQLQueryableRegistrar;
import lu.kbra.pclib.db.SpringDataBaseEntryUtils;

@AutoConfiguration
public class PCLibDBAutoConfiguration {

	// forces the initialization, used by some ColumnTypes
	@Autowired
	private ObjectMapperHolder objectMapperHolder;
	// forces the initialization, used by some ColumnTypes
	@Autowired
	private ConversionServiceHolder conversionServiceHolder;

	@Bean
	@Primary
	@ConditionalOnMissingBean
	public SpringDataBaseEntryUtils defaultSpringDataBaseEntryUtils() {
		return new SpringDataBaseEntryUtils();
	}

	@Bean
	@Primary
	@ConditionalOnMissingBean
	public DeferredSQLQueryableRegistrar defaultDeferredSQLQueryableRegistrar(
			final ApplicationContext applicationContext, final SpringDataBaseEntryUtils dataBaseEntryUtils) {
		return new DeferredSQLQueryableRegistrar(applicationContext, dataBaseEntryUtils);
	}

}
