package lu.kbra.pclib.db.autobuild.sqlite.encoding.binary;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

@Getter
@RequiredArgsConstructor
public class BlobEncodingType implements FixedEncodingType<byte[]> {

	@Override
	public byte[] getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getBytes(columnIndex);
	}

	@Override
	public byte[] getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getBytes(columnName);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, byte[] value) throws SQLException {
		stmt.setBytes(index, value);
	}

	@Override
	public String getTypeName() {
		return "BLOB";
	}

}
