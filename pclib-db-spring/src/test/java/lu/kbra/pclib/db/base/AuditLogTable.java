package lu.kbra.pclib.db.base;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;

@Component
public abstract class AuditLogTable extends DeferredDatabaseTable<AuditLogData> {

	public AuditLogTable(@Qualifier("auditDb") final Database database) {
		super(database);
	}

	@Query(columns = { "event" })
	public abstract Optional<AuditLogData> byEvent(String event);

}
