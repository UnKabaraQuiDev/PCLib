package lu.kbra.pclib.db.autobuild.mysql.encoding.binary;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

@Getter
@RequiredArgsConstructor
public class BinaryEncodingType implements EncodingType<byte[]> {

	private final boolean variable;
	private final int length;

	public BinaryEncodingType(int length) {
		this.variable = true;
		this.length = length;
	}

	public BinaryEncodingType() {
		this.variable = false;
		this.length = 0;
	}

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
		return "BINARY";
	}

	@Override
	public Object variableValue() {
		return length;
	}

}
