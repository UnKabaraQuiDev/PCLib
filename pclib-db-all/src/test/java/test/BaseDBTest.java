package test;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;

import lombok.Getter;
import shared.PersonTable;
import shared.PrintDbRule;

@Getter
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseDBTest implements DBTest, DBTransactionTest, DBViewTest {

	protected DatabaseConnector connector;
	protected Database database;

	@BeforeAll
	public void createDb() throws Exception {
		this.connector = this.createConnector();
		this.database = new Database(this.connector, "dbNameYepYap");
		this.database.getDatabaseEntryUtils().getQueryableHookManager().add(new PrintDbRule());
		this.database.clearBeans().scanFromBeans();

		assert !this.database.exists() : "Db shouldn't exist.";
		assert this.database.create().created() : "Couldn't create database.";
	}

	@AfterAll
	public void deleteDb() throws IOException, SQLException {
		try {
			final PersonTable people = new PersonTable(this.database);
			database.clearBeans().register(people).scanFromBeans();
			this.connector.reset();
			this.database.create();
			assert this.database.exists();
			people.drop();
			assert !people.exists();
		} catch (Exception e) {
			throw e;
		} finally {
			this.database.drop();
			this.connector.reset();
		}
	}

}
