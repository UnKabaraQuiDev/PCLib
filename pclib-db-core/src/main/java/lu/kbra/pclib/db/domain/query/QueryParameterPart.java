package lu.kbra.pclib.db.domain.query;

import java.util.Map;

import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.domain.Qualified;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.HintsOwner;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public final class QueryParameterPart implements HintsOwner {

	private final int index;
	private final String parameterName;
	private final @Nullable @Qualified String column;
	private final String comparator;
	private final boolean inverted;
	private final boolean ignoreNull;
	private final boolean nullable;
	private final boolean limit;
	private final boolean offset;
	/**
	 * if the stored type is a Collection
	 */
	private final boolean list;
	/**
	 * only used for lists
	 */
	private final @Qualified String[] columns;
	/**
	 * if the stored type is an entry
	 */
	private final boolean entry;
	private final ColumnType<?, ?> type;
	private boolean includeInCondition = true;
	private final Map<String, Object> hints;

}
