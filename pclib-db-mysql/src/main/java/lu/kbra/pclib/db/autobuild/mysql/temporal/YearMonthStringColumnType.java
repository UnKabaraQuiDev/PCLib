package lu.kbra.pclib.db.autobuild.mysql.temporal;

import java.lang.reflect.Type;
import java.time.YearMonth;

import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class YearMonthStringColumnType implements ColumnType<YearMonth, String> {

	private final EncodingType<String> encodingType = MySQLColumnTypeRegistry
			.getFixedEncodingType(VarcharEncodingType.class, 10, VarcharEncodingType::new);

	@Override
	public @NonNull YearMonth decode(@NonNull String value, Type type) {
		return YearMonth.parse(value);
	}

	@Override
	public @NonNull String encode(@NonNull YearMonth value) {
		return value.toString();
	}

}
