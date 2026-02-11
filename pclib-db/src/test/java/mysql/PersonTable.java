package mysql;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DataBaseTable;

public class PersonTable extends DataBaseTable<PersonData> {

	public PersonTable(DataBase dataBase) {
		super(dataBase);
	}

}
