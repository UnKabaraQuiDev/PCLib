package lu.kbra.pclib.db.table;

import lu.kbra.pclib.db.impl.DataBaseEntry;

public final class DataBaseTableStatus<T extends DataBaseEntry, B extends AbstractDBTable<T>> {

	private final boolean existed;
	private final B table;

	protected DataBaseTableStatus(final boolean existed, final B table) {
		this.existed = existed;
		this.table = table;
	}

	public boolean existed() {
		return this.existed;
	}

	public boolean created() {
		return !this.existed;
	}

	public B getQueryable() {
		return this.table;
	}

	@Override
	public String toString() {
		return "DataBaseTableStatus@" + System.identityHashCode(this) + " [existed=" + this.existed + ", table=" + this.table + "]";
	}

}