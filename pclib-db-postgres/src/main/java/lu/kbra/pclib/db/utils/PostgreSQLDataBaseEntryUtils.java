package lu.kbra.pclib.db.utils;

import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.utils.registry.PostgreSQLColumnTypeRegistry;

public class PostgreSQLDataBaseEntryUtils extends BaseDataBaseEntryUtils {

	public PostgreSQLDataBaseEntryUtils() {
		super(new PostgreSQLColumnTypeRegistry(), PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME);
	}

}
