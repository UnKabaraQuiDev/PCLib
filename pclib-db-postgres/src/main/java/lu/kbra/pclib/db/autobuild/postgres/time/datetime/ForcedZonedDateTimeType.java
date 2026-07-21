package lu.kbra.pclib.db.autobuild.postgres.time.datetime;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class ForcedZonedDateTimeType implements FixedColumnType<Timestamp> {

	private final ZoneId zone;

	public ForcedZonedDateTimeType(ZoneId zone) {
		this.zone = Objects.requireNonNull(zone);
	}

	public ForcedZonedDateTimeType(String zone) {
		this.zone = ZoneId.of(Objects.requireNonNull(zone));
	}

	public ForcedZonedDateTimeType(Object zone) {
		this.zone = zone instanceof ZoneId ? (ZoneId) zone : ZoneId.of(Objects.toString(Objects.requireNonNull(zone)));
	}

	@Override
	public Object decode(Timestamp value, Type type) {
		if (value == null) {
			return null;
		}

		if (type == ZonedDateTime.class) {
			return value.toLocalDateTime().atZone(zone);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Timestamp encode(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof ZonedDateTime) {
			return Timestamp.valueOf(((ZonedDateTime) value).withZoneSameInstant(zone).toLocalDateTime());
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