package lu.kbra.pclib.db.base;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.annotations.view.Table.Type;
import lu.kbra.pclib.db.table.ProxyDatabaseTable;

@Component
public interface CarTable extends ProxyDatabaseTable<CarData> {

	@Override
	void ctor(@Qualifier("auditDb") final DeferredDatabase database);

	@Query(tables = { @Table(typeName = AuditLogTable.class, join = Type.INNER) })
	List<CarData> byOwnerId(@Param(member = "AuditLogTable:id") long id);

	@Query(tables = { @Table(typeName = AuditLogTable.class, join = Type.INNER) })
	List<CarData> byOwnerId(@Param AuditLogData id);

}
