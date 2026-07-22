package lu.kbra.pclib.db.autobuild.mysql.column.temporal;

import java.lang.reflect.Type;
import java.time.MonthDay;

import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MonthDayStringColumnType implements ColumnType<MonthDay, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry
			.getFixedEncodingType(VarcharEncodingType.class, 64, VarcharEncodingType::new);

	@Override
	public @NonNull MonthDay decode(@NonNull String value, Type type) {
		return MonthDay.parse(value);
	}

	@Override
	public @NonNull String encode(@NonNull MonthDay value) {
		return value.toString();
	}

}
