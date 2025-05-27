package lu.pcy113.pclib.db.autobuild.column;

import lu.pcy113.pclib.db.autobuild.SQLBuildable;

public interface ColumnType extends SQLBuildable {

	String getTypeName();

	boolean isVariable();

	Object variableValue();

	@Override
	default String build() {
		return getTypeName() + (isVariable() ? "(" + variableValue() + ")" : "");
	}

	public abstract class FixedColumnType implements ColumnType {
		
		@Override
		public boolean isVariable() {
			return false;
		}

		@Override
		public Object variableValue() {
			return null;
		}
		
	}

}
