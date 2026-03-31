import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class PersonTable extends DataBaseTable<PersonData> {

	public PersonTable(
			final DataBase dataBase,
			final DataBaseEntryUtils dbEntryUtils,
			final Class<? extends AbstractDBTable<PersonData>> tableClass) {
		super(dataBase, dbEntryUtils, tableClass);
	}

	public PersonTable(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	public PersonTable(final DataBase dataBase) {
		super(dataBase);
	}

}
