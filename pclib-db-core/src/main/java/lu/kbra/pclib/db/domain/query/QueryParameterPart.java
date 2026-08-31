package lu.kbra.pclib.db.domain.query;

import lu.kbra.pclib.db.domain.Qualified;
import lu.kbra.pclib.db.domain.column.type.ColumnType;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public final class QueryParameterPart {

	private final int index;
	private final String parameterName;
	private final @Qualified String column;
	private final String comparator;
	private final boolean ignoreNull;
	private final boolean limit;
	private final boolean offset;
	private final ColumnType<?, ?> type;

}
