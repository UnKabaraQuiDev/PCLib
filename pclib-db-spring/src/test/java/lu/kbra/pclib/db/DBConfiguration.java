package lu.kbra.pclib.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import mysql.MySQL;

@Configuration
public class DBConfiguration {

	static {
		MySQL.start();
	}

	@Bean
	public DataBase dataBase(final DataBaseEntryUtils entryUtils) {
		return new DataBase(() -> new MySQLDataBaseConnector(MySQL.USER, MySQL.PASS, "localhost", MySQL.getPort()),
				"pclib-db-spring", entryUtils);
	}

	@Bean
	public DataBase dataBase2(final DataBaseEntryUtils entryUtils) {
		return new DataBase(() -> new MySQLDataBaseConnector(MySQL.USER, MySQL.PASS, "localhost", MySQL.getPort()),
				"pclib-db-spring-2", entryUtils);
	}

}
