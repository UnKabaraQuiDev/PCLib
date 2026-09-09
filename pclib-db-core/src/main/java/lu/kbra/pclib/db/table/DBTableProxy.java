package lu.kbra.pclib.db.table;

import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.utils.SQLQueryableHookManager;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.impl.function.ThrowingFunction;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class DBTableProxy<V extends DatabaseTable<T>, T extends DatabaseEntry> extends DatabaseTable<T> {

	protected final V delegate;
	protected final Supplier<AbstractConnection> useMethod;

	public DBTableProxy(final V delegate, final Supplier<AbstractConnection> useMethod) {
		this.delegate = delegate;
		this.useMethod = useMethod;
	}

	@Override
	public int clear() throws DBException {
		return this.useWithTry(c -> this.delegate.clear(c));
	}

	@Override
	public int count() throws DBException {
		return this.useWithTry(c -> this.delegate.count(c));
	}

	@Override
	public int countNotNull(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.countNotNull(c, data));
	}

	@Override
	public int countUniques(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.countUniques(c, data));
	}

	@Override
	@Deprecated
	public DatabaseTableStatus create() throws DBException {
		throw new UnsupportedOperationException("Cannot create table through a proxy.");
	}

	@Override
	@Deprecated
	public DatabaseTable<T> createProxy(final Supplier<AbstractConnection> connection) {
		throw new UnsupportedOperationException("Cannot create a proxy of a proxy.");
	}

	@Override
	public T delete(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.delete(c, data));
	}

	@Override
	public <C extends Collection<T>> C deleteAll(final C datas) throws DBException {
		return this.useWithTry(c -> this.delegate.deleteAll(c, datas));
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D deleteIfExists(final C datas, final Supplier<D> supplier)
			throws DBException {
		return this.useWithTry(c -> this.delegate.deleteIfExists(c, datas, supplier));
	}

	@Override
	public Optional<T> deleteIfExists(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.deleteIfExists(c, data));
	}

	@Override
	public Optional<T> deleteUnique(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.deleteUnique(c, data));
	}

	@Override
	public List<T> deleteUniques(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.deleteUniques(c, data));
	}

	@Override
	@Deprecated
	public AbstractDBTable<T> drop() throws DBException {
		throw new UnsupportedOperationException("Cannot drop table through a proxy.");
	}

	@Override
	@Deprecated
	public boolean exists() throws DBException {
		throw new UnsupportedOperationException("Cannot check table status through a proxy.");
	}

	@Override
	public boolean exists(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.exists(c, data));
	}

	@Override
	public boolean existsUnique(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.existsUnique(c, data));
	}

	@Override
	public boolean existsUniques(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.existsUniques(c, data));
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D filterExists(final C datas, final Supplier<D> supplier) throws DBException {
		return this.useWithTry(c -> this.delegate.filterExists(c, datas, supplier));
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D filterExistsUnique(final C datas, final Supplier<D> supplier)
			throws DBException {
		return this.useWithTry(c -> this.delegate.filterExistsUnique(c, datas, supplier));
	}

	@Override
	public DatabaseConnector getConnector() {
		return this.delegate.getConnector();
	}

	@Override
	@Deprecated
	public String[] getCreateSQL() {
		throw new UnsupportedOperationException("Cannot create table through a proxy.");
	}

	@Override
	public Map<String, Object> getCustomHints() {
		return this.delegate.getCustomHints();
	}

	@Override
	public Database getDatabase() {
		return this.delegate.getDatabase();
	}

	@Override
	public DatabaseEntryUtils getDatabaseEntryUtils() {
		return this.delegate.getDatabaseEntryUtils();
	}

	@Override
	public SQLQueryableHookManager getQueryableHookManager() {
		return this.delegate.getQueryableHookManager();
	}

	@Override
	public String getStatementAsSQL(final Statement stmt) {
		return this.delegate.getStatementAsSQL(stmt);
	}

	@Override
	public TableStructure getStructure() {
		return this.delegate.getStructure();
	}

	@Override
	public T insert(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.insert(c, data));
	}

	@Override
	public <C extends Collection<T>> C insertAll(final C data) throws DBException {
		return this.useWithTry(c -> this.delegate.insertAll(c, data));
	}

	@Override
	public T insertAndReload(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.insertAndReload(c, data));
	}

	@Override
	public <C extends Collection<T>> C insertAndReloadAll(final C datas) throws DBException {
		return this.useWithTry(c -> this.delegate.insertAndReloadAll(c, datas));
	}

	@Override
	public T load(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.load(c, data));
	}

	@Override
	public <C extends Collection<T>> C loadAll(final C datas) throws DBException {
		return this.useWithTry(c -> this.delegate.loadAll(c, datas));
	}

	@Override
	public List<T> loadByUnique(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.loadByUnique(c, data));
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D loadAllIfExists(final C datas, final Supplier<D> supplier)
			throws DBException {
		return this.useWithTry(c -> this.delegate.loadIfExists(c, datas, supplier));
	}

	@Override
	public Optional<T> loadIfExists(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.loadIfExists(c, data));
	}

	@Override
	public T loadIfExistsElseInsert(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.loadIfExistsElseInsert(c, data));
	}

	@Override
	public T loadUnique(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.loadUnique(c, data));
	}

	@Override
	public Optional<T> loadUniqueIfExists(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.loadUniqueIfExists(c, data));
	}

	@Override
	public T loadUniqueIfExistsElseInsert(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.loadUniqueIfExistsElseInsert(c, data));
	}

	@Override
	public <B> B query(final SQLQuery<T, B> query) throws DBException {
		return this.useWithTry(c -> this.delegate.query(c, query));
	}

	@Override
	@Deprecated
	public void setDbEntryUtils(final DatabaseEntryUtils dbEntryUtils) {
		throw new UnsupportedOperationException("Cannot change a proxy's DatabaseEntryUtils.");
	}

	@Override
	@Deprecated
	public void setQueryableHookManager(final SQLQueryableHookManager queryableHookManager) {
		throw new UnsupportedOperationException("Cannot change a proxy's QueryableHookManager.");
	}

	@Override
	@Deprecated
	public void setTableStructure(final TableStructure tableStructure) {
		throw new UnsupportedOperationException("Cannot change a proxy's TableStructure.");
	}

	@Override
	@Deprecated
	public int truncate() throws DBException {
		throw new UnsupportedOperationException("Cannot truncate table through a proxy.");
	}

	@Override
	public T update(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.update(c, data));
	}

	@Override
	public <C extends Collection<T>> C updateAll(final C datas) throws DBException {
		return this.useWithTry(c -> this.delegate.updateAll(c, datas));
	}

	@Override
	public T updateAndReload(final T data) throws DBException {
		return this.useWithTry(c -> this.delegate.updateAndReload(c, data));
	}

	@Override
	public <C extends Collection<T>> C updateAndReloadAll(final C datas) throws DBException {
		return this.useWithTry(c -> this.delegate.updateAndReloadAll(c, datas));
	}

	@Override
	protected final AbstractConnection use() throws DBException {
		return this.useMethod.get();
	}

	protected final <R, T extends RuntimeException> R useWithTry(final ThrowingFunction<AbstractConnection, R, T> supplier) {
		try (AbstractConnection c = this.use()) {
			return supplier.apply(c);
		}
	}

	@Override
	public Optional<T> updateAndReloadIfExists(final T data) {
		return this.useWithTry(c -> this.delegate.updateAndReloadIfExists(c, data));
	}

	@Override
	public T updateAndReloadIfExistsElseInsert(final T data) {
		return this.useWithTry(c -> this.delegate.updateAndReloadIfExistsElseInsert(c, data));
	}

	@Override
	public T updateAndReloadIfExistsElseInsertAndReload(final T data) {
		return this.useWithTry(c -> this.delegate.updateAndReloadIfExistsElseInsertAndReload(c, data));
	}

	@Override
	public Optional<T> updateIfExists(final T data) {
		return this.useWithTry(c -> this.delegate.updateIfExists(c, data));
	}

	@Override
	public T updateIfExistsElseInsert(final T data) {
		return this.useWithTry(c -> this.delegate.updateIfExistsElseInsert(c, data));
	}

	@Override
	public T updateIfExistsElseInsertAndReload(final T data) {
		return this.useWithTry(c -> this.delegate.updateIfExistsElseInsertAndReload(c, data));
	}

}
