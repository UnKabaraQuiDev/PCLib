package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.impl.DatabaseEntry;

public class DatabaseViewStatus<T extends DatabaseEntry, B extends AbstractDBView<T>> {

	private final boolean existed;
	private final B table;

	protected DatabaseViewStatus(final boolean existed, final B table) {
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
		return "DatabaseViewStatus{existed=" + this.existed + ", created=" + !this.existed + ", table=" + this.table + "}";
	}

}
