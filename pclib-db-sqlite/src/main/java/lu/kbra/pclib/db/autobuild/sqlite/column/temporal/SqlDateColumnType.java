package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.sql.Date;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.DateEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class SqlDateColumnType implements ColumnType<java.sql.Date, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry.getFixedEncodingType(DateEncodingType.class,
			DateEncodingType::new);

	@Override
	public @NonNull Date decode(@NonNull String value, Type type) {
		return java.sql.Date.valueOf(value);
	}

	@Override
	public @NonNull String encode(@NonNull Date value) {
		return value.toString();
	}

}
