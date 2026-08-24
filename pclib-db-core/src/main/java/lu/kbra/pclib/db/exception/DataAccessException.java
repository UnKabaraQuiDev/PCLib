package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class DataAccessException extends DBException {

	private static final long serialVersionUID = -3076283661803464714L;

	public DataAccessException() {
	}

	public DataAccessException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public DataAccessException(final AbstractDBStructure structure) {
		super(structure);
	}

	public DataAccessException(final String message) {
		super(message);
	}

	public DataAccessException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public DataAccessException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public DataAccessException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public DataAccessException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public DataAccessException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public DataAccessException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DataAccessException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataAccessException(final Throwable cause) {
		super(cause);
	}

}
