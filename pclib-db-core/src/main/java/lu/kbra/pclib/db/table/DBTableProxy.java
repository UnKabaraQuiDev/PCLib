package lu.kbra.pclib.db.table;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.ToString;
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

@ToString
@Getter
public class DBTableProxy<V extends DatabaseTable<X>, X extends DatabaseEntry> extends DatabaseTable<X> {

	protected final V delegate;
	protected final Supplier<AbstractConnection> useMethod;

	public DBTableProxy(final V delegate, final Supplier<AbstractConnection> useMethod) {
		this.delegate = delegate;
		this.useMethod = useMethod;
	}

	protected final <R, T extends RuntimeException> R useWithTry(final ThrowingFunction<AbstractConnection, R, T> supplier) {
		try (AbstractConnection c = this.use()) {
			return supplier.apply(c);
		}
	}

	@Override
	protected final AbstractConnection use() throws DBException {
		return this.useMethod.get();
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
	public TableStructure getStructure() {
		return this.delegate.getStructure();
	}

	@Override
	@Deprecated
	public void setQueryableHookManager(final SQLQueryableHookManager queryableHookManager) {
		throw new UnsupportedOperationException("Cannot change a proxy's QueryableHookManager.");
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
	public int countNotNull(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.countNotNull(c, data));
	}

	@Override
	public int countUniques(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.countUniques(c, data));
	}

	@Override
	@Deprecated
	public DatabaseTableStatus<X, ? extends DatabaseTable<X>> create() throws DBException {
		throw new UnsupportedOperationException("Cannot create table through a proxy.");
	}

	@Override
	@Deprecated
	public DatabaseTable<X> createProxy(final Supplier<AbstractConnection> connection) {
		throw new UnsupportedOperationException("Cannot create a proxy of a proxy.");
	}

	@Override
	public X delete(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.delete(c, data));
	}

	@Override
	public <C extends Collection<X>> C deleteAll(final C datas) throws DBException {
		return this.useWithTry(c -> this.delegate.deleteAll(c, datas));
	}

	@Override
	public <C extends Collection<X>, D extends Collection<X>> D deleteIfExists(final C datas, final Supplier<D> supplier)
			throws DBException {
		return this.useWithTry(c -> this.delegate.deleteIfExists(c, datas, supplier));
	}

	@Override
	public Optional<X> deleteIfExists(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.deleteIfExists(c, data));
	}

	@Override
	public Optional<X> deleteUnique(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.deleteUnique(c, data));
	}

	@Override
	public List<X> deleteUniques(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.deleteUniques(c, data));
	}

	@Override
	@Deprecated
	public DatabaseTable<X> drop() throws DBException {
		throw new UnsupportedOperationException("Cannot drop table through a proxy.");
	}

	@Override
	@Deprecated
	public boolean exists() throws DBException {
		throw new UnsupportedOperationException("Cannot check table status through a proxy.");
	}

	@Override
	public boolean exists(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.exists(c, data));
	}

	@Override
	public boolean existsUnique(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.existsUnique(c, data));
	}

	@Override
	public boolean existsUniques(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.existsUniques(c, data));
	}

	@Override
	public <C extends Collection<X>, D extends Collection<X>> D filterExists(final C datas, final Supplier<D> supplier) throws DBException {
		return this.useWithTry(c -> this.delegate.filterExists(c, datas, supplier));
	}

	@Override
	public <C extends Collection<X>, D extends Collection<X>> D filterExistsUnique(final C datas, final Supplier<D> supplier)
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
	public X insert(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.insert(c, data));
	}

	@Override
	public <C extends Collection<X>> C insertAll(final C data) throws DBException {
		return this.useWithTry(c -> this.delegate.insertAll(c, data));
	}

	@Override
	public X insertAndReload(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.insertAndReload(c, data));
	}

	@Override
	public <C extends Collection<X>> C insertAndReloadAll(final C datas) throws DBException {
		return this.useWithTry(c -> this.delegate.insertAndReloadAll(c, datas));
	}

	@Override
	public X load(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.load(c, data));
	}

	@Override
	public <C extends Collection<X>> C loadAll(final C datas) throws DBException {
		return this.useWithTry(c -> this.delegate.loadAll(c, datas));
	}

	@Override
	public List<X> loadByUnique(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.loadByUnique(c, data));
	}

	@Override
	public <C extends Collection<X>, D extends Collection<X>> D loadIfExists(final C datas, final Supplier<D> supplier) throws DBException {
		return this.useWithTry(c -> this.delegate.loadIfExists(c, datas, supplier));
	}

	@Override
	public Optional<X> loadIfExists(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.loadIfExists(c, data));
	}

	@Override
	public X loadIfExistsElseInsert(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.loadIfExistsElseInsert(c, data));
	}

	@Override
	public X loadUnique(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.loadUnique(c, data));
	}

	@Override
	public Optional<X> loadUniqueIfExists(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.loadUniqueIfExists(c, data));
	}

	@Override
	public X loadUniqueIfExistsElseInsert(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.loadUniqueIfExistsElseInsert(c, data));
	}

	@Override
	public <B> B query(final SQLQuery<X, B> query) throws DBException {
		return this.useWithTry(c -> this.delegate.query(c, query));
	}

	@Override
	@Deprecated
	public void setDbEntryUtils(final DatabaseEntryUtils dbEntryUtils) {
		throw new UnsupportedOperationException("Cannot change a proxy's DatabaseEntryUtils.");
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
	public X update(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.update(c, data));
	}

	@Override
	public <C extends Collection<X>> C updateAll(final C datas) throws DBException {
		return this.useWithTry(c -> this.delegate.updateAll(c, datas));
	}

	@Override
	public X updateAndReload(final X data) throws DBException {
		return this.useWithTry(c -> this.delegate.updateAndReload(c, data));
	}

	@Override
	public <C extends Collection<X>> C updateAndReloadAll(final C datas) throws DBException {
		return this.useWithTry(c -> this.delegate.updateAndReloadAll(c, datas));
	}

}
