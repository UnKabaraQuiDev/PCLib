package lu.kbra.pclib.db.annotations.query;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.annotations.entry.DbmsFilter;
import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.domain.table.DefaultQueryHints;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface Query {

	public static enum Type {
		AUTO,

		FIRST_THROW,
		FIRST_NULL,

		SINGLE_THROW,
		SINGLE_NULL,

		LIST_THROW,
		LIST_NULL,
		LIST_EMPTY;

		Type() {
		}

		public boolean isAuto() {
			return this == AUTO;
		}

		public boolean isEmpty() {
			switch (this) {
			case LIST_EMPTY:
				return true;
			default:
				return false;
			}
		}

		public boolean isFirst() {
			return this == FIRST_THROW || this == FIRST_NULL;
		}

		public boolean isList() {
			return this == LIST_EMPTY || this == LIST_THROW || this == LIST_NULL;
		}

		public boolean isNullable() {
			switch (this) {
			case FIRST_NULL:
			case LIST_NULL:
			case SINGLE_NULL:
				return true;
			default:
				return false;
			}
		}

		public boolean isSingle() {
			return this == SINGLE_THROW || this == SINGLE_NULL;
		}

		public boolean isThrowing() {
			switch (this) {
			case FIRST_THROW:
			case LIST_THROW:
			case SINGLE_THROW:
				return true;
			default:
				return false;
			}
		}
	}

	String OFFSET_KEY = "_offset";
	String LIMIT_KEY = "_limit";

	String TABLE_NAME_KEY = DatabaseEntryUtils.TABLE_NAME_KEY;
	String TABLE_NAME = "{" + Query.TABLE_NAME_KEY + "}";
	String QUALIFIER_KEY = DatabaseEntryUtils.QUALIFIER_KEY;
	String FUNCTION_KEY = DatabaseEntryUtils.FUNCTION_KEY;
	String MEMBER_KEY = DatabaseEntryUtils.MEMBER_KEY;

	@QueryHint(type = DefaultQueryHints.COLUMNS)
	String[] columns() default {};

	@QueryHint(type = DefaultQueryHints.RETURN_COLUMNS)
	String[] retColumns() default { "*" };

	@QueryHint(type = DefaultQueryHints.ORDER_BY)
	OrderBy[] orderBy() default {};

	@QueryHint(type = DefaultQueryHints.STRATEGY)
	Type strategy() default Type.AUTO;

	@QueryHint(type = DefaultQueryHints.CUSTOM_SQL)
	String value() default "";

	@QueryHint(type = DefaultQueryableHints.VIEW_TABLES)
	Table[] tables() default {};

	@QueryHint(type = DefaultQueryHints.DISTINCT)
	boolean distinct() default false;

	@DbmsFilter
	String dbms() default DatabaseEntryUtils.DBMS_FILTER_ALL;

}
