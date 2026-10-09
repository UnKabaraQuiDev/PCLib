package shared.migration.schema.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.table.DatabaseTable;

public class AxaTableN<T extends DatabaseEntry> extends DatabaseTable<T> {

	public AxaTableN(Database database, Class<T> entryClass) {
		super(database, "axa", "axa-table");
		super.customHints.put(DefaultQueryableHints.ENTRY_CLASS, entryClass);
	}

}
