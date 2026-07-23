package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class QueryMethodException extends DBException {

	public QueryMethodException() {
	}

	public QueryMethodException(String message) {
		super(message);
	}

	public QueryMethodException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public QueryMethodException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public QueryMethodException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public QueryMethodException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public QueryMethodException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public QueryMethodException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public QueryMethodException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueryMethodException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public QueryMethodException(Throwable cause) {
		super(cause);
	}

}
