package sqlite;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

public class CityTable extends DatabaseTable<CityData> {

	public CityTable(final Database database) {
		super(database);
	}
}
