package lu.kbra.pclib.db.autobuild.mysql.temporal;

import java.lang.reflect.Type;
import java.time.Period;

import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

// As ISO-8601 String
@Getter
@RequiredArgsConstructor
public class PeriodType implements ColumnType<Period, String> {

	private final EncodingType<String> encodingType = MySQLColumnTypeRegistry
			.getFixedEncodingType(VarcharEncodingType.class, 64, VarcharEncodingType::new);

	@Override
	public @NonNull Period decode(@NonNull String value, Type type) {
		return Period.parse(value);
	}

	@Override
	public @NonNull String encode(@NonNull Period value) {
		return value.toString();
	}

}
