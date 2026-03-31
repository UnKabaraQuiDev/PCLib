package lu.kbra.pclib.db.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.springframework.core.convert.ConversionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;
import lu.kbra.pclib.db.exception.DBException;

public class ListType implements FixedColumnType {

	protected final ObjectMapper objectMapper;
	protected final ConversionService conversionService;

	public ListType(final ObjectMapper objectMapper, final ConversionService conversionService) {
		this.objectMapper = objectMapper;
		this.conversionService = conversionService;
	}

	@Override
	public String getTypeName() {
		return "JSON";
	}

	@Override
	public Object encode(final Object value) {
		if (value instanceof JSONArray) {
			return ((JSONArray) value).toString();
		} else if (value instanceof List<?>) {
			// JSONArray cast doesn't properly handle custom objects (returns list of empty
			// objects)
			try {
				return this.objectMapper.writeValueAsString(value);
			} catch (final JsonProcessingException e) {
				throw new DBException("Couldnt parse JSON.", e);
			}
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Object decode(final Object value, final Type type) {
		if (value == null) {
			return null;
		}

		if (type == JSONArray.class) {
			return new JSONArray((String) value);
		} else if (type instanceof final ParameterizedType parameterizedType) {
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
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setString(index, (String) value);
	}

	@Override
	public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getString(columnName);
	}

}
