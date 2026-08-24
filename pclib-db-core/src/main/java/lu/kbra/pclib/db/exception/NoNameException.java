package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NoNameException extends DBException {

	private static final long serialVersionUID = 2336337445835258767L;

	public NoNameException() {
	}

	public NoNameException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NoNameException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NoNameException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NoNameException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public NoNameException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NoNameException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoNameException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NoNameException(final String message) {
		super(message);
	}

	public NoNameException(final Throwable cause) {
		super(cause);
	}

}
