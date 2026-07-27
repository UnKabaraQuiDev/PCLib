package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class ReadOnlyEntryException extends DBException {

	public ReadOnlyEntryException() {
	}

	public ReadOnlyEntryException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public ReadOnlyEntryException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public ReadOnlyEntryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ReadOnlyEntryException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReadOnlyEntryException(String message) {
		super(message);
	}

	public ReadOnlyEntryException(Throwable cause) {
		super(cause);
	}

	public ReadOnlyEntryException(AbstractDBStructure structure) {
		super(structure);
	}

	public ReadOnlyEntryException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public ReadOnlyEntryException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public ReadOnlyEntryException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public ReadOnlyEntryException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

}
