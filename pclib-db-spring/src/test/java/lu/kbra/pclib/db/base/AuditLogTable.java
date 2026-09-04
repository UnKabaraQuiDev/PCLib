package lu.kbra.pclib.db.base;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.annotations.view.Table.Type;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;

@Component
public abstract class AuditLogTable extends DeferredDatabaseTable<AuditLogData> {

	public AuditLogTable(@Qualifier("auditDb") final DeferredDatabase database) {
		super(database);
	}

	@Query(columns = { "event" })
	public abstract Optional<AuditLogData> byEvent(String event);

	@Query(tables = { @Table(typeName = CarTable.class, join = Type.INNER) })
	public abstract List<CarData> carsByOwnerId(@Param(member = "CarTable:personId") long id);

	@Query(tables = { @Table(typeName = CarTable.class, join = Type.INNER) })
	public abstract List<CarData> carsByOwnerId(@Param(member = "CarTable:personId") AuditLogData id);

}
