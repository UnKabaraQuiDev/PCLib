package lu.kbra.pclib.db.autobuild.mysql.text;

import java.lang.reflect.Type;

import org.json.JSONObject;

import lombok.Getter;
import lombok.NonNull;
import lu.kbra.pclib.db.autobuild.mysql.encoding.misc.JsonEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

@Getter
public class JsonObjectColumnType implements ColumnType<JSONObject, String> {

	private final JsonEncodingType encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(JsonEncodingType.class,
			JsonEncodingType::new);

	@Override
	public @NonNull JSONObject decode(@NonNull String value, Type type) {
		return new JSONObject(value);
	}

	@Override
	public @NonNull String encode(@NonNull JSONObject value) {
		return value.toString();
	}

}