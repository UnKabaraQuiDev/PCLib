package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

public class QueryBuildingException extends DBException {

	public QueryBuildingException() {
	}

	public QueryBuildingException(AbstractDBStructure structure, Throwable e) {
		super(structure, e);
	}

	public QueryBuildingException(AbstractDBStructure structure) {
		super(structure);
	}

	public QueryBuildingException(String sql, AbstractDBStructure structure) {
		super(sql, structure);
	}

	public QueryBuildingException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query, Throwable e) {
		super(message, sql, structure, query, e);
	}

	public QueryBuildingException(String message, String sql, AbstractDBStructure structure, SQLQuery<?, ?> query) {
		super(message, sql, structure, query);
	}

	public QueryBuildingException(String message, String sql, AbstractDBStructure structure, Throwable e) {
		super(message, sql, structure, e);
	}

	public QueryBuildingException(String message, String sql, AbstractDBStructure structure) {
		super(message, sql, structure);
	}

	public QueryBuildingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public QueryBuildingException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueryBuildingException(String message) {
		super(message);
	}

	public QueryBuildingException(Throwable cause) {
		super(cause);
	}

}
