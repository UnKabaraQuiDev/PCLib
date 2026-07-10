package lu.kbra.pclib.db.domain.table;

import lombok.Data;

@Data
public class StructureName {

	private final String name;
	private final String[] nameParts;
	private final String qualifiedName;

}
