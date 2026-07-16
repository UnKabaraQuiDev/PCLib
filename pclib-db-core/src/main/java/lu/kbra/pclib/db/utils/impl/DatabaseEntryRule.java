package lu.kbra.pclib.db.utils.impl;

import java.sql.ResultSet;
import java.util.Optional;

import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface DatabaseEntryRule {

	boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable);

	public static interface InsertRule extends DatabaseEntryRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isInsert();
		}
	}

	public static interface LoadRule extends DatabaseEntryRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isLoad();
		}
	}

	public static interface UpdateRule extends DatabaseEntryRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isUpdate();
		}
	}

	public static interface QueryRule extends DatabaseEntryRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isQuery();
		}
	}

	public static interface TruncateRule extends DatabaseEntryRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isTruncate();
		}
	}

	public static interface ClearRule extends DatabaseEntryRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isClear();
		}
	}

	public static interface CountRule extends DatabaseEntryRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isCount();
		}
	}

	public static interface CreateRule extends DatabaseEntryRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isCreate();
		}
	}

	public static interface DeleteRule extends DatabaseEntryRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isDelete();
		}
	}

	public static interface DropRule extends DatabaseEntryRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isDrop();
		}
	}

	public static interface ExistsRule extends DatabaseEntryRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isExists();
		}
	}

	public static interface BeforeRule extends DatabaseEntryRule {

		<T extends DatabaseEntry> void during(RuleHookType hookType, T data, SQLQueryable<? extends T> queryable);

		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isBefore();
		}
	}

	public static interface DuringRule extends DatabaseEntryRule {

		<T extends DatabaseEntry> void during(
				RuleHookType hookType,
				T data,
				SQLQueryable<? extends T> queryable,
				Optional<ResultSet> result,
				Optional<ResultSet> generatedKeys);

		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isDuring();
		}
	}

	public static interface AfterRule extends DatabaseEntryRule {

		<T extends DatabaseEntry> void during(RuleHookType hookType, T data, SQLQueryable<? extends T> queryable);

		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isAfter();
		}
	}

}
