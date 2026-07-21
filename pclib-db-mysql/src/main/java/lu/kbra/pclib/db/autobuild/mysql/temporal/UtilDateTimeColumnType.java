package lu.kbra.pclib.db.autobuild.mysql.temporal;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;

import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.TimestampEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UtilDateTimeColumnType implements ColumnType<java.util.Date, Timestamp> {

	private final EncodingType<Timestamp> encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(TimestampEncodingType.class,
			TimestampEncodingType::new);

	@Override
	public java.util.@NonNull Date decode(@NonNull Timestamp value, Type type) {
		return new Date(value.getTime());
	}

	@Override
	public @NonNull Timestamp encode(java.util.@NonNull Date value) {
		return new Timestamp(value.getTime());
	}

}
