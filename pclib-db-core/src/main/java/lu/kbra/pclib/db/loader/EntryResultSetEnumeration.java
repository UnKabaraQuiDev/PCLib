package lu.kbra.pclib.db.loader;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Objects;

import lu.kbra.pclib.db.exception.InternalDBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.EntryInstanceProvider.FactoryMethod;

import lombok.Getter;
import lombok.Setter;

public final class EntryResultSetEnumeration<T extends DatabaseEntry> implements Enumeration<T>, AutoCloseable {

	private final SQLQueryable<? extends T> table;
	private final Class<T> entryClazz;
	private final ResultSet rs;
	private final DatabaseEntryUtils databaseEntryUtils;
	private final FactoryMethod factoryMethod;

	@Getter
	@Setter
	private boolean closeStatement = false;

	private boolean hasNext;
	private boolean initialized;

	public EntryResultSetEnumeration(final SQLQueryable<T> table, final ResultSet rs) throws SQLException {
		this.table = Objects.requireNonNull(table, "table is null.");
		this.entryClazz = table.getEntryClass();
		this.rs = Objects.requireNonNull(rs, "rs is null.");

		final ResultSetMetaData resultMetaData = rs.getMetaData();
		final int columnCount = resultMetaData.getColumnCount();
		final String[] columns = new String[columnCount];

		for (int i = 0; i < columns.length; i++) {
			columns[i] = resultMetaData.getColumnLabel(i + 1);
		}

		this.databaseEntryUtils = table.getDatabaseEntryUtils();
		this.factoryMethod = this.databaseEntryUtils.getEntryInstanceProvider().getFactoryMethod(table, columns);
	}

	@Override
	public boolean hasMoreElements() {
		if (this.initialized) {
			return this.hasNext;
		}

		try {
			this.hasNext = this.rs.next();
			this.initialized = true;
			if (!this.hasNext) {
				this.close();
				return false;
			}
			return this.hasNext;
		} catch (final SQLException e) {
			throw new InternalDBException("Failed to advance ResultSet.", e);
		}
	}

	@Override
	public T nextElement() {
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
				copy = this.databaseEntryUtils.getEntryInstanceProvider().instance(this.table);
				this.databaseEntryUtils.fillLoad(this.table, copy, this.rs);
			}

			this.initialized = false;
			return copy;
		} catch (final SQLException e) {
			throw new InternalDBException("Failed to load ResultSet row.", e);
		}
	}

	@Override
	public void close() {
		try {
			final Statement stmt = this.rs.getStatement();
			this.rs.close();
			if (this.closeStatement) {
				stmt.close();
			}
		} catch (final SQLException e) {
			throw new InternalDBException(e);
		}
	}

}
