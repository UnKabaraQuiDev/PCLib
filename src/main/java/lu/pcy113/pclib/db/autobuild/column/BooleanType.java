package lu.pcy113.pclib.db.autobuild.column;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public class BooleanType implements FixedColumnType {

	@Override
	public String getTypeName() {
		return "BOOLEAN";
	}

}
