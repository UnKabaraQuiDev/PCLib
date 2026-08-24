package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class HintScanException extends DBException {

	private static final long serialVersionUID = 8204598950019098377L;

	public HintScanException() {
	}

	public HintScanException(final String message) {
		super(message);
	}

	public HintScanException(final String sql, final AbstractDBStructure structure) {
		super(sql, structure);
	}

	public HintScanException(final AbstractDBStructure structure, final Throwable e) {
		super(structure, e);
	}

	public HintScanException(final String message, final String sql, final AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public HintScanException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		super(message, sql, structure, e);
	}

	public HintScanException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public HintScanException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message, sql, structure, query, e);
	}

	public HintScanException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public HintScanException(
			final String message,
			final Throwable cause,
			final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HintScanException(final Throwable cause) {
		super(cause);
	}

}
