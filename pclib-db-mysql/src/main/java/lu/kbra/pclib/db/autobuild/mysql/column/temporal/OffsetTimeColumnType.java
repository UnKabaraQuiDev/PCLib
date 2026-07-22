package lu.kbra.pclib.db.autobuild.mysql.column.temporal;

import java.lang.reflect.Type;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

// As ISO-8601 String
@Getter
@RequiredArgsConstructor
public class OffsetTimeColumnType implements ColumnType<OffsetTime, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry
			.getFixedEncodingType(VarcharEncodingType.class, 64, VarcharEncodingType::new);

	@Override
	public @NonNull OffsetTime decode(@NonNull String value, Type type) {
		return OffsetTime.parse(value, DateTimeFormatter.ISO_OFFSET_TIME);
	}

	@Override
	public @NonNull String encode(@NonNull OffsetTime value) {
		return value.format(DateTimeFormatter.ISO_OFFSET_TIME);
	}

}
