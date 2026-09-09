package lu.kbra.pclib.db.view;

import lombok.ToString;

@ToString
public class DatabaseViewStatus {

	private final boolean existed;

	protected DatabaseViewStatus(final boolean existed) {
		this.existed = existed;
	}

	public boolean created() {
		return !this.existed;
	}

	public boolean existed() {
		return this.existed;
	}

}
