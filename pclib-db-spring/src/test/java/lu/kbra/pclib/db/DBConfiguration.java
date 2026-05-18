package lu.kbra.pclib.db;

import org.springframework.context.annotation.Configuration;

import mysql.MySQL;

@Configuration
public class DBConfiguration {

	static {
		MySQL.start();
	}

}
