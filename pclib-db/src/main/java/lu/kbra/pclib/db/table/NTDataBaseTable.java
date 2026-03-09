package lu.kbra.pclib.db.table;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.NTSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

@Deprecated
public class NTDataBaseTable<T extends DataBaseEntry> extends DataBaseTable<T> implements NTAbstractDBTable<T> {

	protected NTDataBaseTable() {

	}

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

	@Override
	public NTDataBaseTable<T> createProxy(Connection connection) {
		return new NTDBTableProxy<>(this, connection);
	}

	@Override
	public String toString() {
		return "NTDataBaseTable@" + System.identityHashCode(this) + " [dataBase=" + this.dataBase + ", dbEntryUtils=" + this.dbEntryUtils
				+ ", structure=" + this.structure + ", tableClass=" + this.tableClass + "]";
	}

}