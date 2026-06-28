package lu.kbra.pclib.db.table;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.SQLRequestType;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

public class DBTableProxy<V extends DataBaseTable<X>, X extends DataBaseEntry> extends DataBaseTable<X> {

	protected final V delegate;
	protected final Connection connection;

	public DBTableProxy(final V delegate, final Connection connection) {
		this.delegate = delegate;
		this.connection = connection;
	}

	@Override
	public int clear() throws DBException {
		return this.delegate.clear(this.connection);
	}

	@Override
	public int count() throws DBException {
		return this.delegate.count(this.connection);
	}

	@Override
	public int countNotNull(final X data) throws DBException {
		return this.delegate.countNotNull(this.connection, data);
	}

	@Override
	public int countUniques(final X data) throws DBException {
		return this.delegate.countUniques(this.connection, data);
	}

	@Deprecated
	@Override
	public DataBaseTableStatus<X, ? extends DataBaseTable<X>> create() throws DBException {
		return this.delegate.create(this.connection);
	}

	@Override
	public X delete(final X data) throws DBException {
		return this.delegate.delete(this.connection, data);
	}

	@Override
	public Optional<X> deleteIfExists(final X data) throws DBException {
		return this.delegate.deleteIfExists(this.connection, data);
	}

	@Override
	public Optional<X> deleteUnique(final X data) throws DBException {
		return this.delegate.deleteUnique(this.connection, data);
	}

	@Override
	public List<X> deleteUniques(final X data) throws DBException {
		return this.delegate.deleteUniques(this.connection, data);
	}

	@Deprecated
	@Override
	public DataBaseTable<X> drop() throws DBException {
		return this.delegate.drop(this.connection);
	}

	@Override
	public boolean exists() throws DBException {
		return this.delegate.exists(this.connection);
	}

	@Override
	public boolean exists(final X data) throws DBException {
		return this.delegate.exists(this.connection, data);
	}

	@Override
	public boolean existsUnique(final X data) throws DBException {
		return this.delegate.existsUnique(this.connection, data);
	}

	@Override
	public boolean existsUniques(final X data) throws DBException {
		return this.delegate.existsUniques(this.connection, data);
	}

	@Override
	public String[] getColumnNames() {
		return this.delegate.getColumnNames();
	}

	@Override
	public ColumnData[] getColumns() {
		return this.delegate.getColumns();
	}

	@Override
	public ConstraintData[] getConstraints() {
		return this.delegate.getConstraints();
	}

	@Override
	@Deprecated
	public String[] getCreateSQL() {
		return super.getCreateSQL();
	}

	@Override
	public DataBase getDatabase() {
		return this.delegate.getDatabase();
	}

	@Override
	public DataBaseEntryUtils getDataBaseEntryUtils() {
		return this.delegate.getDataBaseEntryUtils();
	}

	public V getDelegate() {
		return this.delegate;
	}

	@Override
	public Class<DataBaseEntry> getEntryType() {
		return this.delegate.getEntryType();
	}

	@Override
	public String getName() {
		return this.delegate.getName();
	}

	@Override
	public String[] getPrimaryKeysNames() {
		return this.delegate.getPrimaryKeysNames();
	}

	@Override
	public String getQualifiedName() {
		return this.delegate.getQualifiedName();
	}

	@Override
	public Class<? extends AbstractDBTable<X>> getTableClass() {
		return this.delegate.getTableClass();
	}

	@Override
	public Class<? extends SQLQueryable<X>> getTargetClass() {
		return this.delegate.getTargetClass();
	}

	@Override
	public X insert(final X data) throws DBException {
		return this.delegate.insert(this.connection, data);
	}

	@Override
	public X insertAndReload(final X data) throws DBException {
		return this.delegate.insertAndReload(this.connection, data);
	}

	@Override
	public X load(final X data) throws DBException {
		return this.delegate.load(this.connection, data);
	}

	@Override
	public List<X> loadByUnique(final X data) throws DBException {
		return this.delegate.loadByUnique(this.connection, data);
	}

	@Override
	public Optional<X> loadIfExists(final X data) throws DBException {
		return this.delegate.loadIfExists(this.connection, data);
	}

	@Override
	public X loadIfExistsElseInsert(final X data) throws DBException {
		return this.delegate.loadIfExistsElseInsert(this.connection, data);
	}

	@Override
	public X loadUnique(final X data) throws DBException {
		return this.delegate.loadUnique(this.connection, data);
	}

	@Override
	public Optional<X> loadUniqueIfExists(final X data) throws DBException {
		return this.delegate.loadUniqueIfExists(this.connection, data);
	}

	@Override
	public X loadUniqueIfExistsElseInsert(final X data) throws DBException {
		return this.delegate.loadUniqueIfExistsElseInsert(this.connection, data);
	}

	@Override
	public <B> B query(final SQLQuery<X, B> query) throws DBException {
		return this.delegate.query(this.connection, query);
	}

	@Deprecated
	@Override
	public void requestHook(final SQLRequestType type, final Object query) {
		throw new UnsupportedOperationException("This should never be called.");
	}

	@Deprecated
	@Override
	public void setDbEntryUtils(final DataBaseEntryUtils dbEntryUtils) {
		this.delegate.setDbEntryUtils(dbEntryUtils);
	}

	@Override
	public String toString() {
		return "DBTableProxy@" + System.identityHashCode(this) + " [delegate=" + this.delegate + "]";
	}

	@Deprecated
	@Override
	public int truncate() throws DBException {
		return this.delegate.truncate(this.connection);
	}

	@Override
	public X update(final X data) throws DBException {
		return this.delegate.update(this.connection, data);
	}

	@Override
	public X updateAndReload(final X data) throws DBException {
		return this.delegate.updateAndReload(this.connection, data);
	}

	@Override
	protected AbstractConnection use() throws DBException {
		throw new UnsupportedOperationException("This should never be called.");
	}

}
