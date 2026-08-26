package lu.kbra.pclib.db.autobuild.postgres.column.text;

import java.lang.reflect.Type;

import org.json.JSONArray;

import lu.kbra.pclib.db.autobuild.postgres.encoding.misc.JsonEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;

@Getter
public class JsonArrayColumnType implements ColumnType<JSONArray, String> {

	private final JsonEncodingType encodingType = EncodingTypeRegistry.getFixedEncodingType(JsonEncodingType.class, JsonEncodingType::new);

	@Override
	public JSONArray decode(final String value, final Type type) {
		return new JSONArray(value);
	}

	@Override
	public String encode(final JSONArray value) {
		return value.toString();
	}

}
