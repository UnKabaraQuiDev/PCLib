package lu.kbra.pclib.db.domain.table;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class StructureName implements MapConvertible {

	private final String name;
	private final String[] nameParts;
	private final String qualifiedName;

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("nameParts", nameParts);
		map.put("qualifiedName", qualifiedName);

		return map;
	}

}
