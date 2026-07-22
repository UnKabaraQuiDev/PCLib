package lu.kbra.pclib.db.autobuild.sqlite.column.text;

import lombok.Getter;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
public class StringColumnType implements IdentityColumnType<String> {

	private final EncodingType<String> encodingType;

	public StringColumnType(final int length, final boolean max) {
		if (max) {
			this.encodingType = new VarcharEncodingType(length);
		} else {
			this.encodingType = EncodingTypeRegistry.getFixedEncodingType(TextEncodingType.class, TextEncodingType::new);
		}
	}

	public StringColumnType(final Object object, final boolean max) {
		this(ColumnType.asInt(object), max);
	}

	public StringColumnType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(TextEncodingType.class, TextEncodingType::new);
	}

}
