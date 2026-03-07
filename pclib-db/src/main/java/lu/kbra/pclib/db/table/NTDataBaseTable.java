package lu.kbra.pclib.db.table;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.NTSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.table.transaction.NTDBTableTransaction;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class NTDataBaseTable<T extends DataBaseEntry> extends DataBaseTable<T> implements NTAbstractDBTable<T> {

	public NTDataBaseTable(final DataBase dataBase) {
		super(dataBase);
	}

	public NTDataBaseTable(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	public NTDataBaseTable(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils,
			final Class<? extends AbstractDBTable<T>> tableClass) {
		super(dataBase, dbEntryUtils, tableClass);
	}

	@Override
	public NTDBTableTransaction<T> createTransaction() throws DBException {
		return new NTTableTransaction();
	}

	@Override
	public NextTask<Void, ?, NTDBTableTransaction<T>> ntCreateTransaction() {
		return NextTask.create(this::createTransaction);
	}

	@Override
	public <B> NextTask<Void, ?, B> ntQuery(final SQLQuery<T, B> query) {
		return NextTask.create(() -> this.query(query));
	}

	public <B> NextTask<Void, ?, B> ntQuery(final Connection c, final SQLQuery<T, B> query) {
		return NextTask.create(() -> this.query(c, query));
	}

	@Override
	public NextTask<Void, ?, Boolean> ntExists() {
		return NextTask.create(() -> this.exists());
	}

	public NextTask<Void, ?, Boolean> ntExists(final Connection c) {
		return NextTask.create(() -> this.exists(c));
	}

	@Override
	public NextTask<Void, ?, DataBaseTableStatus<T, ? extends NTAbstractDBTable<T>>> ntCreate() {
		return NextTask.create(() -> (DataBaseTableStatus<T, ? extends NTDataBaseTable<T>>) this.create());
	}

	public NextTask<Void, ?, DataBaseTableStatus<T, ? extends NTAbstractDBTable<T>>> ntCreate(final Connection c) {
		return NextTask.create(() -> (DataBaseTableStatus<T, ? extends NTDataBaseTable<T>>) this.create(c));
	}

	@Override
	public NextTask<Void, ?, NTAbstractDBTable<T>> ntDrop() {
		return NextTask.create(() -> {
			this.drop();
			return this;
		});
	}

	public NextTask<Void, ?, NTAbstractDBTable<T>> ntDrop(final Connection c) {
		return NextTask.create(() -> {
			this.drop(c);
			return this;
		});
	}

	@Override
	public NextTask<Void, ?, T> ntLoad(final T data) {
		return NextTask.create(() -> this.load(data));
	}

	public NextTask<Void, ?, T> ntLoad(final Connection c, final T data) {
		return NextTask.create(() -> this.load(c, data));
	}

	@Override
	public NextTask<Void, ?, Integer> ntCount() {
		return NextTask.create(() -> this.count());
	}

	public NextTask<Void, ?, Integer> ntCount(final Connection c) {
		return NextTask.create(() -> this.count(c));
	}

	@Override
	public NextTask<Void, ?, Integer> ntTruncate() {
		return NextTask.create(() -> this.truncate());
	}

	public NextTask<Void, ?, Integer> ntTruncate(final Connection c) {
		return NextTask.create(() -> this.truncate(c));
	}

	@Override
	public NextTask<Void, ?, Integer> ntClear() {
		return NextTask.create(() -> this.clear());
	}

	public NextTask<Void, ?, Integer> ntClear(final Connection c) {
		return NextTask.create(() -> this.clear(c));
	}

	@Override
	public NextTask<Void, ?, T> ntUpdateAndReload(final T data) {
		return NextTask.create(() -> this.updateAndReload(data));
	}

	public NextTask<Void, ?, T> ntUpdateAndReload(final Connection c, final T data) {
		return NextTask.create(() -> this.updateAndReload(c, data));
	}

	@Override
	public NextTask<Void, ?, T> ntUpdate(final T data) {
		return NextTask.create(() -> this.update(data));
	}

	public NextTask<Void, ?, T> ntUpdate(final Connection c, final T data) {
		return NextTask.create(() -> this.update(c, data));
	}

	@Override
	public NextTask<Void, Void, Optional<T>> ntDeleteIfExists(final T data) {
		return NextTask.create(() -> this.deleteIfExists(data));
	}

	public NextTask<Void, Void, Optional<T>> ntDeleteIfExists(final Connection c, final T data) {
		return NextTask.create(() -> this.deleteIfExists(c, data));
	}

	@Override
	public NextTask<Void, ?, Optional<T>> ntDeleteUnique(final T data) {
		return NextTask.create(() -> this.deleteUnique(data));
	}

	public NextTask<Void, ?, Optional<T>> ntDeleteUnique(final Connection c, final T data) {
		return NextTask.create(() -> this.deleteUnique(c, data));
	}

	@Override
	public NextTask<Void, ?, List<T>> ntDeleteUniques(final T data) {
		return NextTask.create(() -> this.deleteUniques(data));
	}

	public NextTask<Void, ?, List<T>> ntDeleteUniques(final Connection c, final T data) {
		return NextTask.create(() -> this.deleteUniques(c, data));
	}

	@Override
	public NextTask<Void, ?, T> ntDelete(final T data) {
		return NextTask.create(() -> this.delete(data));
	}

	public NextTask<Void, ?, T> ntDelete(final Connection c, final T data) {
		return NextTask.create(() -> this.delete(c, data));
	}

	@Override
	public NextTask<Void, ?, T> ntInsertAndReload(final T data) {
		return NextTask.create(() -> this.insertAndReload(data));
	}

	public NextTask<Void, ?, T> ntInsertAndReload(final Connection c, final T data) {
		return NextTask.create(() -> this.insertAndReload(c, data));
	}

	@Override
	public NextTask<Void, ?, T> ntInsert(final T data) {
		return NextTask.create(() -> this.insert(data));
	}

	public NextTask<Void, ?, T> ntInsert(final Connection c, final T data) {
		return NextTask.create(() -> this.insert(c, data));
	}

	@Override
	public NextTask<Void, ?, List<T>> ntLoadByUnique(final T data) {
		return NextTask.create(() -> this.loadByUnique(data));
	}

	public NextTask<Void, ?, List<T>> ntLoadByUnique(final Connection c, final T data) {
		return NextTask.create(() -> this.loadByUnique(c, data));
	}

	@Override
	public NextTask<Void, ?, T> ntLoadUnique(final T data) {
		return NextTask.create(() -> this.loadUnique(data));
	}

	public NextTask<Void, ?, T> ntLoadUnique(final Connection c, final T data) {
		return NextTask.create(() -> this.loadUnique(c, data));
	}

	@Override
	public NextTask<Void, ?, T> ntLoadIfExistsElseInsert(final T data) {
		return NextTask.create(() -> this.loadUniqueIfExistsElseInsert(data));
	}

	public NextTask<Void, ?, T> ntLoadIfExistsElseInsert(final Connection c, final T data) {
		return NextTask.create(() -> this.loadUniqueIfExistsElseInsert(c, data));
	}

	@Override
	public NextTask<Void, ?, Optional<T>> ntLoadIfExists(final T data) {
		return NextTask.create(() -> this.loadUniqueIfExists(data));
	}

	public NextTask<Void, ?, Optional<T>> ntLoadIfExists(final Connection c, final T data) {
		return NextTask.create(() -> this.loadUniqueIfExists(c, data));
	}

	@Override
	public NextTask<Void, ?, Boolean> ntExists(final T data) {
		return NextTask.create(() -> this.exists(data));
	}

	public NextTask<Void, ?, Boolean> ntExists(final Connection c, final T data) {
		return NextTask.create(() -> this.exists(c, data));
	}

	@Override
	public NextTask<Void, ?, Boolean> ntExistsUniques(final T data) {
		return NextTask.create(() -> this.existsUniques(data));
	}

	public NextTask<Void, ?, Boolean> ntExistsUniques(final Connection c, final T data) {
		return NextTask.create(() -> this.existsUniques(c, data));
	}

	@Override
	public NextTask<Void, ?, Boolean> ntExistsUnique(final T data) {
		return NextTask.create(() -> this.existsUnique(data));
	}

	public NextTask<Void, ?, Boolean> ntExistsUnique(final Connection c, final T data) {
		return NextTask.create(() -> this.existsUnique(c, data));
	}

	@Override
	public NextTask<Void, ?, Integer> ntCountUniques(final T data) {
		return NextTask.create(() -> this.countUniques(data));
	}

	public NextTask<Void, ?, Integer> ntCountUniques(final Connection c, final T data) {
		return NextTask.create(() -> this.countUniques(c, data));
	}

	@Override
	public NextTask<Void, ?, Integer> ntCountNotNull(final T data) {
		return NextTask.create(() -> this.countNotNull(data));
	}

	public NextTask<Void, ?, Integer> ntCountNotNull(final Connection c, final T data) {
		return NextTask.create(() -> this.countNotNull(c, data));
	}

	@Override
	public Class<? extends NTSQLQueryable<T>> getTargetClass() {
		return (Class<? extends NTSQLQueryable<T>>) super.getTargetClass();
	}

	public class NTTableTransaction extends AbstractTableTransaction implements NTDBTableTransaction<T>, NTAbstractDBTable<T> {

		public NTTableTransaction(final Connection connection) {
			super(connection);
		}

		public NTTableTransaction() {
		}

		@Override
		public Connection getConnection() {
			return this.connection;
		}

		@Override
		public void requestHook(final SQLRequestType type, final Object query) {
			NTDataBaseTable.this.requestHook(this, type, query);
		}

		@Override
		public NextTask<Void, ?, NTDBTableTransaction<T>> ntCreateTransaction() throws DBException {
			return NTDataBaseTable.this.ntCreateTransaction();
		}

		@Override
		public <B> NextTask<Void, ?, B> ntQuery(final SQLQuery<T, B> query) {
			return NTDataBaseTable.this.ntQuery(this.connection, query);
		}

		@Override
		public NextTask<Void, ?, Boolean> ntExists() {
			return NTDataBaseTable.this.ntExists(this.connection);
		}

		@Override
		public NextTask<Void, ?, DataBaseTableStatus<T, ? extends NTAbstractDBTable<T>>> ntCreate() {
			return NTDataBaseTable.this.ntCreate(this.connection);
		}

		@Override
		public NextTask<Void, ?, NTAbstractDBTable<T>> ntDrop() {
			return NTDataBaseTable.this.ntDrop(this.connection);
		}

		@Override
		public NextTask<Void, ?, T> ntLoad(final T data) {
			return NTDataBaseTable.this.ntLoad(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, Integer> ntCount() {
			return NTDataBaseTable.this.ntCount(this.connection);
		}

		@Override
		public NextTask<Void, ?, Integer> ntTruncate() {
			return NTDataBaseTable.this.ntTruncate(this.connection);
		}

		@Override
		public NextTask<Void, ?, Integer> ntClear() {
			return NTDataBaseTable.this.ntClear(this.connection);
		}

		@Override
		public NextTask<Void, ?, T> ntUpdateAndReload(final T data) {
			return NTDataBaseTable.this.ntUpdateAndReload(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, T> ntUpdate(final T data) {
			return NTDataBaseTable.this.ntUpdate(this.connection, data);
		}

		@Override
		public NextTask<Void, Void, Optional<T>> ntDeleteIfExists(final T data) {
			return NTDataBaseTable.this.ntDeleteIfExists(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, Optional<T>> ntDeleteUnique(final T data) {
			return NTDataBaseTable.this.ntDeleteUnique(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, List<T>> ntDeleteUniques(final T data) {
			return NTDataBaseTable.this.ntDeleteUniques(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, T> ntDelete(final T data) {
			return NTDataBaseTable.this.ntDelete(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, T> ntInsertAndReload(final T data) {
			return NTDataBaseTable.this.ntInsertAndReload(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, T> ntInsert(final T data) {
			return NTDataBaseTable.this.ntInsert(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, List<T>> ntLoadByUnique(final T data) {
			return NTDataBaseTable.this.ntLoadByUnique(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, T> ntLoadUnique(final T data) {
			return NTDataBaseTable.this.ntLoadUnique(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, T> ntLoadIfExistsElseInsert(final T data) {
			return NTDataBaseTable.this.ntLoadIfExistsElseInsert(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, Optional<T>> ntLoadIfExists(final T data) {
			return NTDataBaseTable.this.ntLoadIfExists(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, Boolean> ntExists(final T data) {
			return NTDataBaseTable.this.ntExists(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, Boolean> ntExistsUniques(final T data) {
			return NTDataBaseTable.this.ntExistsUniques(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, Boolean> ntExistsUnique(final T data) {
			return NTDataBaseTable.this.ntExistsUnique(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, Integer> ntCountUniques(final T data) {
			return NTDataBaseTable.this.ntCountUniques(this.connection, data);
		}

		@Override
		public NextTask<Void, ?, Integer> ntCountNotNull(final T data) {
			return NTDataBaseTable.this.ntCountNotNull(this.connection, data);
		}

		@Override
		public Class<? extends NTSQLQueryable<T>> getTargetClass() {
			return NTDataBaseTable.this.getTargetClass();
		}

		@Override
		public <B> B query(final SQLQuery<T, B> query) throws DBException {
			return NTDataBaseTable.this.query(query);
		}

		@Override
		public int count() throws DBException {
			return NTDataBaseTable.this.count();
		}

		@Override
		public String getName() {
			return NTDataBaseTable.this.getName();
		}

		@Override
		public String getQualifiedName() {
			return NTDataBaseTable.this.getQualifiedName();
		}

		@Override
		public DataBaseEntryUtils getDbEntryUtils() {
			return NTDataBaseTable.this.getDbEntryUtils();
		}

		@Override
		public boolean exists() throws DBException {
			return NTDataBaseTable.this.exists();
		}

		@Override
		public DataBaseTableStatus<T, ? extends DataBaseTable<T>> create() throws DBException {
			return NTDataBaseTable.this.create();
		}

		@Override
		public DataBaseTable<T> drop() throws DBException {
			return NTDataBaseTable.this.drop();
		}

		@Override
		public int countUniques(final T data) throws DBException {
			return NTDataBaseTable.this.countUniques(data);
		}

		@Override
		public int countNotNull(final T data) throws DBException {
			return NTDataBaseTable.this.countNotNull(data);
		}

		@Override
		public boolean exists(final T data) throws DBException {
			return NTDataBaseTable.this.exists(data);
		}

		@Override
		public boolean existsUniques(final T data) throws DBException {
			return NTDataBaseTable.this.existsUniques(data);
		}

		@Override
		public boolean existsUnique(final T data) throws DBException {
			return NTDataBaseTable.this.existsUnique(data);
		}

		@Override
		public Optional<T> loadUniqueIfExists(final T data) throws DBException {
			return NTDataBaseTable.this.loadUniqueIfExists(data);
		}

		@Override
		public T loadUniqueIfExistsElseInsert(final T data) throws DBException {
			return NTDataBaseTable.this.loadUniqueIfExistsElseInsert(data);
		}

		public T loadIfExistsElseInsert(final T data) throws DBException {
			return NTDataBaseTable.this.loadIfExistsElseInsert(data);
		}

		public Optional<T> loadIfExists(final T data) throws DBException {
			return NTDataBaseTable.this.loadIfExists(data);
		}

		@Override
		public T loadUnique(final T data) throws DBException {
			return NTDataBaseTable.this.loadUnique(data);
		}

		@Override
		public List<T> loadByUnique(final T data) throws DBException {
			return NTDataBaseTable.this.loadByUnique(data);
		}

		@Override
		public T insert(final T data) throws DBException {
			return NTDataBaseTable.this.insert(data);
		}

		@Override
		public T insertAndReload(final T data) throws DBException {
			return NTDataBaseTable.this.insertAndReload(data);
		}

		@Override
		public T delete(final T data) throws DBException {
			return NTDataBaseTable.this.delete(data);
		}

		@Override
		public Optional<T> deleteIfExists(final T data) throws DBException {
			return NTDataBaseTable.this.deleteIfExists(data);
		}

		@Override
		public Optional<T> deleteUnique(final T data) throws DBException {
			return NTDataBaseTable.this.deleteUnique(data);
		}

		@Override
		public List<T> deleteUniques(final T data) throws DBException {
			return NTDataBaseTable.this.deleteUniques(data);
		}

		@Override
		public T update(final T data) throws DBException {
			return NTDataBaseTable.this.update(data);
		}

		@Override
		public T updateAndReload(final T data) throws DBException {
			return NTDataBaseTable.this.updateAndReload(data);
		}

		@Override
		public T load(final T data) throws DBException {
			return NTDataBaseTable.this.load(data);
		}

		@Override
		public int clear() throws DBException {
			return NTDataBaseTable.this.clear();
		}

		@Override
		public int truncate() throws DBException {
			return NTDataBaseTable.this.truncate();
		}

		@Override
		public NTDBTableTransaction<T> createTransaction() throws DBException {
			return NTDataBaseTable.this.createTransaction();
		}

		@Override
		public DataBase getDataBase() {
			return NTDataBaseTable.this.getDataBase();
		}

		@Override
		public String getCreateSQL() {
			return NTDataBaseTable.this.getCreateSQL();
		}

		@Override
		public String[] getPrimaryKeysNames() {
			return NTDataBaseTable.this.getPrimaryKeysNames();
		}

		@Override
		public String toString() {
			return "NTTableTransaction@" + System.identityHashCode(this) + " [lock=" + lock + ", closed=" + closed + ", completed="
					+ completed + ", connection=" + connection + "]";
		}

	}

	@Override
	public String toString() {
		return "NTDataBaseTable@" + System.identityHashCode(this) + " [dataBase=" + this.dataBase + ", dbEntryUtils=" + this.dbEntryUtils
				+ ", structure=" + this.structure + ", tableClass=" + this.tableClass + "]";
	}

}