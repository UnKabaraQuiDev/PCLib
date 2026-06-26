package lu.kbra.pclib.db.utils;

import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

@Deprecated
public class MySQLDataBaseEntryUtils extends BaseDataBaseEntryUtils {

	@Deprecated
	public MySQLDataBaseEntryUtils() {
		super(new MySQLColumnTypeRegistry(), MySQLDbmsProvider.DBMS_QUALIFIER_NAME);
	}

}
