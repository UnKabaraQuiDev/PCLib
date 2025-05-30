package lu.pcy113.pclib.db.autobuild.column;

import java.sql.Types;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public final class TimeTypes {

	public static class DateType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "DATE";
		}

		@Override
		public int getSQLType() {
			return Types.DATE;
		}

	}

	public static class TimestampType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "TIMESTAMP";
		}

		@Override
		public int getSQLType() {
			return Types.TIMESTAMP;
		}

	}

}
