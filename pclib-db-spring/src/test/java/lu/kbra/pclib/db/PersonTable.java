package lu.kbra.pclib.db;

import java.util.Optional;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;

@Component
public abstract class PersonTable extends DeferredDataBaseTable<PersonData> {

	public PersonTable(final DataBase dataBase) {
		super(dataBase);
	}

	@Query(columns = { "name" })
	public abstract Optional<PersonData> byName(String name);

}
