package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NewInstanceException extends DBException {

	private static final long serialVersionUID = 8437579436832194600L;

	public NewInstanceException() {
	}

	public NewInstanceException(final String message) {
		super(message);
	}

	public NewInstanceException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NewInstanceException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public NewInstanceException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NewInstanceException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public NewInstanceException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NewInstanceException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NewInstanceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NewInstanceException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NewInstanceException(final Throwable cause) {
		super(cause);
	}

}
