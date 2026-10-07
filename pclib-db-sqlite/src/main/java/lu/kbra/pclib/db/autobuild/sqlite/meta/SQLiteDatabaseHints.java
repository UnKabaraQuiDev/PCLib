package lu.kbra.pclib.db.autobuild.sqlite.meta;

public final class SQLiteDatabaseHints {

	public static final String FOREIGN_KEYS = "FOREIGN_KEYS";
	public static final String JOURNAL_MODE = "JOURNAL_MODE";
	public static final String SYNCHRONOUS = "SYNCHRONOUS";
	public static final String BUSY_TIMEOUT = "BUSY_TIMEOUT";
	public static final String CACHE_SIZE = "CACHE_SIZE";
	public static final String TEMP_STORE = "TEMP_STORE";
	public static final String AUTO_VACUUM = "AUTO_VACUUM";
	public static final String SECURE_DELETE = "SECURE_DELETE";
	public static final String RECURSIVE_TRIGGERS = "RECURSIVE_TRIGGERS";
	@Deprecated
	public static final String CASE_SENSITIVE_LIKE = "CASE_SENSITIVE_LIKE";
	public static final String USER_VERSION = "USER_VERSION";
	public static final String APPLICATION_ID = "APPLICATION_ID";

	private SQLiteDatabaseHints() {
	}
}
