package lu.kbra.pclib.db.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import lu.kbra.pclib.db.DeferredQLQueryableRegistrar;
import lu.kbra.pclib.db.SpringDataBaseEntryUtils;

@AutoConfiguration
@ComponentScan(basePackageClasses = DeferredQLQueryableRegistrar.class)
public class PCLibDBConfiguration {

	// forces the initialization, used by some ColumnTypes
	@Autowired
	private ObjectMapperHolder objectMapperHolder;
	// forces the initialization, used by some ColumnTypes
	@Autowired
	private ConversionServiceHolder conversionServiceHolder;

	@Bean
	@ConditionalOnMissingBean
	public SpringDataBaseEntryUtils defaultSpringDataBaseEntryUtils() {
		return new SpringDataBaseEntryUtils();
	}

}
