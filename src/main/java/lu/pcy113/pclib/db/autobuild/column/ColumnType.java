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

	@FunctionalInterface
	public interface FixedColumnType extends ColumnType {

		default boolean isVariable() {
			return false;
		}

		default Object variableValue() {
			return null;
		}

	}

}
