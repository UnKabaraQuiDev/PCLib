package lu.kbra.pclib.db.autobuild.column;

import lu.kbra.pclib.db.autobuild.column.Generated.Type;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class GeneratedColumnData extends ColumnData {

	protected Type storageType;

	public GeneratedColumnData(final ColumnData columnData, final Generated gen) {
		super(columnData);

		this.storageType = gen.value();
	}

	public void setStorageType(final Type storageType) {
		this.storageType = storageType;
	}

	public Type getStorageType() {
		return this.storageType;
	}

	@Override
	public String build(final DataBaseConnector conn) {
		return this.getEscapedName() + " " + this.type.build(conn) + " GENERATED ALWAYS AS (" + super.defaultValue + ") "
				+ this.storageType.name() + (this.nullable || "sqlite".equalsIgnoreCase(conn.getProtocol()) ? "" : " NOT NULL");
	}

}
