package lu.kbra.pclib.db.autobuild.sqlite.column.text;

import java.lang.reflect.Type;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CharColumnType implements ColumnType<Character, String> {

	private final EncodingType<String> encodingType;

	public CharColumnType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(VarcharEncodingType.class, 1, VarcharEncodingType::new);
	}

	@Override
	public Character decode(final String value, final Type type) {
		return value.charAt(0);
	}

	@Override
	public String encode(final Character value) {
		return value.toString();
	}

}
