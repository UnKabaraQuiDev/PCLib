package lu.kbra.pclib.db.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.core.convert.ConversionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NonNull;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.exception.EncodeFailedException;

public class MapType implements ColumnType<Map<?, ?>, String> {

	private final ObjectMapper objectMapper;
	private final ConversionService conversionService;

	@Getter
	private EncodingType<String> encodingType;

	public MapType(
			final ObjectMapper objectMapper,
			final ConversionService conversionService,
			final EncodingType<String> jsonEncodingType) {
		this.objectMapper = objectMapper.copy();
		this.conversionService = conversionService;
		this.objectMapper.activateDefaultTyping(this.objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
		this.encodingType = jsonEncodingType;
	}

	@Override
	public @NonNull Map<?, ?> decode(@NonNull String value, Type type) {
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
	public @NonNull String encode(@NonNull Map<?, ?> value) {
		try {
			return this.objectMapper.writeValueAsString(value);
		} catch (final JsonProcessingException e) {
			throw new EncodeFailedException("Could not serialize map.", e);
		}
	}
}
