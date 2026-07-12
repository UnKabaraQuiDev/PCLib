package lu.kbra.pclib.db.utils;

import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.utils.registry.SQLiteColumnTypeRegistry;

@Deprecated
public class SQLiteDatabaseEntryUtils extends BaseDatabaseEntryUtils {

	@Deprecated
	public SQLiteDatabaseEntryUtils() {
		super(new SQLiteColumnTypeRegistry(), SQLiteDbmsProvider.DBMS_QUALIFIER_NAME);
	}

}
