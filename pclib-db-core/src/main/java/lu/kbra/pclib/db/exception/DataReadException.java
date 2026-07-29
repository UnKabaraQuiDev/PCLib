package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class DataReadException extends DBException {

	private static final long serialVersionUID = 1581776640859470815L;

	public DataReadException() {
	}

	public DataReadException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public DataReadException(final AbstractDBStructure structure) {
		super(structure);
	}

	public DataReadException(final String message) {
		super(message);
	}

	public DataReadException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public DataReadException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public DataReadException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public DataReadException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public DataReadException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public DataReadException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DataReadException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataReadException(final Throwable cause) {
		super(cause);
	}

}
