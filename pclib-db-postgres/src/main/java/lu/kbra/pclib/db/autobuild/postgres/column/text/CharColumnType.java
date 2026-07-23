package lu.kbra.pclib.db.autobuild.postgres.column.text;

import java.lang.reflect.Type;

import lu.kbra.pclib.db.autobuild.postgres.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CharColumnType implements ColumnType<Character, String> {

	private final EncodingType<String> encodingType;

	public CharColumnType() {
		this.encodingType = new CharEncodingType(1);
	}

	@Override
	public @NonNull Character decode(@NonNull String value, Type type) {
		return value.charAt(0);
	}

	@Override
	public @NonNull String encode(@NonNull Character value) {
		return value.toString();
	}

}
