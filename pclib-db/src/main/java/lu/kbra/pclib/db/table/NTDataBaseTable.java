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
import lu.kbra.pclib.impl.ThrowingSupplier;

@Deprecated
public class NTDataBaseTable<T extends DataBaseEntry> extends DataBaseTable<T> implements NTAbstractDBTable<T> {

	@Deprecated
	protected NTDataBaseTable() {

	}

	@Deprecated
	public NTDataBaseTable(final DataBase dataBase) {
		super(dataBase);
	}

	@Deprecated
	public NTDataBaseTable(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	@Deprecated
	public NTDataBaseTable(
			final DataBase dataBase,
			final DataBaseEntryUtils dbEntryUtils,
			final Class<? extends AbstractDBTable<T>> tableClass) {
		super(dataBase, dbEntryUtils, tableClass);
	}

	@Deprecated
	@Override
	public <B> NextTask<Void, ?, B> ntQuery(final SQLQuery<T, B> query) {
		return NextTask.create(() -> this.query(query));
	}

	@Deprecated
	public <B> NextTask<Void, ?, B> ntQuery(final Connection c, final SQLQuery<T, B> query) {
		return NextTask.create(() -> this.query(c, query));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Boolean> ntExists() {
		return NextTask.create((ThrowingSupplier<Boolean, Throwable>) this::exists);
	}

	@Deprecated
	public NextTask<Void, ?, Boolean> ntExists(final Connection c) {
		return NextTask.create(() -> this.exists(c));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, DataBaseTableStatus<T, ? extends NTAbstractDBTable<T>>> ntCreate() {
		return NextTask.create(() -> (DataBaseTableStatus<T, ? extends NTDataBaseTable<T>>) this.create());
	}

	@Deprecated
	public NextTask<Void, ?, DataBaseTableStatus<T, ? extends NTAbstractDBTable<T>>> ntCreate(final Connection c) {
		return NextTask.create(() -> (DataBaseTableStatus<T, ? extends NTDataBaseTable<T>>) this.create(c));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, NTAbstractDBTable<T>> ntDrop() {
		return NextTask.create(() -> {
			this.drop();
			return this;
		});
	}

	@Deprecated
	public NextTask<Void, ?, NTAbstractDBTable<T>> ntDrop(final Connection c) {
		return NextTask.create(() -> {
			this.drop(c);
			return this;
		});
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, T> ntLoad(final T data) {
		return NextTask.create(() -> this.load(data));
	}

	@Deprecated
	public NextTask<Void, ?, T> ntLoad(final Connection c, final T data) {
		return NextTask.create(() -> this.load(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Integer> ntCount() {
		return NextTask.create((ThrowingSupplier<Integer, Throwable>) this::count);
	}

	@Deprecated
	public NextTask<Void, ?, Integer> ntCount(final Connection c) {
		return NextTask.create(() -> this.count(c));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Integer> ntTruncate() {
		return NextTask.create((ThrowingSupplier<Integer, Throwable>) this::truncate);
	}

	@Deprecated
	public NextTask<Void, ?, Integer> ntTruncate(final Connection c) {
		return NextTask.create(() -> this.truncate(c));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Integer> ntClear() {
		return NextTask.create((ThrowingSupplier<Integer, Throwable>) this::clear);
	}

	@Deprecated
	public NextTask<Void, ?, Integer> ntClear(final Connection c) {
		return NextTask.create(() -> this.clear(c));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, T> ntUpdateAndReload(final T data) {
		return NextTask.create(() -> this.updateAndReload(data));
	}

	@Deprecated
	public NextTask<Void, ?, T> ntUpdateAndReload(final Connection c, final T data) {
		return NextTask.create(() -> this.updateAndReload(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, T> ntUpdate(final T data) {
		return NextTask.create(() -> this.update(data));
	}

	@Deprecated
	public NextTask<Void, ?, T> ntUpdate(final Connection c, final T data) {
		return NextTask.create(() -> this.update(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, Void, Optional<T>> ntDeleteIfExists(final T data) {
		return NextTask.create(() -> this.deleteIfExists(data));
	}

	@Deprecated
	public NextTask<Void, Void, Optional<T>> ntDeleteIfExists(final Connection c, final T data) {
		return NextTask.create(() -> this.deleteIfExists(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Optional<T>> ntDeleteUnique(final T data) {
		return NextTask.create(() -> this.deleteUnique(data));
	}

	@Deprecated
	public NextTask<Void, ?, Optional<T>> ntDeleteUnique(final Connection c, final T data) {
		return NextTask.create(() -> this.deleteUnique(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, List<T>> ntDeleteUniques(final T data) {
		return NextTask.create(() -> this.deleteUniques(data));
	}

	@Deprecated
	public NextTask<Void, ?, List<T>> ntDeleteUniques(final Connection c, final T data) {
		return NextTask.create(() -> this.deleteUniques(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, T> ntDelete(final T data) {
		return NextTask.create(() -> this.delete(data));
	}

	@Deprecated
	public NextTask<Void, ?, T> ntDelete(final Connection c, final T data) {
		return NextTask.create(() -> this.delete(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, T> ntInsertAndReload(final T data) {
		return NextTask.create(() -> this.insertAndReload(data));
	}

	@Deprecated
	public NextTask<Void, ?, T> ntInsertAndReload(final Connection c, final T data) {
		return NextTask.create(() -> this.insertAndReload(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, T> ntInsert(final T data) {
		return NextTask.create(() -> this.insert(data));
	}

	@Deprecated
	public NextTask<Void, ?, T> ntInsert(final Connection c, final T data) {
		return NextTask.create(() -> this.insert(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, List<T>> ntLoadByUnique(final T data) {
		return NextTask.create(() -> this.loadByUnique(data));
	}

	@Deprecated
	public NextTask<Void, ?, List<T>> ntLoadByUnique(final Connection c, final T data) {
		return NextTask.create(() -> this.loadByUnique(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, T> ntLoadUnique(final T data) {
		return NextTask.create(() -> this.loadUnique(data));
	}

	@Deprecated
	public NextTask<Void, ?, T> ntLoadUnique(final Connection c, final T data) {
		return NextTask.create(() -> this.loadUnique(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, T> ntLoadIfExistsElseInsert(final T data) {
		return NextTask.create(() -> this.loadUniqueIfExistsElseInsert(data));
	}

	@Deprecated
	public NextTask<Void, ?, T> ntLoadIfExistsElseInsert(final Connection c, final T data) {
		return NextTask.create(() -> this.loadUniqueIfExistsElseInsert(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Optional<T>> ntLoadIfExists(final T data) {
		return NextTask.create(() -> this.loadUniqueIfExists(data));
	}

	@Deprecated
	public NextTask<Void, ?, Optional<T>> ntLoadIfExists(final Connection c, final T data) {
		return NextTask.create(() -> this.loadUniqueIfExists(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Boolean> ntExists(final T data) {
		return NextTask.create(() -> this.exists(data));
	}

	@Deprecated
	public NextTask<Void, ?, Boolean> ntExists(final Connection c, final T data) {
		return NextTask.create(() -> this.exists(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Boolean> ntExistsUniques(final T data) {
		return NextTask.create(() -> this.existsUniques(data));
	}

	@Deprecated
	public NextTask<Void, ?, Boolean> ntExistsUniques(final Connection c, final T data) {
		return NextTask.create(() -> this.existsUniques(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Boolean> ntExistsUnique(final T data) {
		return NextTask.create(() -> this.existsUnique(data));
	}

	@Deprecated
	public NextTask<Void, ?, Boolean> ntExistsUnique(final Connection c, final T data) {
		return NextTask.create(() -> this.existsUnique(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Integer> ntCountUniques(final T data) {
		return NextTask.create(() -> this.countUniques(data));
	}

	@Deprecated
	public NextTask<Void, ?, Integer> ntCountUniques(final Connection c, final T data) {
		return NextTask.create(() -> this.countUniques(c, data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Integer> ntCountNotNull(final T data) {
		return NextTask.create(() -> this.countNotNull(data));
	}

	@Deprecated
	public NextTask<Void, ?, Integer> ntCountNotNull(final Connection c, final T data) {
		return NextTask.create(() -> this.countNotNull(c, data));
	}

	@Deprecated
	@Override
	public Class<? extends NTSQLQueryable<T>> getTargetClass() {
		return (Class<? extends NTSQLQueryable<T>>) super.getTargetClass();
	}

	@Deprecated
	@Override
	public NTDataBaseTable<T> createProxy(final Connection connection) {
		return new NTDBTableProxy<>(this, connection);
	}

	@Deprecated
	@Override
	public String toString() {
		return "NTDataBaseTable@" + System.identityHashCode(this) + " [dataBase=" + this.dataBase + ", dbEntryUtils=" + this.dbEntryUtils
				+ ", structure=" + this.tableStructure + ", tableClass=" + this.tableClass + "]";
	}

}
