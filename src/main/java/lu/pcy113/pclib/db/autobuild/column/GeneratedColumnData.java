package lu.pcy113.pclib.db.autobuild.column;

import lu.pcy113.pclib.db.autobuild.column.Generated.Type;

public class GeneratedColumnData extends ColumnData {

	protected Type storageType;

	public GeneratedColumnData(ColumnData columnData, Generated gen) {
		super(columnData);

		this.storageType = gen.value();
	}

	public void setStorageType(Type storageType) {
		this.storageType = storageType;
	}

	public Type getStorageType() {
		return storageType;
	}

	@Override
	public String build() {
		return getEscapedName() + " " + type.build() + " GENERATED ALWAYS AS (" + super.defaultValue + ") " + storageType.name() + (nullable ? "" : " NOT NULL");
	}

}
