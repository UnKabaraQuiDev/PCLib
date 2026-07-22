package lu.kbra.pclib.db.autobuild.postgres.column.text;

import java.lang.reflect.Type;

import lombok.Getter;
import lombok.NonNull;
import lu.kbra.pclib.db.autobuild.postgres.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
public class CharArrayColumnType implements ColumnType<char[], String> {

	private final EncodingType<String> encodingType;

	public CharArrayColumnType(final int length, final boolean max) {
		if (max) {
			this.encodingType = new VarcharEncodingType(length);
		} else {
			this.encodingType = new CharEncodingType(length);
		}
	}

	public CharArrayColumnType(final Object object, final boolean max) {
		this(ColumnType.asInt(object), max);
	}

	public CharArrayColumnType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(TextEncodingType.class, TextEncodingType::new);
	}

	@Override
	public @NonNull char[] decode(@NonNull String value, Type type) {
		return value.toCharArray();
	}

	@Override
	public @NonNull String encode(@NonNull char[] value) {
		return new String(value);
	}

}
