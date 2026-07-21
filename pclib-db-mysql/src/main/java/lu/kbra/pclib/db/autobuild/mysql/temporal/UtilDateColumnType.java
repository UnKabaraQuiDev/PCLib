package lu.kbra.pclib.db.autobuild.mysql.temporal;

import java.lang.reflect.Type;
import java.sql.Date;

import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.DateEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UtilDateColumnType implements ColumnType<java.util.Date, java.sql.Date> {

	private final EncodingType<java.sql.Date> encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(DateEncodingType.class,
			DateEncodingType::new);

	@Override
	public java.util.@NonNull Date decode(@NonNull java.sql.Date value, Type type) {
		return value;
	}

	@Override
	public @NonNull java.sql.Date encode(java.util.@NonNull Date value) {
		return new Date(value.getTime());
	}

}
