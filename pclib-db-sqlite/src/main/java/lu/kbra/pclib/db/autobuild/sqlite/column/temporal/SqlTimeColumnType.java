package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.TimeEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SqlTimeColumnType implements ColumnType<Time, String> {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

	private final EncodingType<String> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimeEncodingType.class,
			TimeEncodingType::new);

	@Override
	public Time decode(final String value, final Type type) {
		return Time.valueOf(LocalTime.parse(value, SqlTimeColumnType.FORMATTER));
	}

	@Override
	public String encode(final Time value) {
		return value.toLocalTime().format(SqlTimeColumnType.FORMATTER);
	}

}
