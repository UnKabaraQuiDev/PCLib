package lu.kbra.pclib.db.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Limit;
import lu.kbra.pclib.db.annotations.query.Offset;
import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;

@Component
public abstract class PersonTable extends DeferredDataBaseTable<PersonData> {

	public PersonTable(@Qualifier("people") final DataBase dataBase) {
		super(dataBase);
	}

	@Query(columns = { "name" })
	public abstract Optional<PersonData> byName(String name);

	@Query
	public abstract List<PersonData> byNameLike(@Param(value = "name", comparator = "LIKE") String name);

	@Query("SELECT * FROM {NAME} WHERE {Q:name} = ?;")
	public abstract Optional<PersonData> byNameWithExplicitSql(String name);

	@Query
	public abstract Optional<PersonData> byNameWithParam(@Param("name") String name);

	@Query("SELECT COUNT(*) FROM {NAME} WHERE {Q:name} LIKE ?;")
	public abstract int countByNameLike(String name);

	@Query("SELECT {Q:id} FROM {NAME} WHERE {Q:name} = ?;")
	public abstract long idValueByName(String name);

	@Query("SELECT {Q:name} FROM {NAME} WHERE {Q:name} = ?;")
	public abstract String nameValueByName(String name);

	@Query("SELECT {Q:name} FROM {NAME} WHERE {Q:name} LIKE ? ORDER BY {Q:id} ASC;")
	public abstract List<String> nameValuesByNameLike(String name);

	@Query("SELECT {Q:name} FROM {NAME} WHERE {Q:name} = ?;")
	public abstract Optional<String> optionalNameValueByName(String name);

	@Query(orderBy = @OrderBy(column = "id", type = OrderBy.Type.DESC))
	public abstract List<PersonData>
			orderedByIdDesc(@Param(value = "name", ignoreNull = true) String name, @Limit long limit, @Offset long offset);

}
