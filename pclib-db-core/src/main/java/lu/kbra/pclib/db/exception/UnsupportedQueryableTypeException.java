package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class UnsupportedQueryableTypeException extends DBException {

	private static final long serialVersionUID = -609579767168773138L;

	public UnsupportedQueryableTypeException() {
	}

	public UnsupportedQueryableTypeException(final String message) {
		super(message);
	}

	public UnsupportedQueryableTypeException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public UnsupportedQueryableTypeException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public UnsupportedQueryableTypeException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final Throwable e) {
		super(message, sql, structure, e);
	}

	public UnsupportedQueryableTypeException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public UnsupportedQueryableTypeException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public UnsupportedQueryableTypeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UnsupportedQueryableTypeException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnsupportedQueryableTypeException(final Throwable cause) {
		super(cause);
	}

}
