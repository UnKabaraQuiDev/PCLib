package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class NewInstanceException extends DBException {

	public NewInstanceException() {
	}

	public NewInstanceException(String message) {
		super(message);
	}

	public NewInstanceException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public NewInstanceException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public NewInstanceException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public NewInstanceException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public NewInstanceException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public NewInstanceException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public NewInstanceException(String message, Throwable cause) {
		super(message, cause);
	}

	public NewInstanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NewInstanceException(Throwable cause) {
		super(cause);
	}

}
