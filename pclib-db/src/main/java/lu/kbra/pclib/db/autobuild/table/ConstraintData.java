package lu.kbra.pclib.db.autobuild.table;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.SQLBuildable;

public abstract class ConstraintData implements SQLBuildable {

	public static final int NAME_MAX_LENGTH = 64;
	
	public abstract String getName();

	public String getEscapedName() {
		return PCUtils.sqlEscapeIdentifier(getName());
	}

}
