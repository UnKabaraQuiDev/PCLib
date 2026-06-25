package lu.kbra.pclib.db.autobuild.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataBaseStructure {

	private String name;
	private Map<String, Object> baseHints;
	private final List<TableStructure> tableStructures = new ArrayList<>();

	public <V> V getBaseHint(final String key) {
		return (V) this.baseHints.get(key);
	}

	public <V> V getBaseHint(final String key, final V default_) {
		return (V) this.baseHints.getOrDefault(key, default_);
	}

	public <V> boolean hasBaseHint(final String key) {
		return this.baseHints.containsKey(key);
	}

}
