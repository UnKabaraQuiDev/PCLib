package lu.kbra.pclib.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.autobuild.query.Limit;
import lu.kbra.pclib.db.autobuild.query.Offset;
import lu.kbra.pclib.db.autobuild.query.Param;
import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;

@Component
public abstract class PersonTable extends DeferredDataBaseTable<PersonData> {

	public PersonTable(@Qualifier("peopleDb") final DataBase dataBase) {
		super(dataBase);
	}

	@Query(columns = { "name" })
	public abstract Optional<PersonData> byName(String name);

	@Query("SELECT * FROM {NAME} WHERE `name` = ?;")
	public abstract Optional<PersonData> byNameWithExplicitSql(String name);

	@Query
	public abstract Optional<PersonData> byNameWithParam(@Param("name") String name);

	@Query
	public abstract List<PersonData> byNameLike(@Param(value = "name", comparator = "LIKE") String name);

	@Query(orderBy = @OrderBy(column = "id", type = OrderBy.Type.DESC))
	public abstract List<PersonData> orderedByIdDesc(
			@Param(value = "name", ignoreNull = true) String name,
			@Limit long limit,
			@Offset long offset);

}
