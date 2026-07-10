package lu.kbra.pclib.db.domain.column.meta;

public class DefaultColumnHints {

	public static final String AUTO_INCREMENT = "AUTO_INCREMENT";

	public static final String NULLABLE = "NULLABLE";

	public static final String PRIMARY_KEY = "PRIMARY_KEY";

	public static final String UNIQUE_INDEX = "UNIQUE_INDEX";

	public static final String FOREIGN_KEY_COLUMN = "PRIMARY_KEY_COLUMN";
	public static final String FOREIGN_KEY_GROUP_ID = "PRIMARY_KEY_GROUP_ID";
	public static final String FOREIGN_KEY_ON_DELETE = "PRIMARY_KEY_ON_DELETE";
	public static final String FOREIGN_KEY_ON_UPDATE = "PRIMARY_KEY_ON_UPDATE";
	public static final String FOREIGN_KEY_TABLE = "PRIMARY_KEY_TABLE";
	public static final String FOREIGN_KEY_TABLE_NAME = "PRIMARY_KEY_TABLE_NAME";

	public static final String DEFAULT_VALUE = "DEFAULT_VALUE";
	public static final String GENERATED_TYPE = "GENERATED_TYPE";

	public static final String CHECK = "CHECK_";
	public static final String CHECK_VALUE = "CHECK_VALUE";
	public static final String CHECK_NAME = "CHECK_NAME";

	public static final String ON_UPDATE = "ON_UPDATE";

	protected DefaultColumnHints() {
	}

}
