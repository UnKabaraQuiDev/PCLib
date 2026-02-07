package lu.kbra.pclib.db.autobuild.column;

import lu.kbra.pclib.db.autobuild.column.Generated.Type;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

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
	public String build(DataBaseConnector conn) {
		return getEscapedName() + " " + type.build(conn) + " GENERATED ALWAYS AS (" + super.defaultValue + ") " + storageType.name()
				+ (nullable || conn.getProtocol().equalsIgnoreCase("sqlite") ? "" : " NOT NULL");
	}

}
