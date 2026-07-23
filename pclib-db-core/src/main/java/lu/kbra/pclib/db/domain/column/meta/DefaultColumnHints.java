package lu.kbra.pclib.db.domain.column.meta;

public class DefaultColumnHints {

	public static final String AUTO_INCREMENT = "AUTO_INCREMENT";

	public static final String NULLABLE = "NULLABLE";

	public static final String PRIMARY_KEY = "PRIMARY_KEY";

	public static final String VERSION = "VERSION";

	public static final String UNIQUE = "UNIQUE";
	public static final String UNIQUE_INDEX = "UNIQUE_INDEX";

	public static final String FOREIGN_KEY_COLUMN = "FOREIGN_KEY_COLUMN";
	public static final String FOREIGN_KEY_GROUP_ID = "FOREIGN_KEY_GROUP_ID";
	public static final String FOREIGN_KEY_ON_DELETE = "FOREIGN_KEY_ON_DELETE";
	public static final String FOREIGN_KEY_ON_UPDATE = "FOREIGN_KEY_ON_UPDATE";
	public static final String FOREIGN_KEY_TABLE = "FOREIGN_KEY_TABLE";
	public static final String FOREIGN_KEY_TABLE_NAME = "FOREIGN_KEY_TABLE_NAME";

	public static final String DEFAULT_VALUE = "DEFAULT_VALUE";

	public static final String GENERATED_VALUE = "GENERATED_VALUE";
	public static final String GENERATED_STORAGE_TYPE = "GENERATED_STORAGE_TYPE";

	public static final String CHECK = "CHECK_";
	public static final String CHECK_VALUE = "CHECK_VALUE";
	public static final String CHECK_NAME = "CHECK_NAME";

	public static final String ON_UPDATE = "ON_UPDATE";

	public static final String UPDATE_EXPR = "UPDATE_EXPRESSION";
	public static final String UPDATE_EXPR_VALUE = "UPDATE_EXPRESSION_VALUE";

	protected DefaultColumnHints() {
	}

}
