package lu.kbra.pclib.db.autobuild.postgres.encoding.bool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.VariableEncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BitEncodingType implements VariableEncodingType<boolean[]> {

	private final int length;

	@Override
	public boolean[] getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return BitEncodingType.unpack(rs.getBytes(columnIndex));
	}

	@Override
	public boolean[] getObject(final ResultSet rs, final String columnName) throws SQLException {
		return BitEncodingType.unpack(rs.getBytes(columnName));
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final boolean[] value) throws SQLException {
		stmt.setBytes(index, BitEncodingType.pack(value));
	}

	@Override
	public int getSQLType() {
		return Types.BIT;
	}

	@Override
	public String getTypeName() {
		return "BIT";
	}

	@Override
	public Object getVariableValue() {
		return this.length;
	}

	private static boolean[] unpack(final byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		final boolean[] bits = new boolean[bytes.length * 8];

		for (int i = 0; i < bits.length; i++) {
			final int b = bytes[i / 8] & 0xFF;
			bits[i] = (b & 1 << 7 - i % 8) != 0;
		}

		return bits;
	}

	private static byte[] pack(final boolean[] bits) {
		if (bits == null) {
			return null;
		}

		final byte[] bytes = new byte[(bits.length + 7) / 8];

		for (int i = 0; i < bits.length; i++) {
			if (bits[i]) {
				bytes[i / 8] |= (byte) (1 << 7 - i % 8);
			}
		}

		return bytes;
	}

}
