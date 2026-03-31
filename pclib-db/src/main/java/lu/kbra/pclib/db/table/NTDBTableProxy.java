package lu.kbra.pclib.db.table;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.table.ConstraintData;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.AbstractConnection;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.NTSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class NTDBTableProxy<V extends NTDataBaseTable<X>, X extends DataBaseEntry> extends NTDataBaseTable<X> {

	protected final V delegate;
	protected final Connection connection;

	public NTDBTableProxy(final V delegate, final Connection connection) {
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
	public <B> NextTask<Void, ?, B> ntQuery(final SQLQuery<X, B> query) {
		return this.delegate.ntQuery(this.connection, query);
	}

	@Override
	public NextTask<Void, ?, Boolean> ntExists() {
		return this.delegate.ntExists(this.connection);
	}

	@Override
	public NextTask<Void, ?, DataBaseTableStatus<X, ? extends NTAbstractDBTable<X>>> ntCreate() {
		return this.delegate.ntCreate(this.connection);
	}

	@Override
	public NextTask<Void, ?, NTAbstractDBTable<X>> ntDrop() {
		return this.delegate.ntDrop(this.connection);
	}

	@Override
	public NextTask<Void, ?, X> ntLoad(final X data) {
		return this.delegate.ntLoad(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, Integer> ntCount() {
		return this.delegate.ntCount(this.connection);
	}

	@Override
	public NextTask<Void, ?, Integer> ntTruncate() {
		return this.delegate.ntTruncate(this.connection);
	}

	@Override
	public NextTask<Void, ?, Integer> ntClear() {
		return this.delegate.ntClear(this.connection);
	}

	@Override
	public NextTask<Void, ?, X> ntUpdateAndReload(final X data) {
		return this.delegate.ntUpdateAndReload(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, X> ntUpdate(final X data) {
		return this.delegate.ntUpdate(this.connection, data);
	}

	@Override
	public NextTask<Void, Void, Optional<X>> ntDeleteIfExists(final X data) {
		return this.delegate.ntDeleteIfExists(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, Optional<X>> ntDeleteUnique(final X data) {
		return this.delegate.ntDeleteUnique(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, List<X>> ntDeleteUniques(final X data) {
		return this.delegate.ntDeleteUniques(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, X> ntDelete(final X data) {
		return this.delegate.ntDelete(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, X> ntInsertAndReload(final X data) {
		return this.delegate.ntInsertAndReload(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, X> ntInsert(final X data) {
		return this.delegate.ntInsert(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, List<X>> ntLoadByUnique(final X data) {
		return this.delegate.ntLoadByUnique(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, X> ntLoadUnique(final X data) {
		return this.delegate.ntLoadUnique(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, X> ntLoadIfExistsElseInsert(final X data) {
		return this.delegate.ntLoadIfExistsElseInsert(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, Optional<X>> ntLoadIfExists(final X data) {
		return this.delegate.ntLoadIfExists(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, Boolean> ntExists(final X data) {
		return this.delegate.ntExists(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, Boolean> ntExistsUniques(final X data) {
		return this.delegate.ntExistsUniques(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, Boolean> ntExistsUnique(final X data) {
		return this.delegate.ntExistsUnique(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, Integer> ntCountUniques(final X data) {
		return this.delegate.ntCountUniques(this.connection, data);
	}

	@Override
	public NextTask<Void, ?, Integer> ntCountNotNull(final X data) {
		return this.delegate.ntCountNotNull(this.connection, data);
	}

	@Override
	public boolean exists() throws DBException {
		return this.delegate.exists(this.connection);
	}

	@Deprecated
	@Override
	public DataBaseTableStatus<X, ? extends DataBaseTable<X>> create() throws DBException {
		return this.delegate.create(this.connection);
	}

	@Deprecated
	@Override
	public DataBaseTable<X> drop() throws DBException {
		return this.delegate.drop(this.connection);
	}

	@Override
	public int countUniques(final X data) throws DBException {
		return this.delegate.countUniques(this.connection, data);
	}

	@Override
	public int countNotNull(final X data) throws DBException {
		return this.delegate.countNotNull(this.connection, data);
	}

	@Override
	public boolean exists(final X data) throws DBException {
		return this.delegate.exists(this.connection, data);
	}

	@Override
	public boolean existsUniques(final X data) throws DBException {
		return this.delegate.existsUniques(this.connection, data);
	}

	@Override
	public boolean existsUnique(final X data) throws DBException {
		return this.delegate.existsUnique(this.connection, data);
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
	public X loadIfExistsElseInsert(final X data) throws DBException {
		return this.delegate.loadIfExistsElseInsert(this.connection, data);
	}

	@Override
	public Optional<X> loadIfExists(final X data) throws DBException {
		return this.delegate.loadIfExists(this.connection, data);
	}

	@Override
	public X loadUnique(final X data) throws DBException {
		return this.delegate.loadUnique(this.connection, data);
	}

	@Override
	public List<X> loadByUnique(final X data) throws DBException {
		return this.delegate.loadByUnique(this.connection, data);
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

	@Override
	public X update(final X data) throws DBException {
		return this.delegate.update(this.connection, data);
	}

	@Override
	public X updateAndReload(final X data) throws DBException {
		return this.delegate.updateAndReload(this.connection, data);
	}

	@Override
	public X load(final X data) throws DBException {
		return this.delegate.load(this.connection, data);
	}

	@Override
	public <B> B query(final SQLQuery<X, B> query) throws DBException {
		return this.delegate.query(this.connection, query);
	}

	@Override
	public int count() throws DBException {
		return this.delegate.count(this.connection);
	}

	@Override
	public int clear() throws DBException {
		return this.delegate.clear(this.connection);
	}

	@Deprecated
	@Override
	public int truncate() throws DBException {
		return this.delegate.truncate(this.connection);
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
	public Class<? extends NTSQLQueryable<X>> getTargetClass() {
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
