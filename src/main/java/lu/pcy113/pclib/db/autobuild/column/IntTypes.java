package lu.pcy113.pclib.db.autobuild.column;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public final class IntTypes {

	public static class BitType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "BIT";
		}

	}

	public static class SmallIntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "SMALLINT";
		}

	}

	public static class IntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "INT";
		}

	}

	public static class BigIntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "BIGINT";
		}

	}

}
