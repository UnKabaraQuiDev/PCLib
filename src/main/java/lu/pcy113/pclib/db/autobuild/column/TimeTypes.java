package lu.pcy113.pclib.db.autobuild.column;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public final class TimeTypes {

	public static class DateType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "DATE";
		}

	}

	public static class TimestampType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "TIMESTAMP";
		}

	}

}
