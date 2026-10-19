package lu.kbra.pclib.db.migration.compare;

import lombok.Data;

@Data
public class TableNameChanged implements SchemaChange {

	private final String[] oldName;
	private final String[] newName;

}
