package lu.kbra.pclib.db.exception;

import lombok.Getter;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;

@Getter
public class DBException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 2298946527564581019L;
	public static final String INCLUDE_STRUCTURE_IN_EXCEPTION_PROPERTY = DBException.class.getSimpleName()
			+ ".include_structure_in_exception";
	public static boolean INCLUDE_STRUCTURE_IN_EXCEPTION = PCUtils.getBoolean(DBException.INCLUDE_STRUCTURE_IN_EXCEPTION_PROPERTY, true);

	public static final String INCLUDE_SQL_IN_EXCEPTION_PROPERTY = DBException.class.getSimpleName() + ".include_sql_in_exception";
	public static boolean INCLUDE_SQL_IN_EXCEPTION = PCUtils.getBoolean(DBException.INCLUDE_SQL_IN_EXCEPTION_PROPERTY, true);

	public static final String INCLUDE_QUERY_IN_EXCEPTION_PROPERTY = DBException.class.getSimpleName() + ".include_query_in_exception";
	public static boolean INCLUDE_QUERY_IN_EXCEPTION = PCUtils.getBoolean(DBException.INCLUDE_QUERY_IN_EXCEPTION_PROPERTY, true);

	private String customMessage;
	private String sql;
	private AbstractDBStructure structure;
	private SQLQuery<?, ?> query;

	public DBException() {
	}

	public DBException(final String message) {
		super(message);
		this.customMessage = message;
	}

	public DBException(final String sql, final AbstractDBStructure structure) {
		this("", sql, structure, null, null);
	}

	public DBException(final String message, final String sql, final AbstractDBStructure structure) {
		this(message, sql, structure, null, null);
	}

	public DBException(final String message, final String sql, final AbstractDBStructure structure, final Throwable e) {
		this(message, sql, structure, null, e);
	}

	public DBException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query) {
		this(message, sql, structure, query, null);
	}

	public DBException(final String message, final String sql, final AbstractDBStructure structure, final SQLQuery<?, ?> query, final Throwable e) {
		super(message + (DBException.INCLUDE_SQL_IN_EXCEPTION ? "\n --- Source ---\n" + (sql == null ? "<none>" : sql) : "")
				+ (DBException.INCLUDE_STRUCTURE_IN_EXCEPTION
						? "\n --- Structure ---\n" + (structure == null ? "<none>" : structure.toTreeString())
						: "")
				+ (DBException.INCLUDE_QUERY_IN_EXCEPTION ? "\n --- Query ---\n" + (query == null ? "<none>" : query) : ""), e);
		this.customMessage = message;
		this.sql = sql;
		this.structure = structure;
		this.query = query;
	}

	public DBException(final String message, final Throwable cause) {
		super(message, cause);
		this.customMessage = message;
	}

	public DBException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.customMessage = message;
	}

	public DBException(final Throwable cause) {
		super(cause);
	}

}
