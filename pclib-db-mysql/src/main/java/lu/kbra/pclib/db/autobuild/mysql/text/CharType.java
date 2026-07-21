package lu.kbra.pclib.db.autobuild.mysql.text;

import java.lang.reflect.Type;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

@Getter
@RequiredArgsConstructor
public class CharType implements ColumnType<Character, String> {

	private final EncodingType<String> encodingType;

	@Override
	public @NonNull Character decode(@NonNull String value, Type type) {
		return value.charAt(0);
	}

	@Override
	public @NonNull String encode(@NonNull Character value) {
		return value.toString();
	}

}