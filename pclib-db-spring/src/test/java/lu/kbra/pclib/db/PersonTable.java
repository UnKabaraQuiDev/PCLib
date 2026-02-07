package lu.kbra.pclib.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;

@Component
public abstract class PersonTable extends DeferredDataBaseTable<PersonData> {

	public PersonTable(@Qualifier("dataBase2") DataBase dataBase) {
		super(dataBase);
	}

}
