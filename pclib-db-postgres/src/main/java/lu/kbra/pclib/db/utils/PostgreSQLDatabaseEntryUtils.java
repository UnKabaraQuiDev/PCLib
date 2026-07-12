package lu.kbra.pclib.db.utils;

import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.utils.registry.PostgreSQLColumnTypeRegistry;

@Deprecated
public class PostgreSQLDatabaseEntryUtils extends BaseDatabaseEntryUtils {

	@Deprecated
	public PostgreSQLDatabaseEntryUtils() {
		super(new PostgreSQLColumnTypeRegistry(), PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME);
	}

}
