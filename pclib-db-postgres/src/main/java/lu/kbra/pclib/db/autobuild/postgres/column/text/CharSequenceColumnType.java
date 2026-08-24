package lu.kbra.pclib.db.autobuild.postgres.column.text;

import java.lang.reflect.Type;

import lu.kbra.pclib.db.autobuild.postgres.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class CharSequenceColumnType implements ColumnType<CharSequence, String> {

	private final EncodingType<String> encodingType;

	public CharSequenceColumnType(final int length, final boolean max) {
		if (max) {
			this.encodingType = new VarcharEncodingType(length);
		} else {
			this.encodingType = new CharEncodingType(length);
		}
	}

	public CharSequenceColumnType(final Object object, final boolean max) {
		this(ColumnType.asInt(object), max);
	}

	public CharSequenceColumnType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(TextEncodingType.class, TextEncodingType::new);
	}

	@Override
	public @NonNull CharSequence decode(@NonNull String value, Type type) {
		return value;
	}

	@Override
	public @NonNull String encode(@NonNull CharSequence value) {
		return value.toString();
	}

}
