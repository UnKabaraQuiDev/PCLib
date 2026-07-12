package mysql;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class PersonTable extends DataBaseTable<PersonData> {

	public PersonTable(final DataBase dataBase) {
		super(dataBase);
	}

	@Override
	public void requestHook(final SQLRequestType type, final Object query) {
		System.err.println(type + " " + PCUtils.valueToString(query));
	}

}
