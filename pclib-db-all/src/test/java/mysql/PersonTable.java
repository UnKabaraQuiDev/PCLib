package mysql;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class PersonTable extends DataBaseTable<PersonData> {

	public PersonTable(final DataBase dataBase) {
		super(dataBase);
	}

	@Override
	public void requestHook(SQLRequestType type, Object query) {
		System.err.println(type + " " + query);
	}

}
