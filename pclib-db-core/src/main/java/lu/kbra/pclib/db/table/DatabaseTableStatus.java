package lu.kbra.pclib.db.table;

import lu.kbra.pclib.db.impl.DatabaseEntry;

public final class DatabaseTableStatus<T extends DatabaseEntry, B extends AbstractDBTable<T>> {

	private final boolean existed;
	private final B table;

	protected DatabaseTableStatus(final boolean existed, final B table) {
		this.existed = existed;
		this.table = table;
	}

	public boolean created() {
		return !this.existed;
	}

	public boolean existed() {
		return this.existed;
	}

	public B getQueryable() {
		return this.table;
	}

	@Override
	public String toString() {
		return "DatabaseTableStatus@" + System.identityHashCode(this) + " [existed=" + this.existed + ", table=" + this.table + "]";
	}

}
