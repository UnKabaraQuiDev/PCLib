package lu.kbra.pclib.db.autobuild.column;

import lu.kbra.pclib.db.autobuild.column.Generated.Type;

public class GeneratedColumnData extends ColumnData {

	protected Type storageType;

	public GeneratedColumnData(final ColumnData columnData, final Generated gen) {
		super(columnData);

		this.storageType = gen.value();
	}

	public Type getStorageType() {
		return this.storageType;
	}

	@Override
	public boolean isGenerated() {
		return true;
	}

	public void setStorageType(final Type storageType) {
		this.storageType = storageType;
	}

	@Override
	public String toString() {
		return "GeneratedColumnData@" + System.identityHashCode(this) + " [storageType=" + this.storageType + ", name=" + this.name
				+ ", type=" + this.type + ", autoIncrement=" + this.autoIncrement + ", nullable=" + this.nullable + ", defaultValue="
				+ this.defaultValue + ", onUpdate=" + this.onUpdate + ", primaryKey=" + this.primaryKey + ", unique=" + this.unique
				+ ", foreignKey=" + this.foreignKey + ", field=" + this.field + "]";
	}

}
