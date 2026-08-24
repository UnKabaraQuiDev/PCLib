package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class PingFailedException extends DBException {

	private static final long serialVersionUID = 5091013162027203892L;

	public PingFailedException() {
	}

	public PingFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public PingFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public PingFailedException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public PingFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public PingFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public PingFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PingFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public PingFailedException(final String message) {
		super(message);
	}

	public PingFailedException(final Throwable cause) {
		super(cause);
	}

}
