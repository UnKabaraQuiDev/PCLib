package lu.kbra.pclib.db.autobuild.postgres.column.temporal;

import java.lang.reflect.Type;
import java.time.YearMonth;

import lu.kbra.pclib.db.autobuild.postgres.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class YearMonthStringColumnType implements ColumnType<YearMonth, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry
			.getFixedEncodingType(VarcharEncodingType.class, 10, VarcharEncodingType::new);

	@Override
	public YearMonth decode(final String value, final Type type) {
		return YearMonth.parse(value);
	}

	@Override
	public String encode(final YearMonth value) {
		return value.toString();
	}

}
