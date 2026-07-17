package lu.kbra.pclib.db.utils;

import java.util.HashMap;
import java.util.Map;

import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.impl.MapConvertible;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class QueryableTemplate implements HintsOwner, MapConvertible {

	protected final Map<String, Object> hints;

	public QueryableTemplate(final Class<? extends SQLQueryable<?>> queryableClass) {
		this.hints = new HashMap<>();
		this.setTargetClass(queryableClass);
	}

	public QueryableTemplate setName(final String name) {
		this.hints.put(DefaultQueryableHints.NAME_OVERRIDE, name);
		return this;
	}

	public QueryableTemplate setDefinedName(final String name) {
		this.hints.put(DefaultQueryableHints.DEFINED_NAME, name);
		return this;
	}

	public QueryableTemplate setTargetClass(final Class<? extends SQLQueryable<?>> queryableClass) {
		this.hints.put(DefaultQueryableHints.TARGET_CLASS, queryableClass);
		return this;
	}

	public QueryableTemplate set(final String key, final String name) {
		this.hints.put(key, name);
		return this;
	}

	@Override
	public Map<String, Object> toMap() {
		return hints;
	}

}
