package mysql;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class PersonTable extends DatabaseTable<PersonData> {

	public PersonTable(final Database database) {
		super(database);
	}

	@Override
	public void requestHook(final SQLRequestType type, final Object query) {
		System.err.println(type + " " + PCUtils.valueToString(query));
	}

}
