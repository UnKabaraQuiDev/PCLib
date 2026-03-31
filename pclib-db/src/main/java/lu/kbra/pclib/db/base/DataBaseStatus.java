package lu.kbra.pclib.db.base;

public class DataBaseStatus {

	private final boolean existed;
	private final DataBase database;

	protected DataBaseStatus(final boolean existed, final DataBase database) {
		this.existed = existed;
		this.database = database;
	}

	public boolean existed() {
		return this.existed;
	}

	public boolean created() {
		return !this.existed;
	}

	public DataBase getDatabase() {
		return this.database;
	}

	@Override
	public String toString() {
		return "DataBaseStatus{existed=" + this.existed + ", created=" + !this.existed + ", db=" + this.database + "}";
	}

}
