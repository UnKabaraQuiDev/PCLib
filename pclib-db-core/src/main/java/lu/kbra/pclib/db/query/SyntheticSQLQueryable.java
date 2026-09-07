package lu.kbra.pclib.db.query;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry.ReadOnlyDatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.SQLQueryableHookManager;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import lombok.Data;

@Data
class SyntheticSQLQueryable<T extends ReadOnlyDatabaseEntry> implements SQLQueryable<T> {

	private final Database database;
	private final DatabaseEntryUtils databaseEntryUtils;
	private final SQLQueryableStructure structure;
	private final SQLQueryableHookManager queryableHookManager;

	@Override
	public int count() throws DBException {
		return 0;
	}

	@Override
	public <B> B query(SQLQuery<T, B> query) throws DBException {
		throw new UnsupportedOperationException();
	}

}
