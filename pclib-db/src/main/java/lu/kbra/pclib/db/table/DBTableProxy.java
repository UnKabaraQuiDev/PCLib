package lu.kbra.pclib.db.table;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.table.ConstraintData;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.AbstractConnection;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class DBTableProxy<V extends DataBaseTable<X>, X extends DataBaseEntry> extends DataBaseTable<X> {

	protected final V delegate;
	protected final Connection connection;

	public DBTableProxy(final V delegate, Connection connection) {
		this.delegate = delegate;
		this.connection = connection;
	}

	@Override
	protected AbstractConnection use() throws DBException {
		throw new UnsupportedOperationException("This should never be called.");
	}

	@Deprecated
	@Override
	public void requestHook(final SQLRequestType type, final Object query) {
		throw new UnsupportedOperationException("This should never be called.");
	}

	@Override
	public boolean exists() throws DBException {
		return this.delegate.exists(connection);
	}

	@Deprecated
	@Override
	public DataBaseTableStatus<X, ? extends DataBaseTable<X>> create() throws DBException {
		return this.delegate.create(connection);
	}

	@Deprecated
	@Override
	public DataBaseTable<X> drop() throws DBException {
		return this.delegate.drop(connection);
	}

	@Override
	public int countUniques(final X data) throws DBException {
		return this.delegate.countUniques(connection, data);
	}

	@Override
	public int countNotNull(final X data) throws DBException {
		return this.delegate.countNotNull(connection, data);
	}

	@Override
	public boolean exists(final X data) throws DBException {
		return this.delegate.exists(connection, data);
	}

	@Override
	public boolean existsUniques(final X data) throws DBException {
		return this.delegate.existsUniques(connection, data);
	}

	@Override
	public boolean existsUnique(final X data) throws DBException {
		return this.delegate.existsUnique(connection, data);
	}

	@Override
	public Optional<X> loadUniqueIfExists(final X data) throws DBException {
		return this.delegate.loadUniqueIfExists(connection, data);
	}

	@Override
	public X loadUniqueIfExistsElseInsert(final X data) throws DBException {
		return this.delegate.loadUniqueIfExistsElseInsert(connection, data);
	}

	@Override
	public X loadIfExistsElseInsert(final X data) throws DBException {
		return this.delegate.loadIfExistsElseInsert(connection, data);
	}

	@Override
	public Optional<X> loadIfExists(final X data) throws DBException {
		return this.delegate.loadIfExists(connection, data);
	}

	@Override
	public X loadUnique(final X data) throws DBException {
		return this.delegate.loadUnique(connection, data);
	}

	@Override
	public List<X> loadByUnique(final X data) throws DBException {
		return this.delegate.loadByUnique(connection, data);
	}

	@Override
	public X insert(final X data) throws DBException {
		return this.delegate.insert(connection, data);
	}

	@Override
	public X insertAndReload(final X data) throws DBException {
		return this.delegate.insertAndReload(connection, data);
	}

	@Override
	public X delete(final X data) throws DBException {
		return this.delegate.delete(connection, data);
	}

	@Override
	public Optional<X> deleteIfExists(final X data) throws DBException {
		return this.delegate.deleteIfExists(connection, data);
	}

	@Override
	public Optional<X> deleteUnique(final X data) throws DBException {
		return this.delegate.deleteUnique(connection, data);
	}

	@Override
	public List<X> deleteUniques(final X data) throws DBException {
		return this.delegate.deleteUniques(connection, data);
	}

	@Override
	public X update(final X data) throws DBException {
		return this.delegate.update(connection, data);
	}

	@Override
	public X updateAndReload(final X data) throws DBException {
		return this.delegate.updateAndReload(connection, data);
	}

	@Override
	public X load(final X data) throws DBException {
		return this.delegate.load(connection, data);
	}

	@Override
	public <B> B query(final SQLQuery<X, B> query) throws DBException {
		return this.delegate.query(connection, query);
	}

	@Override
	public int count() throws DBException {
		return this.delegate.count(connection);
	}

	@Override
	public int clear() throws DBException {
		return this.delegate.clear(connection);
	}

	@Deprecated
	@Override
	public int truncate() throws DBException {
		return this.delegate.truncate(connection);
	}

	@Deprecated
	@Override
	public String getCreateSQL() {
		return this.delegate.getCreateSQL();
	}

	@Override
	public String getName() {
		return this.delegate.getName();
	}

	@Override
	public String getQualifiedName() {
		return this.delegate.getQualifiedName();
	}

	@Override
	public Class<? extends SQLQueryable<X>> getTargetClass() {
		return this.delegate.getTargetClass();
	}

	@Override
	public Class<? extends AbstractDBTable<X>> getTableClass() {
		return this.delegate.getTableClass();
	}

	@Override
	public Class<DataBaseEntry> getEntryType() {
		return this.delegate.getEntryType();
	}

	@Override
	public ColumnData[] getColumns() {
		return this.delegate.getColumns();
	}

	@Override
	public String getCharacterSet() {
		return this.delegate.getCharacterSet();
	}

	@Override
	public String getCollation() {
		return this.delegate.getCollation();
	}

	@Override
	public String getEngine() {
		return this.delegate.getEngine();
	}

	@Override
	public ConstraintData[] getConstraints() {
		return this.delegate.getConstraints();
	}

	@Override
	public String[] getColumnNames() {
		return this.delegate.getColumnNames();
	}

	@Override
	public String[] getPrimaryKeysNames() {
		return this.delegate.getPrimaryKeysNames();
	}

	@Override
	public DataBase getDataBase() {
		return this.delegate.getDataBase();
	}

	@Override
	public DataBaseEntryUtils getDbEntryUtils() {
		return this.delegate.getDbEntryUtils();
	}

	@Deprecated
	@Override
	public void setDbEntryUtils(final DataBaseEntryUtils dbEntryUtils) {
		this.delegate.setDbEntryUtils(dbEntryUtils);
	}

	public V getDelegate() {
		return this.delegate;
	}

	@Override
	public String toString() {
		return "DBTableProxy@" + System.identityHashCode(this) + " [delegate=" + this.delegate + "]";
	}

}