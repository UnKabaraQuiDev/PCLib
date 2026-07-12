package mysql;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

public class CarTable extends DatabaseTable<CarData> {

	public CarTable(final Database database) {
		super(database);
	}

}
