package lu.kbra.pclib.db.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.core.convert.ConversionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;
import lu.kbra.pclib.db.exception.EncodeFailedException;

public class MapType implements FixedColumnType {

	protected final ObjectMapper objectMapper;
	protected final ConversionService conversionService;

	public MapType(final ObjectMapper objectMapper, final ConversionService conversionService) {
		this.objectMapper = objectMapper.copy();
		this.conversionService = conversionService;
		this.objectMapper.activateDefaultTyping(this.objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
	}

	@Override
	public Object decode(final Object value, final Type type) {
		if (value == null) {
			return null;
		}

		if (type instanceof final ParameterizedType parameterizedType) {

			final Type rawType = parameterizedType.getRawType();

			if (rawType instanceof final Class<?> rawClass && Map.class.isAssignableFrom(rawClass)) {
				final Type keyType = parameterizedType.getActualTypeArguments()[0];
				final Type valueType = parameterizedType.getActualTypeArguments()[1];

				if (!(keyType instanceof final Class<?> keyClass) || !(valueType instanceof final Class<?> valueClass)) {
					throw new IllegalArgumentException("Unsupported map types: " + keyType + ", " + valueType);
				}

				final Map<Object, Object> map;

				if (rawClass.equals(Map.class)) {
					map = new HashMap<>();
				} else {
					map = (Map<Object, Object>) PCUtils.newInstance(rawClass);
				}

				final JSONObject json = new JSONObject((String) value);

				for (final String key : json.keySet()) {
					final Object decodedKey = keyClass == String.class ? key : this.conversionService.convert(key, keyClass);
					final Object rawValue = json.get(key);
					final Object decodedValue;

					if (rawValue == JSONObject.NULL) {
						decodedValue = null;
					} else if (valueClass.isAssignableFrom(rawValue.getClass())) {
						decodedValue = valueClass.cast(rawValue);
					} else {
						decodedValue = this.conversionService.convert(rawValue, valueClass);
					}

					map.put(decodedKey, decodedValue);
				}

				return map;
			}
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Object encode(final Object value) {
		if (!(value instanceof Map<?, ?>)) {
			return ColumnType.unsupported(value);
		}

		try {
			return this.objectMapper.writeValueAsString(value);
		} catch (final JsonProcessingException e) {
			throw new EncodeFailedException("Could not serialize map.", e);
		}
	}

	@Override
	public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getString(columnName);
	}

	@Override
	public String getTypeName() {
		return "JSON";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setString(index, (String) value);
	}
}
