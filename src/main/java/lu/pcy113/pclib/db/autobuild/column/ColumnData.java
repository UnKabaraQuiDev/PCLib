package lu.pcy113.pclib.db.autobuild.column;

import lu.pcy113.pclib.db.autobuild.SQLBuildable;

public class ColumnData implements SQLBuildable {

	private String name;
	private ColumnType type;

	public ColumnData() {
	}

	public ColumnData(String name, ColumnType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ColumnType getType() {
		return type;
	}

	public void setType(ColumnType type) {
		this.type = type;
	}

	@Override
	public String build() {
		return name + " " + type.build();
	}

}
