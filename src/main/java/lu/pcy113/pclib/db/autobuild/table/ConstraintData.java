package lu.pcy113.pclib.db.autobuild.table;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.db.autobuild.SQLBuildable;

public abstract class ConstraintData implements SQLBuildable {

	public abstract String getName();

	public String getEscapedName() {
		return PCUtils.sqlEscapeIdentifier(getName());
	}

}
