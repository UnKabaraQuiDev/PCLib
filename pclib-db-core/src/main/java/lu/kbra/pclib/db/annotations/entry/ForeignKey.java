package lu.kbra.pclib.db.annotations.entry;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.table.ForeignKeyData.OnAction;
import lu.kbra.pclib.db.impl.SQLQueryable;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface ForeignKey {

	public enum DeferMode {

		NOT_DEFERRABLE,
		INITIALLY_IMMEDIATE,
		INITIALLY_DEFERRED;

	}

	@ColumnHint(type = DefaultColumnHints.FOREIGN_KEY_COLUMN)
	String column() default "";

	@ColumnHint(type = DefaultColumnHints.FOREIGN_KEY_GROUP_ID)
	int groupId() default 0;

	@ColumnHint(type = DefaultColumnHints.FOREIGN_KEY_ON_DELETE)
	OnAction onDelete() default OnAction.NO_ACTION;

	@ColumnHint(type = DefaultColumnHints.FOREIGN_KEY_ON_UPDATE)
	OnAction onUpdate() default OnAction.NO_ACTION;

	@ColumnHint(type = DefaultColumnHints.FOREIGN_KEY_TABLE_NAME)
	String tableName() default "";

	@ColumnHint(type = DefaultColumnHints.FOREIGN_KEY_TABLE)
	Class<? extends SQLQueryable<?>> table();

	@ColumnHint(type = DefaultColumnHints.FOREIGN_DEFER_MODE)
	DeferMode mode() default DeferMode.INITIALLY_DEFERRED;

}
