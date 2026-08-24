package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class DataStoreException extends DBException {

	private static final long serialVersionUID = 7084235216162177089L;

	public DataStoreException() {
	}

	public DataStoreException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public DataStoreException(final AbstractDBStructure structure) {
		super(structure);
	}

	public DataStoreException(final String message) {
		super(message);
	}

	public DataStoreException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public DataStoreException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public DataStoreException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public DataStoreException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public DataStoreException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public DataStoreException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DataStoreException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataStoreException(final Throwable cause) {
		super(cause);
	}

}
