package lu.kbra.pclib.db.utils;

import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.utils.registry.SQLiteColumnTypeRegistry;

@Deprecated
public class SQLiteDataBaseEntryUtils extends BaseDataBaseEntryUtils {

	@Deprecated
	public SQLiteDataBaseEntryUtils() {
		super(new SQLiteColumnTypeRegistry(), SQLiteDbmsProvider.DBMS_QUALIFIER_NAME);
	}

}
