package lu.kbra.pclib.db.autobuild.table;

import lombok.Data;

@Data
public abstract class ConstraintData {

	public static final int NAME_MAX_LENGTH = 64;

	public abstract String getName();

}
