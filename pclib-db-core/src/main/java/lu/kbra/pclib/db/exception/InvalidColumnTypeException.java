package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class InvalidColumnTypeException extends DBException {

	private static final long serialVersionUID = -2512062876297124412L;

	public InvalidColumnTypeException() {
	}

	public InvalidColumnTypeException(final String message) {
		super(message);
	}

	public InvalidColumnTypeException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public InvalidColumnTypeException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public InvalidColumnTypeException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public InvalidColumnTypeException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public InvalidColumnTypeException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public InvalidColumnTypeException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public InvalidColumnTypeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InvalidColumnTypeException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidColumnTypeException(final Throwable cause) {
		super(cause);
	}

}
