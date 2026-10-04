package lu.kbra.pclib.db.migration.compare;

import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class SchemaDelta {

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
