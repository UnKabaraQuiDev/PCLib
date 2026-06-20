package lu.kbra.pclib.db.utils;

import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

public class MySQLDataBaseEntryUtils extends BaseDataBaseEntryUtils {

	public MySQLDataBaseEntryUtils() {
		super(new MySQLColumnTypeRegistry(), MySQLDbmsProvider.DBMS_QUALIFIER_NAME);
	}

}
