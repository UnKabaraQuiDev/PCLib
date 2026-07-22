package lu.kbra.pclib.db.autobuild.postgres.encoding.bool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.domain.column.type.EncodingType.VariableEncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BitEncodingType implements VariableEncodingType<boolean[]> {

	private final int length;

	@Override
	public boolean[] getObject(ResultSet rs, int columnIndex) throws SQLException {
		return unpack(rs.getBytes(columnIndex));
	}

	@Override
	public boolean[] getObject(ResultSet rs, String columnName) throws SQLException {
		return unpack(rs.getBytes(columnName));
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, boolean[] value) throws SQLException {
		stmt.setBytes(index, pack(value));
	}

	@Override
	public String getTypeName() {
		return "BIT";
	}

	@Override
	public Object variableValue() {
		return length;
	}

	private static boolean[] unpack(byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		boolean[] bits = new boolean[bytes.length * 8];

		for (int i = 0; i < bits.length; i++) {
			int b = bytes[i / 8] & 0xFF;
			bits[i] = (b & (1 << (7 - (i % 8)))) != 0;
		}

		return bits;
	}

	private static byte[] pack(boolean[] bits) {
		if (bits == null) {
			return null;
		}

		byte[] bytes = new byte[(bits.length + 7) / 8];

		for (int i = 0; i < bits.length; i++) {
			if (bits[i]) {
				bytes[i / 8] |= (byte) (1 << (7 - (i % 8)));
			}
		}

		return bytes;
	}

}
