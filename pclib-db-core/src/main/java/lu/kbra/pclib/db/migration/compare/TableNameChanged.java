package lu.kbra.pclib.db.migration.compare;

public class TableNameChanged implements SchemaChange {

	private final String oldName;
	private final String newName;

	public TableNameChanged(final String oldName, final String newName) {
		this.oldName = oldName;
		this.newName = newName;
	}

	public String getOldName() {
		return this.oldName;
	}

	public String getNewName() {
		return this.newName;
	}

}
