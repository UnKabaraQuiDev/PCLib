package lu.kbra.pclib.db.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.springframework.core.convert.ConversionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NonNull;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.exception.EncodeFailedException;

public class ListType implements ColumnType<List<?>, String> {

	private final ObjectMapper objectMapper;
	private final ConversionService conversionService;

	@Getter
	private final EncodingType<String> encodingType;

	public ListType(
			final ObjectMapper objectMapper,
			final ConversionService conversionService,
			final EncodingType<String> jsonEncodingType) {
		this.objectMapper = objectMapper.copy();
		this.conversionService = conversionService;
		this.objectMapper.activateDefaultTyping(this.objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
		this.encodingType = jsonEncodingType;
	}

	@Override
	public @NonNull List<?> decode(@NonNull String value, Type type) {
		if (type instanceof final ParameterizedType parameterizedType) {
			final Type rawType = parameterizedType.getRawType();

			if (rawType instanceof final Class<?> rawClass && List.class.isAssignableFrom((Class<?>) rawType)) {
				final Type elementType = parameterizedType.getActualTypeArguments()[0];

				if (!(elementType instanceof final Class<?> elementClass)) {
					throw new IllegalArgumentException("Unsupported element type: " + elementType);
				}
				final List<Object> list;
				if (rawClass.equals(List.class)) {
					list = new ArrayList<>();
				} else {
					list = (List<Object>) PCUtils.newInstance(rawClass);
				}

				final JSONArray array = new JSONArray((String) value);
				array.forEach(item -> {
					if (elementClass.isAssignableFrom(item.getClass())) {
						list.add(elementClass.cast(item));
					} else {
						list.add(this.conversionService.convert(item, elementClass));
					}
				});

				return list;
			}
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public @NonNull String encode(@NonNull List<?> value) {
		// JSONArray cast doesn't properly handle custom objects (returns list of empty
		// objects)
		try {
			return this.objectMapper.writeValueAsString(value);
		} catch (final JsonProcessingException e) {
			throw new EncodeFailedException("Couldn't generate JSON.", e);
		}
	}

}
