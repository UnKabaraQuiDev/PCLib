package lu.kbra.pclib.db.autobuild.column;

import lu.kbra.pclib.db.autobuild.column.Generated.Type;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class GeneratedColumnData extends ColumnData {

	protected Type storageType;

	public GeneratedColumnData(final ColumnData columnData, final Generated gen) {
		super(columnData);

		this.storageType = gen.value();
	}

	@Override
	public String toString() {
		return "GeneratedColumnData@" + System.identityHashCode(this) + " [storageType=" + storageType + ", name=" + name + ", type=" + type
				+ ", autoIncrement=" + autoIncrement + ", nullable=" + nullable + ", defaultValue=" + defaultValue + ", onUpdate="
				+ onUpdate + ", primaryKey=" + primaryKey + ", unique=" + unique + ", foreignKey=" + foreignKey + ", field=" + field + "]";
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

}
