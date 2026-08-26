package lu.kbra.pclib.db.autobuild.postgres.column.text;

import java.lang.reflect.Type;

import org.json.JSONObject;

import lu.kbra.pclib.db.autobuild.postgres.encoding.misc.JsonEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;

@Getter
public class JsonObjectColumnType implements ColumnType<JSONObject, String> {

	private final JsonEncodingType encodingType = EncodingTypeRegistry.getFixedEncodingType(JsonEncodingType.class, JsonEncodingType::new);

	@Override
	public JSONObject decode(final String value, final Type type) {
		return new JSONObject(value);
	}

	@Override
	public String encode(final JSONObject value) {
		return value.toString();
	}

}
