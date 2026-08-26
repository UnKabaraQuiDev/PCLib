package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.time.MonthDay;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MonthDayStringColumnType implements ColumnType<MonthDay, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry
			.getFixedEncodingType(VarcharEncodingType.class, 64, VarcharEncodingType::new);

	@Override
	public MonthDay decode(final String value, final Type type) {
		return MonthDay.parse(value);
	}

	@Override
	public String encode(final MonthDay value) {
		return value.toString();
	}

}
