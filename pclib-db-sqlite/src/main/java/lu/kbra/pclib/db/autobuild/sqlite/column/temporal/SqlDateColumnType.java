package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.sql.Date;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.DateEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SqlDateColumnType implements ColumnType<java.sql.Date, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry.getFixedEncodingType(DateEncodingType.class,
			DateEncodingType::new);

	@Override
	public Date decode(final String value, final Type type) {
		return java.sql.Date.valueOf(value);
	}

	@Override
	public String encode(final Date value) {
		return value.toString();
	}

}
