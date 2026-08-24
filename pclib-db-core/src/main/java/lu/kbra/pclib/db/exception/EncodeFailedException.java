package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class EncodeFailedException extends DBException {

	private static final long serialVersionUID = 6676456162397373069L;

	public EncodeFailedException() {
	}

	public EncodeFailedException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public EncodeFailedException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public EncodeFailedException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public EncodeFailedException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public EncodeFailedException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public EncodeFailedException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EncodeFailedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public EncodeFailedException(final String message) {
		super(message);
	}

	public EncodeFailedException(final Throwable cause) {
		super(cause);
	}

}
