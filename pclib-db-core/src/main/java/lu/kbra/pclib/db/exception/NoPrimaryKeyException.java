package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoPrimaryKeyException extends DBException {

	private static final long serialVersionUID = -2286219501069357216L;

	public NoPrimaryKeyException() {
	}

	public NoPrimaryKeyException(final String message) {
		super(message);
	}

	public NoPrimaryKeyException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoPrimaryKeyException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public NoPrimaryKeyException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoPrimaryKeyException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public NoPrimaryKeyException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoPrimaryKeyException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoPrimaryKeyException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoPrimaryKeyException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoPrimaryKeyException(final Throwable cause) {
		super(cause);
	}

}
