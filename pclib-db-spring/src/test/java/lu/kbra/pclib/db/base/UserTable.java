package lu.kbra.pclib.db.base;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;

@Component
public abstract class UserTable extends DeferredDataBaseTable<UserData> {

	public UserTable(@Qualifier("peopleDb") final DataBase dataBase) {
		super(dataBase);
	}

	@Query(columns = { "name" })
	public abstract Optional<UserData> byName(String name);

}
