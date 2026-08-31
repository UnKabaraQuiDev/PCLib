package lu.kbra.pclib.db.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.annotations.view.Table.Type;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;

@Component
public abstract class CarTable extends DeferredDatabaseTable<CarData> {

	public CarTable(@Qualifier("auditDb") final DeferredDatabase database) {
		super(database);
	}

	@Query(tables = { @Table(typeName = AuditLogTable.class, join = Type.INNER) })
	public abstract List<CarData> byOwnerId(@Param(member = "AuditLogTable:id") long id);

}
