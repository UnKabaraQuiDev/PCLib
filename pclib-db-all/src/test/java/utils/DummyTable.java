package utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.MySQLDatabaseConnector;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.DatabaseScanner;
import lu.kbra.pclib.db.utils.SQLQueryableHookManager;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import lombok.Getter;

@Getter
public class DummyTable<T extends DatabaseEntry> implements AbstractDBTable<T> {

	private final DatabaseEntryUtils databaseEntryUtils;
	private TableStructure structure;
	private final Database database;
	private final Map<String, Object> customHints;

	public DummyTable(final DatabaseEntryUtils utils, final Class<T> entryClass) {
		this.databaseEntryUtils = utils;
		this.customHints = new HashMap<>();
		this.database = new Database(new MySQLDatabaseConnector("username", "password", "host", 1234), "dummy_database", utils);
		this.database.setDatabaseStructure(new DatabaseStructure("dummy_database",
				utils.getStructureVisitor().qualifiedName("dummy_database"),
				this.database.getCustomHints(),
				null));
		this.customHints.put(DefaultQueryableHints.ENTRY_CLASS, entryClass);
		this.customHints.put(DefaultQueryableHints.TARGET_CLASS, this.getClass());
		this.customHints.put(DefaultQueryableHints.TABLE_ID, this.getClass().getName());
	}

	public DummyTable(final DatabaseEntryUtils utils, final Class<T> entryClass, final Database database) {
		this.databaseEntryUtils = utils;
		this.customHints = new HashMap<>();
		this.database = database;
		this.customHints.put(DefaultQueryableHints.ENTRY_CLASS, entryClass);
		this.customHints.put(DefaultQueryableHints.TARGET_CLASS, this.getClass());
		this.customHints.put(DefaultQueryableHints.TABLE_ID, this.getClass().getName());
	}

	public TableStructure scan() {
		new DatabaseScanner(this.database).register(this, customHints, new HashMap<>()).doScan();
		return this.structure;
	}

	@Override
	public void setTableStructure(final TableStructure tableStructure) {
		this.structure = tableStructure;
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

	@Override
	public int clear() throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int countNotNull(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int countUniques(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T delete(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<T> deleteIfExists(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<T> deleteUnique(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<T> deleteUniques(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public AbstractDBTable<T> drop() throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean exists(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean existsUnique(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean existsUniques(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getCreateSQL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setQueryableHookManager(final SQLQueryableHookManager queryableHookManager) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T insert(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T insertAndReload(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T load(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<T> loadByUnique(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T loadUnique(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<T> loadUniqueIfExists(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<T> loadIfExists(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T loadUniqueIfExistsElseInsert(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T loadUniqueIfExistsElseInsertAndReload(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int truncate() throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T update(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T updateAndReload(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<T> updateAndReloadIfExists(final T data) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T updateAndReloadIfExistsElseInsert(final T data) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T updateAndReloadIfExistsElseInsertAndReload(final T data) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<T> updateIfExists(final T data) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T updateIfExistsElseInsert(final T data) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T updateIfExistsElseInsertAndReload(final T data) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>> C insertAll(final C datas) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>> C insertAndReloadAll(final C datas) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>> C deleteAll(final C datas) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>> C updateAll(final C datas) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>> C updateAndReloadAll(final C datas) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>> C loadAll(final C datas) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>> C loadAllUnique(final C datas) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D loadAllByUnique(final C datas, final Supplier<D> supplier)
			throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D loadAllIfExists(final C datas, final Supplier<D> supplier)
			throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D deleteAllIfExists(final C datas, final Supplier<D> supplier)
			throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D filterExists(final C datas, final Supplier<D> supplier) throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D filterExistsUnique(final C datas, final Supplier<D> supplier)
			throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D filterExistsByUnique(final C datas, final Supplier<D> supplier)
			throws DBException {
		throw new UnsupportedOperationException();
	}

	@Override
	public T loadIfExistsElseInsert(final T data) throws DBException {
		throw new UnsupportedOperationException();
	}

}
