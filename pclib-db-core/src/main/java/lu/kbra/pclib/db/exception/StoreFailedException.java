package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class StoreFailedException extends DBException {

	public StoreFailedException() {
	}

	public StoreFailedException(String message) {
		super(message);
	}

	public StoreFailedException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public StoreFailedException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public StoreFailedException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public StoreFailedException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public StoreFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public StoreFailedException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public StoreFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public StoreFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public StoreFailedException(Throwable cause) {
		super(cause);
	}

}
