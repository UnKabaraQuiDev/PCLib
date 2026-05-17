package lu.kbra.pclib.db.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.db.base.DeferredDataBase;
import lu.kbra.pclib.db.connector.DataBaseConnectorFactory;
import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;
import lu.kbra.pclib.db.connector.SQLiteDataBaseConnector;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.SpringDataBaseEntryUtils;

@AutoConfiguration(after = PCLibDBRegistrarAutoConfiguration.class)
@EnableConfigurationProperties(PCLibDBProperties.class)
@ConditionalOnProperty(prefix = "pclib.db", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PCLibDBAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	SpringDataBaseEntryUtils springDataBaseEntryUtils(
			final ObjectMapper objectMapper,
			final ApplicationConversionService conversionService,
			final PCLibDBProperties properties) {

		if (properties.getProtocol() != null) {
			return new SpringDataBaseEntryUtils(objectMapper, conversionService, properties.getProtocol());
		}

		return new SpringDataBaseEntryUtils(objectMapper, conversionService);
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnProperty(prefix = "pclib.db", name = "protocol", havingValue = "mysql")
	static class MySqlConfiguration {

		@Bean
		@ConditionalOnProperty(prefix = "pclib.db", name = "expose-connector", havingValue = "true", matchIfMissing = true)
		DataBaseConnectorFactory mysqlConnector(final PCLibDBProperties properties) {
			final PCLibDBProperties.Mysql mysql = properties.getMysql();

			final MySQLDataBaseConnector connector = new MySQLDataBaseConnector();
			connector.host = mysql.getHost();
			connector.port = mysql.getPort();
			connector.username = mysql.getUsername();
			connector.password = mysql.getPassword();
			connector.characterSet = mysql.getCharacterSet();
			connector.collation = mysql.getCollation();
			connector.engine = mysql.getEngine();

			return connector::clone;
		}

		@Bean
		@ConditionalOnProperty(prefix = "pclib.db", name = "expose-database", havingValue = "true", matchIfMissing = true)
		DeferredDataBase mysqlDataBase(@Qualifier("mysqlConnector")
		final DataBaseConnectorFactory connector, final PCLibDBProperties properties, final DataBaseEntryUtils dataBaseEntryUtils) {
			return new DeferredDataBase(connector, properties.getMysql().getName(), dataBaseEntryUtils);
		}
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnProperty(prefix = "pclib.db", name = "protocol", havingValue = "sqlite")
	static class SqliteConfiguration {

		@Bean
		@ConditionalOnProperty(prefix = "pclib.db", name = "expose-connector", havingValue = "true", matchIfMissing = true)
		DataBaseConnectorFactory sqliteConnector(final PCLibDBProperties properties) {
			final PCLibDBProperties.Sqlite sqlite = properties.getSqlite();

			final SQLiteDataBaseConnector connector = new SQLiteDataBaseConnector();
			connector.dirPath = sqlite.getDirPath();

			return connector::clone;
		}

		@Bean
		@ConditionalOnProperty(prefix = "pclib.db", name = "expose-database", havingValue = "true", matchIfMissing = true)
		DeferredDataBase sqliteDataBase(@Qualifier("sqliteConnector")
		final DataBaseConnectorFactory connector, final PCLibDBProperties properties, final DataBaseEntryUtils dataBaseEntryUtils) {
			return new DeferredDataBase(connector, properties.getSqlite().getName(), dataBaseEntryUtils);
		}
	}
}
