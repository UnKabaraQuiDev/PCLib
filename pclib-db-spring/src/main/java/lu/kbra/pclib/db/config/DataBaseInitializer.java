package lu.kbra.pclib.db.config;

import java.sql.SQLException;
import java.util.List;

import jakarta.annotation.PostConstruct;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.view.AbstractDBView;

public class DataBaseInitializer {

	private final List<DataBase> databases;
	private final List<AbstractDBTable<?>> tables;
	private final List<AbstractDBView<?>> views;

	public DataBaseInitializer(List<DataBase> databases, List<AbstractDBTable<?>> tables, List<AbstractDBView<?>> views) {
		this.databases = databases;
		this.tables = tables;
		this.views = views;
	}

	@PostConstruct
	public void init() throws SQLException {
		for (DataBase db : databases) {
			db.create();
		}

		for (AbstractDBTable<?> table : tables) {
			table.create();
		}

		for (AbstractDBView<?> view : views) {
			view.create();
		}
	}

	@Override
	public String toString() {
		return "DataBaseInitializer@" + System.identityHashCode(this) + " [databases=" + databases + ", tables="
				+ tables + ", views=" + views + "]";
	}

}
