package lu.kbra.pclib.db.utils;

import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.domain.Qualified;
import lu.kbra.pclib.db.impl.SQLQueryable;

import lombok.Data;

@Data
public class QueryTableStructure {

	private final Table.Type joinType;
	private final Class<? extends SQLQueryable<?>> type;
	private final String name;
	private final @Qualified String asName;
	private final String on;

	public boolean isMain() {
		return this.joinType == Table.Type.MAIN || this.joinType == Table.Type.MAIN_UNION || this.joinType == Table.Type.MAIN_UNION_ALL;
	}

}
