package lu.kbra.pclib.db.autobuild.sqlite.column.text;

import java.lang.reflect.Type;

import org.json.JSONObject;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;

@Getter
public class JsonObjectColumnType implements ColumnType<JSONObject, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry.getFixedEncodingType(TextEncodingType.class,
			TextEncodingType::new);

	@Override
	public JSONObject decode(final String value, final Type type) {
		return new JSONObject(value);
	}

	@Override
	public String encode(final JSONObject value) {
		return value.toString();
	}

}
