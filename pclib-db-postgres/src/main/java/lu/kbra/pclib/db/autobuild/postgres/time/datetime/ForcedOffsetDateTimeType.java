package lu.kbra.pclib.db.autobuild.postgres.time.datetime;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class ForcedOffsetDateTimeType implements FixedColumnType<Timestamp> {

	private final ZoneOffset zone;

	public ForcedOffsetDateTimeType(ZoneOffset zone) {
		this.zone = Objects.requireNonNull(zone);
	}

	public ForcedOffsetDateTimeType(String zone) {
		this.zone = ZoneOffset.of(Objects.requireNonNull(zone));
	}

	public ForcedOffsetDateTimeType(Object zone) {
		this.zone = zone instanceof ZoneOffset ? (ZoneOffset) zone : ZoneOffset.of(Objects.toString(Objects.requireNonNull(zone)));
	}

	@Override
	public Object decode(Timestamp value, Type type) {
		if (value == null) {
			return null;
		}

		if (type == OffsetDateTime.class) {
			return value.toLocalDateTime().atZone(zone);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Timestamp encode(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof OffsetDateTime) {
			return Timestamp.valueOf(((OffsetDateTime) value).withOffsetSameInstant(zone).toLocalDateTime());
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Timestamp getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getTimestamp(columnIndex);
	}

	@Override
	public Timestamp getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getTimestamp(columnName);
	}

	@Override
	public String getTypeName() {
		return "TIMESTAMP";
	}

	@Override
	public int getSQLType() {
		return Types.TIMESTAMP;
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Timestamp value) throws SQLException {
		stmt.setTimestamp(index, value);
	}

}