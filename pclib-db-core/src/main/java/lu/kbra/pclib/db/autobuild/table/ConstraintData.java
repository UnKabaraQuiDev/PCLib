package lu.kbra.pclib.db.autobuild.table;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.SQLBuildable;

import lombok.Data;

@Data
public abstract class ConstraintData implements SQLBuildable {

	public static final int NAME_MAX_LENGTH = 64;

	public String getEscapedName() {
		return PCUtils.sqlEscapeIdentifier(this.getName());
	}

	public abstract String getName();

}
