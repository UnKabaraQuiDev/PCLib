package lu.kbra.pclib.db.table;

import lombok.ToString;

@ToString
public final class DatabaseTableStatus {

	private final boolean existed;

	protected DatabaseTableStatus(final boolean existed) {
		this.existed = existed;
	}

	public boolean created() {
		return !this.existed;
	}

	public boolean existed() {
		return this.existed;
	}

}
