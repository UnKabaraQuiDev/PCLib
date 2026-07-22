package lu.kbra.pclib.db.autobuild.sqlite.column.text;

import java.lang.reflect.Type;
import java.util.UUID;

import lombok.Getter;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
public class UUIDType implements ColumnType<UUID, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry
			.getFixedEncodingType(VarcharEncodingType.class, 36, VarcharEncodingType::new);

	@Override
	public UUID decode(String value, Type type) {
		return UUID.fromString(value);
	}

	@Override
	public String encode(UUID value) {
		return value.toString();
	}

}
