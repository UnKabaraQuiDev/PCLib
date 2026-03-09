package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.impl.DataBaseEntry;

public class DataBaseViewStatus<T extends DataBaseEntry, B extends AbstractDBView<T>> {

	private final boolean existed;
	private final B table;

	protected DataBaseViewStatus(final boolean existed, final B table) {
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
		return "DataBaseViewStatus{existed=" + this.existed + ", created=" + !this.existed + ", table=" + this.table + "}";
	}

}