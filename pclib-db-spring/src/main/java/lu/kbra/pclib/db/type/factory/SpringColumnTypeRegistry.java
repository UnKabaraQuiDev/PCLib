package lu.kbra.pclib.db.type.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;
import lu.kbra.pclib.db.type.ListType;
import lu.kbra.pclib.db.type.MapType;
import lu.kbra.pclib.db.utils.DelegatingHintOwner;
import lu.kbra.pclib.db.utils.registry.ColumnTypeFactory;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

@Component
public class SpringColumnTypeRegistry implements DatabaseTypeFactory {

	private final ObjectMapper objectMapper;
	private final ConversionService conversionService;

	public SpringColumnTypeRegistry(final ObjectMapper objectMapper, final ConversionService conversionService) {
		this.objectMapper = objectMapper;
		this.conversionService = conversionService;
	}

	@Override
	public void registerColumnTypes(final List<ColumnTypeFactory<?>> typeMap) {
		ColumnTypeRegistry.registerType(ListType.class,
				(clazz, map, etp) -> clazz == List.class || clazz == ArrayList.class || clazz == LinkedList.class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ListType(this.objectMapper,
						this.conversionService,
						etp.getTypeFor(String.class, new DelegatingHintOwner(Map.of(DefaultTypeHints.JSON, true)))),
				typeMap);

		ColumnTypeRegistry
				.registerType(MapType.class,
						(clazz, map, etp) -> clazz == Map.class || clazz == HashMap.class || clazz == LinkedHashMap.class
								|| clazz == TreeMap.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
						(type, map, etp) -> new MapType(this.objectMapper,
								this.conversionService,
								etp.getTypeFor(String.class, new DelegatingHintOwner(Map.of(DefaultTypeHints.JSON, true)))),
						typeMap);
	}

	@Override
	public boolean matches(final String protocol) {
		return true;
	}

}
