package lu.kbra.pclib.db.migration.compare;

import java.util.Collections;
import java.util.List;

import lu.kbra.pclib.db.domain.table.DatabaseStructure;

import lombok.Data;

@Data
public class SchemaDelta {

	private final DatabaseStructure oldStructure;
	private final DatabaseStructure newStructure;
	private final List<SchemaChange> changes;

	public List<SchemaChange> getChanges() {
		return Collections.unmodifiableList(this.changes);
	}

	public boolean isEmpty() {
		return this.changes.isEmpty();
	}

	public boolean hasChanges() {
		return !this.changes.isEmpty();
	}

}
