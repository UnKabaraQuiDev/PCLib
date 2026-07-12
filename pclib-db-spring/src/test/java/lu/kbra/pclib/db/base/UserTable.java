package lu.kbra.pclib.db.base;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;

@Component
public abstract class UserTable extends DeferredDatabaseTable<UserData> {

	public UserTable(@Qualifier("people") final Database database) {
		super(database);
	}

	@Query(columns = { "name" })
	public abstract Optional<UserData> byName(String name);

}
