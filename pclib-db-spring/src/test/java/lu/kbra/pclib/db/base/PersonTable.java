package lu.kbra.pclib.db.base;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.annotations.query.Cached;
import lu.kbra.pclib.db.annotations.query.Limit;
import lu.kbra.pclib.db.annotations.query.Offset;
import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public abstract class PersonTable extends DeferredDatabaseTable<PersonData> {

	public PersonTable(@Qualifier("people") final DeferredDatabase database) {
		super(database);
	}

	@Query(columns = { "name" })
	public abstract Optional<PersonData> byName(@Param String name);

	@Query
	public abstract Optional<PersonData> byAnyName(@Param String[] name);

	@Query
	public abstract Optional<PersonData> byAnyNameList(@Param List<String> name);

	@Query
	public abstract List<PersonData> byNameLike(@Param(value = "name", comparator = "LIKE") String name);

	@Query("SELECT * FROM {NAME} WHERE {Q:name} = ?;")
	public abstract Optional<PersonData> byNameWithExplicitSql(@Param String name);

	@Query("SELECT * FROM {NAME} WHERE {Q:name} = {V:name};")
	public abstract Optional<PersonData> byNameWithHalfExplicitSql(String name);

	@Query(condition = "{Q:name} = {V:name}")
	public abstract Optional<PersonData> byNameWithHalfExplicitCondition(String name);

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

	@Query
	public abstract Stream<PersonData> allStream();

	@Query
	public abstract Mono<PersonData> firstMono();

	@Query
	public abstract Flux<PersonData> allFlux();

	@Query
	public abstract NextTask<?, ?, PersonData> firstNextTask();

	@Query
	public abstract Iterator<PersonData> allIterator();

	@Query
	public abstract Enumeration<PersonData> allEnumeration();

	@Query
	public abstract @Cached Stream<PersonData> allStreamCached();

	@Query
	public abstract @Cached Flux<PersonData> allFluxCached();

	@Query
	public abstract @Cached Iterator<PersonData> allIteratorCached();

	@Query
	public abstract @Cached Enumeration<PersonData> allEnumerationCached();

	@Query(orderBy = @OrderBy(value = "id", type = OrderBy.Type.DESC))
	public abstract List<PersonData>
			orderedByIdDesc(@Param(value = "name", ignoreNull = true) String name, @Limit long limit, @Offset long offset);

}
