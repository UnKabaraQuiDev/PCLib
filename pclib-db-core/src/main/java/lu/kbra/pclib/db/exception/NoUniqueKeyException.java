package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoUniqueKeyException extends DBException {

	private static final long serialVersionUID = -123968843841068312L;

	public NoUniqueKeyException() {
	}

	public NoUniqueKeyException(final String message) {
		super(message);
	}

	public NoUniqueKeyException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoUniqueKeyException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public NoUniqueKeyException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoUniqueKeyException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public NoUniqueKeyException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoUniqueKeyException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoUniqueKeyException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoUniqueKeyException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoUniqueKeyException(final Throwable cause) {
		super(cause);
	}

}
