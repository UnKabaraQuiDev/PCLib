package lu.kbra.pclib.db;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DeferredNTDataBaseTable;

@Component
public abstract class NTUserTable extends DeferredNTDataBaseTable<UserData> {

	public NTUserTable(@Qualifier("dataBase") DataBase dataBase) {
		super(dataBase);
	}

}
