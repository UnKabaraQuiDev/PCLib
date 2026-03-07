package mysql;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DataBaseTable;

public class CarTable extends DataBaseTable<CarData> {

	public CarTable(DataBase dataBase) {
		super(dataBase);
	}

}