package lu.kbra.pclib.db.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.annotations.view.Table.Type;
import lu.kbra.pclib.db.table.AbstractDBTable;

@Component
public interface AuditLogTable extends AbstractDBTable<AuditLogData> {

	void ctor(@Qualifier("auditDb") final DeferredDatabase database);

	@Query(columns = { "event" })
	Optional<AuditLogData> byEvent(String event);

	@Query(tables = { @Table(typeName = CarTable.class, join = Type.INNER) })
	List<CarData> carsByOwnerId(@Param(member = "CarTable:personId") long id);

	@Query(tables = { @Table(typeName = CarTable.class, join = Type.INNER) })
	List<CarData> carsByOwnerId(@Param(member = "CarTable:personId") AuditLogData id);

}
