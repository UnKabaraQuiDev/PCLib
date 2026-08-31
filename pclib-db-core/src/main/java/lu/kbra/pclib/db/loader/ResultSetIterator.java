package lu.kbra.pclib.db.loader;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.EntryInstanceProvider;
import lu.kbra.pclib.db.utils.impl.EntryInstanceProvider.FactoryMethod;

public final class ResultSetIterator<T extends DatabaseEntry> implements Iterator<T> {

	private final SQLQueryable<? extends T> table;
	private final Class<T> entryClazz;
	private final ResultSet rs;
	private final DatabaseEntryUtils databaseEntryUtils;
	private final EntryInstanceProvider entryInstanceProvider;
	private final FactoryMethod factoryMethod;

	private boolean hasNext;
	private boolean initialized;

	public ResultSetIterator(final SQLQueryable<? extends T> table, final Class<T> entryClazz, final ResultSet rs) throws SQLException {
		this.table = Objects.requireNonNull(table, "table is null.");
		this.entryClazz = Objects.requireNonNull(entryClazz, "entryClazz is null.");
		this.rs = Objects.requireNonNull(rs, "rs is null.");

		final ResultSetMetaData resultMetaData = rs.getMetaData();
		final int columnCount = resultMetaData.getColumnCount();
		final String[] columns = new String[columnCount];

		for (int i = 0; i < columns.length; i++) {
			columns[i] = resultMetaData.getColumnLabel(i + 1);
		}

		this.databaseEntryUtils = table.getDatabaseEntryUtils();
		this.entryInstanceProvider = databaseEntryUtils.getEntryInstanceProvider();
		this.factoryMethod = this.entryInstanceProvider.getFactoryMethod(table, columns);
	}

	@Override
	public boolean hasNext() {
		if (this.initialized) {
			return this.hasNext;
		}

		try {
			this.hasNext = this.rs.next();
			this.initialized = true;
			return this.hasNext;
		} catch (final SQLException e) {
			throw new RuntimeException("Failed to advance ResultSet.", e);
		}
	}

	@Override
	public T next() {
		try {
			if (!this.initialized) {
				this.hasNext = this.rs.next();
				this.initialized = true;
			} else if (!this.hasNext) {
				throw new NoSuchElementException();
			}

			final T copy;

			if (this.factoryMethod != null) {
				copy = this.databaseEntryUtils.fillLoad(this.entryClazz, this.rs, this.factoryMethod);
			} else {
				copy = this.entryInstanceProvider.instance(this.table);
				this.databaseEntryUtils.fillLoad(this.table, copy, this.rs);
			}

			this.initialized = false;
			return copy;
		} catch (final SQLException e) {
			throw new RuntimeException("Failed to load ResultSet row.", e);
		}
	}

}
