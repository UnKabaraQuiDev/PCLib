package lu.kbra.pclib.db.base;

public class DatabaseStatus {

	private final boolean existed;
	private final Database database;

	protected DatabaseStatus(final boolean existed, final Database database) {
		this.existed = existed;
		this.database = database;
	}

	public boolean created() {
		return !this.existed;
	}

	public boolean existed() {
		return this.existed;
	}

	public Database getDatabase() {
		return this.database;
	}

	@Override
	public String toString() {
		return "DatabaseStatus{existed=" + this.existed + ", created=" + !this.existed + ", db=" + this.database + "}";
	}

}
