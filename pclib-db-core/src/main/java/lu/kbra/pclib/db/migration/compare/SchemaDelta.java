package lu.kbra.pclib.db.migration.compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchemaDelta {

	private final List<SchemaChange> changes;

	public SchemaDelta(final List<SchemaChange> changes) {
		this.changes = new ArrayList<>(changes);
	}

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
