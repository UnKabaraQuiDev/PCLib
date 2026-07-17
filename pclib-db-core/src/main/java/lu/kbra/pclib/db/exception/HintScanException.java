package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class HintScanException extends DBException {

	public HintScanException() {
	}

	public HintScanException(String message) {
		super(message);
	}

	public HintScanException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public HintScanException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public HintScanException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public HintScanException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public HintScanException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public HintScanException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public HintScanException(String message, Throwable cause) {
		super(message, cause);
	}

	public HintScanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HintScanException(Throwable cause) {
		super(cause);
	}

}
