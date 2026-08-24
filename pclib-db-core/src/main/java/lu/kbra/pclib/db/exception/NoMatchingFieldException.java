package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoMatchingFieldException extends DBException {

	private static final long serialVersionUID = -5827755125703345798L;

	public NoMatchingFieldException() {
	}

	public NoMatchingFieldException(final String message) {
		super(message);
	}

	public NoMatchingFieldException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoMatchingFieldException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public NoMatchingFieldException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoMatchingFieldException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public NoMatchingFieldException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoMatchingFieldException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoMatchingFieldException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoMatchingFieldException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoMatchingFieldException(final Throwable cause) {
		super(cause);
	}

}
