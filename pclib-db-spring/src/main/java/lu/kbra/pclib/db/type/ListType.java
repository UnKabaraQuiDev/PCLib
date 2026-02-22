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
import lu.kbra.pclib.db.table.DBException;

public class ListType implements FixedColumnType {

	protected final ObjectMapper objectMapper;
	protected final ConversionService conversionService;

	public ListType(ObjectMapper objectMapper, ConversionService conversionService) {
		this.objectMapper = objectMapper;
		this.conversionService = conversionService;
	}

	@Override
	public String getTypeName() {
		return "JSON";
	}

	@Override
	public Object encode(Object value) {
		if (value instanceof JSONArray) {
			return ((JSONArray) value).toString();
		} else if (value instanceof List<?>) {
			// JSONArray cast doesn't properly handle custom objects (returns list of empty
			// objects)
			try {
				return objectMapper.writeValueAsString(value);
			} catch (JsonProcessingException e) {
				throw new DBException("Couldnt parse JSON.", e);
			}
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Object decode(Object value, Type type) {
		if (value == null)
			return null;

		if (type == JSONArray.class) {
			return new JSONArray((String) value);
		} else if (type instanceof ParameterizedType) {
			final ParameterizedType parameterizedType = (ParameterizedType) type;
			final Type rawType = parameterizedType.getRawType();

			if (rawType instanceof Class<?> && List.class.isAssignableFrom((Class<?>) rawType)) {
				final Class<?> rawClass = (Class<?>) rawType;
				final Type elementType = parameterizedType.getActualTypeArguments()[0];

				if (!(elementType instanceof Class<?>)) {
					throw new IllegalArgumentException("Unsupported element type: " + elementType);
				}
				final Class<?> elementClass = (Class<?>) elementType;

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
						list.add(conversionService.convert(item, elementClass));
					}
				});

				return list;
			}
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
		stmt.setString(index, (String) value);
	}

	@Override
	public String getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getString(columnName);
	}

}
