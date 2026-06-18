package lu.kbra.pclib.db.base;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.Unique;
import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class AuditLogData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	protected long id;

	@Column
	@Unique
	protected @MaxLength(64) String event;

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
