package lu.kbra.pclib.db.utils;

import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.utils.registry.SQLiteColumnTypeRegistry;

public class SQLiteDataBaseEntryUtils extends BaseDataBaseEntryUtils {

	public SQLiteDataBaseEntryUtils() {
		super(new SQLiteColumnTypeRegistry(), SQLiteDbmsProvider.DBMS_QUALIFIER_NAME);
	}

}
