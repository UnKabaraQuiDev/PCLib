package lu.kbra.pclib.db;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.Unique;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class AuditLogData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	protected long id;

	@Column(length = 64)
	@Unique
	protected String event;

	public AuditLogData() {
	}

	public AuditLogData(final String event) {
		this.event = event;
	}

	@Override
	public String toString() {
		return "AuditLogData@" + System.identityHashCode(this) + " [id=" + this.id + ", event=" + this.event + "]";
	}

}
