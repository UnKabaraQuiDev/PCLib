package lu.pcy113.pclib.db.autobuild.column;

import java.sql.Types;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public class BooleanType implements FixedColumnType {

	@Override
	public String getTypeName() {
		return "BOOLEAN";
	}

	@Override
	public int getSQLType() {
		return Types.BOOLEAN;
	}

}
