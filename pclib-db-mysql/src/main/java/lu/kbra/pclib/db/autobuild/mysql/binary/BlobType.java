package lu.kbra.pclib.db.autobuild.mysql.binary;

import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class BlobType implements FixedColumnType<Blob> {

	@Override
	public Object decode(final Blob value, final Type type) {
		if (type == Blob.class) {
			return (Blob) value;
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Blob encode(final Object value) {
		if (value instanceof Blob) {
			return (Blob) value;
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Blob getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getBlob(columnIndex);
	}

	@Override
	public Blob getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getBlob(columnName);
	}

	@Override
	public String getTypeName() {
		return "BLOB";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Blob value) throws SQLException {
		stmt.setBlob(index, value);
	}

}