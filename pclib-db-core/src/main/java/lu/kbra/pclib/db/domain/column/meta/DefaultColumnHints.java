package lu.kbra.pclib.db.domain.column.meta;

public class DefaultColumnHints {

	public static final String AUTO_INCREMENT = "CO_AUTO_INCREMENT";

	public static final String NULLABLE = "CO_NULLABLE";

	public static final String PRIMARY_KEY = "CO_PRIMARY_KEY";

	public static final String VERSION = "CO_VERSION";

	public static final String UNIQUE = "CO_UNIQUE";
	public static final String UNIQUE_INDEX = "CO_UNIQUE_INDEX";
	public static final String UNIQUE_NAME = "CO_UNIQUE_NAME";

	public static final String INDEX = "CO_INDEX";
	public static final String INDEX_INDEX = "CO_INDEX_INDEX";
	public static final String INDEX_NAME = "CO_INDEX_NAME";

	public static final String FOREIGN_KEY_COLUMN = "CO_FOREIGN_KEY_COLUMN";
	public static final String FOREIGN_KEY_GROUP_ID = "CO_FOREIGN_KEY_GROUP_ID";
	public static final String FOREIGN_KEY_ON_DELETE = "CO_FOREIGN_KEY_ON_DELETE";
	public static final String FOREIGN_KEY_ON_UPDATE = "CO_FOREIGN_KEY_ON_UPDATE";
	public static final String FOREIGN_KEY_TABLE = "CO_FOREIGN_KEY_TABLE";
	public static final String FOREIGN_KEY_TABLE_NAME = "CO_FOREIGN_KEY_TABLE_NAME";
	public static final String FOREIGN_DEFER_MODE = "CO_FOREIGN_DEFER_MODE";
	public static final String FOREIGN_KEY_NAME = "CO_FOREIGN_KEY_NAME";

	public static final String DEFAULT_VALUE = "CO_DEFAULT_VALUE";

	public static final String GENERATED_VALUE = "CO_GENERATED_VALUE";
	public static final String GENERATED_STORAGE_TYPE = "CO_GENERATED_STORAGE_TYPE";

	public static final String CHECK = "CO_CHECK_";
	public static final String CHECK_VALUE = "CO_CHECK_VALUE";
	public static final String CHECK_NAME = "CO_CHECK_NAME";

	public static final String ON_UPDATE = "CO_ON_UPDATE";

	public static final String UPDATE_EXPR = "CO_UPDATE_EXPRESSION";
	public static final String UPDATE_EXPR_VALUE = "CO_UPDATE_EXPRESSION_VALUE";

	protected DefaultColumnHints() {
	}

}
