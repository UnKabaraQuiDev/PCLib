package lu.kbra.pclib.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

@Configuration
public class DBConfiguration {

//	@Bean
//	public DataBaseConnector connector() {
//		return new MySQLDataBaseConnector("user", "pass", "localhost", MySQLDataBaseConnector.DEFAULT_PORT);
//	}

	@Bean
	public DataBase dataBase(final DataBaseEntryUtils entryUtils) {
		return new DataBase(
				new MySQLDataBaseConnector("user", "pass", "localhost", MySQLDataBaseConnector.DEFAULT_PORT),
				"pclib-db-spring", entryUtils);
	}

	@Bean
	public DataBase dataBase2(final DataBaseEntryUtils entryUtils) {
		return new DataBase(
				new MySQLDataBaseConnector("user", "pass", "localhost", MySQLDataBaseConnector.DEFAULT_PORT),
				"pclib-db-spring-2", entryUtils);
	}

}
