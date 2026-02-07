package lu.kbra.pclib.db.config;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.view.AbstractDBView;

@Configuration
public class DataBaseInitializerConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = "lu.pclib.db", name = "auto-create", havingValue = "true", matchIfMissing = true)
	public DataBaseInitializer dataBaseInitializer(ObjectProvider<List<DataBase>> databases,
			ObjectProvider<List<AbstractDBTable<?>>> tables, ObjectProvider<List<AbstractDBView<?>>> views) {
		return new DataBaseInitializer(databases.getIfAvailable(Collections::emptyList),
				tables.getIfAvailable(Collections::emptyList), views.getIfAvailable(Collections::emptyList));
	}

}
