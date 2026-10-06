package utils;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.MySQLDatabaseConnector;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.SQLQueryableHookManager;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import lombok.Getter;

@Getter
public class DummyQueryable<T extends DatabaseEntry> implements SQLQueryable<T> {

	private final DatabaseEntryUtils databaseEntryUtils;
	private final DummyStructure structure;
	private final Database database;

	public DummyQueryable(final DatabaseEntryUtils utils, Class<T> entryClass) {
		this.databaseEntryUtils = utils;
		this.structure = new DummyStructure(utils, (Class<? extends SQLQueryable<?>>) (Class) DummyQueryable.class, entryClass);
		this.database = new Database(new MySQLDatabaseConnector("username", "password", "host", 1234), "dummy_database", utils);
		this.database.setDatabaseStructure(new DatabaseStructure("dummy_database",
				utils.getStructureVisitor().qualifiedName("dummy_database"),
				this.database.getCustomHints(),
				null));
	}

	public DummyQueryable(final DatabaseEntryUtils utils, Class<T> entryClass, Database database) {
		this.databaseEntryUtils = utils;
		this.structure = new DummyStructure(utils, (Class<? extends SQLQueryable<?>>) (Class) DummyQueryable.class, entryClass);
		this.database = database;
	}

	public SQLQueryableStructure scan() {
		databaseEntryUtils.getDatabaseScanner().register(this).doScan();
		return this.structure;
	}

	@Override
	public int count() {
		return 0;
	}

	@Override
	public <B> B query(final SQLQuery<T, B> query) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean create() throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SQLQueryableHookManager getQueryableHookManager() {
		return this.databaseEntryUtils.getQueryableHookManager();
	}

	@Override
	public boolean exists() throws DBException {
		return false;
	}

}
