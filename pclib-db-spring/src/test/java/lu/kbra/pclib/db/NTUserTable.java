package lu.kbra.pclib.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.autobuild.query.Query.Type;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DeferredNTDataBaseTable;

@Component
public abstract class NTUserTable extends DeferredNTDataBaseTable<UserData> {

	public NTUserTable(@Qualifier("dataBase") DataBase dataBase) {
		super(dataBase);
	}

	@Query(columns = { "name" })
	public abstract Optional<UserData> byName(String name);

	@Query(columns = { "name", "pass" })
	public abstract Optional<UserData> byNameAndPass(String name, String pass);

	@Query(columns = { "name" }, strategy = Type.SINGLE_NULL)
	public abstract NextTask<List<Object>, ?, UserData> ntByName();

}
