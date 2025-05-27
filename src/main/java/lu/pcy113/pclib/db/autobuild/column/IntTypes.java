package lu.pcy113.pclib.db.autobuild.column;

import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public final class IntTypes {

	public static class BitType extends FixedColumnType {

		@Override
		public String getTypeName() {
			return "BIT";
		}

	}

	public static class SmallIntType extends FixedColumnType {

		@Override
		public String getTypeName() {
			return "SMALLINT";
		}

	}

	public static class IntType extends FixedColumnType {

		@Override
		public String getTypeName() {
			return "INT";
		}

	}

	public static class BigIntType extends FixedColumnType {

		@Override
		public String getTypeName() {
			return "BIGINT";
		}

	}

}
