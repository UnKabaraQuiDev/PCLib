package lu.kbra.pclib.db.utils.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.core.convert.ConversionService;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.db.type.ListType;
import lu.kbra.pclib.db.type.MapType;

public class SpringColumnTypeRegistry implements ColumnTypeRegistry {

	private final ObjectMapper objectMapper;
	private final ConversionService conversionService;

	public SpringColumnTypeRegistry(final ObjectMapper objectMapper, final ConversionService conversionService) {
		this.objectMapper = objectMapper;
		this.conversionService = conversionService;
	}

	@Override
	public void registerTypes(final List<ColumnTypeFactory> typeMap) {
		ColumnTypeRegistry.registerType(ListType.class,
				(clazz, map) -> clazz == List.class || clazz == ArrayList.class || clazz == LinkedList.class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ListType(this.objectMapper, this.conversionService),
				typeMap);

		ColumnTypeRegistry.registerType(MapType.class,
				(clazz, map) -> clazz == Map.class || clazz == HashMap.class || clazz == LinkedHashMap.class || clazz == TreeMap.class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new MapType(this.objectMapper, this.conversionService),
				typeMap);
	}

}
