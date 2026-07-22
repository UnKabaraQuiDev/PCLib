package lu.kbra.pclib.db.autobuild.mysql.column.text;

import java.lang.reflect.Type;

import org.json.JSONArray;

import lu.kbra.pclib.db.autobuild.mysql.encoding.misc.JsonEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class JsonArrayColumnType implements ColumnType<JSONArray, String> {

	private final JsonEncodingType encodingType = EncodingTypeRegistry.getFixedEncodingType(JsonEncodingType.class,
			JsonEncodingType::new);

	@Override
	public @NonNull JSONArray decode(@NonNull String value, Type type) {
		return new JSONArray(value);
	}

	@Override
	public @NonNull String encode(@NonNull JSONArray value) {
		return value.toString();
	}

}
