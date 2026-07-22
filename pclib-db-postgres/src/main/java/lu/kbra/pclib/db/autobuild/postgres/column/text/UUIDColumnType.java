package lu.kbra.pclib.db.autobuild.postgres.column.text;

import java.lang.reflect.Type;
import java.util.UUID;

import lu.kbra.pclib.db.autobuild.postgres.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;

@Getter
public class UUIDColumnType implements ColumnType<UUID, String> {

	private final CharEncodingType encodingType = EncodingTypeRegistry
			.getFixedEncodingType(CharEncodingType.class, 36, CharEncodingType::new);

	@Override
	public UUID decode(String value, Type type) {
		return UUID.fromString(value);
	}

	@Override
	public String encode(UUID value) {
		return value.toString();
	}

}
