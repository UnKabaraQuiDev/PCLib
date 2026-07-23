package lu.kbra.pclib.db.type;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NonNull;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.exception.DecodeFailedException;
import lu.kbra.pclib.db.exception.EncodeFailedException;

public class ListColumnType implements ColumnType<List<?>, String> {

	private final ObjectMapper objectMapper;

	@Getter
	private final EncodingType<String> encodingType;

	public ListColumnType(final ObjectMapper objectMapper, final EncodingType<String> jsonEncodingType) {
		this.objectMapper = objectMapper;
		this.encodingType = jsonEncodingType;
	}

	@Override
	public @NonNull List<?> decode(@NonNull String value, Type type) {
		try {
			return this.objectMapper.readValue(value, this.objectMapper.getTypeFactory().constructType(type));
		} catch (IOException e) {
			throw new DecodeFailedException("Couldn't decode JSON.", e);
		}
	}

	@Override
	public @NonNull String encode(@NonNull List<?> value) {
		try {
			return this.objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new EncodeFailedException("Couldn't generate JSON.", e);
		}
	}

}
