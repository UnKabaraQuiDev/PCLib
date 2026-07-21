package lu.kbra.pclib.db.autobuild.mysql.temporal;

import java.lang.reflect.Type;
import java.time.Duration;

import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DurationType implements ColumnType<Duration, String> {

	private final EncodingType<String> encodingType = MySQLColumnTypeRegistry
			.getFixedEncodingType(VarcharEncodingType.class, 64, VarcharEncodingType::new);

	@Override
	public @NonNull Duration decode(@NonNull String value, Type type) {
		return Duration.parse(value);
	}

	@Override
	public @NonNull String encode(@NonNull Duration value) {
		return value.toString();
	}

}
