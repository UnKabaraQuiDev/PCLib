package lu.kbra.pclib.db.exception;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.utils.impl.SQLColumnTypeProvider;

import lombok.Getter;

@Getter
public class DBException extends RuntimeException {

	private static final long serialVersionUID = 2298946527564581019L;

	public static final String INCLUDE_STRUCTURE_IN_EXCEPTION_PROPERTY = DBException.class.getSimpleName()
			+ ".include_structure_in_exception";
	public static boolean INCLUDE_STRUCTURE_IN_EXCEPTION = PCUtils.getBoolean(DBException.INCLUDE_STRUCTURE_IN_EXCEPTION_PROPERTY, true);

	public static final String INCLUDE_SQL_IN_EXCEPTION_PROPERTY = DBException.class.getSimpleName() + ".include_sql_in_exception";
	public static boolean INCLUDE_SQL_IN_EXCEPTION = PCUtils.getBoolean(DBException.INCLUDE_SQL_IN_EXCEPTION_PROPERTY, true);

	public static final String INCLUDE_QUERY_IN_EXCEPTION_PROPERTY = DBException.class.getSimpleName() + ".include_query_in_exception";
	public static boolean INCLUDE_QUERY_IN_EXCEPTION = PCUtils.getBoolean(DBException.INCLUDE_QUERY_IN_EXCEPTION_PROPERTY, true);

	public static final String INCLUDE_TYPE_HINTS_IN_EXCEPTION_PROPERTY = SQLColumnTypeProvider.class.getSimpleName()
			+ ".include_type_hints_in_exception";
	public static boolean INCLUDE_TYPE_HINTS_IN_EXCEPTION = PCUtils.getBoolean(DBException.INCLUDE_TYPE_HINTS_IN_EXCEPTION_PROPERTY, true);

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

	public DBException(final AbstractDBStructure structure, final Throwable e) {
		this("", null, structure, null, e);
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

	public DBException(
			final String message,
			final String sql,
			final AbstractDBStructure structure,
			final SQLQuery<?, ?> query,
			final Throwable e) {
		super(message + (DBException.INCLUDE_SQL_IN_EXCEPTION ? "\n --- Source ---\n" + (sql == null ? "<none>" : sql) : "")
				+ "\n --- Structure ---\n" + (structure == null ? "<none>"
						: DBException.INCLUDE_STRUCTURE_IN_EXCEPTION ? structure.toTreeString()
						: structure.toString())
				+ (DBException.INCLUDE_QUERY_IN_EXCEPTION ? "\n --- Query ---\n" + (query == null ? "<none>" : query) : ""),
				DBException.sanitizeCause(structure, e));
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

	private static Throwable sanitizeCause(final AbstractDBStructure structure, final Throwable throwable) {
		if (throwable == null) {
			return null;
		}

		Throwable current = throwable;

		while (current != null) {
			if (current instanceof DBException && ((DBException) current).getStructure() == structure) {
				((DBException) current).structure = null;
			}
			current = current.getCause();
		}

		return throwable;
	}

}
