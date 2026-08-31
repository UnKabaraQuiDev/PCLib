package lu.kbra.pclib.db.domain.query;

import java.util.LinkedHashMap;
import java.util.Map;

import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.domain.Qualified;
import lu.kbra.pclib.db.domain.table.AbstractDBStructure;
import lu.kbra.pclib.db.domain.view.ViewOrderStructure;
import lu.kbra.pclib.db.domain.view.ViewTableStructure;
import lu.kbra.pclib.db.query.ReturnMapping;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class QueryStructure implements AbstractDBStructure {

	private final String sql;
	private final @Qualified String qualifiedName;
	private final @Qualified String alias;
	private final String[] columns;
	private final String[] returnColumns;
	private final ViewTableStructure[] joinTables;
	private final @Nullable String condition;
	private final ViewOrderStructure[] orderBy;
	private final @Nullable String customSQL;
	private final Query.Type strategy;
	private final QueryParameterPart[] parameters;
	private final Map<String, Object> hints;
	private final ReturnMapping returnMapping;
	private final boolean distinct;
	private final boolean limit;
	private final boolean offset;
	private final int[] parameterOrder;

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new LinkedHashMap<>();

		map.put("sql", this.sql);
		map.put("qualifiedName", this.qualifiedName);
		map.put("alias", this.alias);
		map.put("columns", this.columns);
		map.put("returnColumns", this.returnColumns);
		map.put("joinTables", this.joinTables);
		map.put("condition", this.condition);
		map.put("orderBy", this.orderBy);
		map.put("customSQL", this.customSQL);
		map.put("strategy", this.strategy);
		map.put("parameters", this.parameters);
		map.put("hints", this.hints);
		map.put("returnMapping", this.returnMapping);
		map.put("distinct", this.distinct);
		map.put("limit", this.limit);
		map.put("offset", this.offset);
		map.put("parameterOrder", this.parameterOrder);

		return map;
	}

}
