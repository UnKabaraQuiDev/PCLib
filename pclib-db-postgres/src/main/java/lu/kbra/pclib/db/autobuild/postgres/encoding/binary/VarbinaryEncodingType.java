package lu.kbra.pclib.db.autobuild.postgres.encoding.binary;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.domain.column.type.EncodingType.VariableEncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VarbinaryEncodingType implements VariableEncodingType<byte[]> {

	private final int length;

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
		return "VARBINARY";
	}

	@Override
	public Object variableValue() {
		return length;
	}

}
