package lu.kbra.pclib.db.autobuild.mysql.column.text;

import java.lang.reflect.Type;

import lu.kbra.pclib.db.autobuild.mysql.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.meta.SizeClass;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;

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

	public CharSequenceColumnType(final SizeClass sizeClass) {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(TextEncodingType.class, sizeClass, TextEncodingType::new);
	}

	public CharSequenceColumnType() {
		this(SizeClass.NORMAL);
	}

	@Override
	public CharSequence decode(final String value, final Type type) {
		return value;
	}

	@Override
	public String encode(final CharSequence value) {
		return value.toString();
	}

}
