package lu.kbra.pclib.db.utils.impl;

import java.sql.Connection;
import java.sql.Statement;

import lu.kbra.pclib.db.impl.SQLQueryable;

public interface SQLQueryableRule {

	boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable);

	default boolean shouldRunPrepare() {
		return false;
	}

	default boolean shouldRunBefore() {
		return false;
	}

	default boolean shouldRunDuring() {
		return false;
	}

	default boolean shouldRunAfter() {
		return false;
	}

	public static interface InsertRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isInsert();
		}
	}

	public static interface LoadRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isLoad();
		}
	}

	public static interface UpdateRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isUpdate();
		}
	}

	public static interface QueryRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isQuery();
		}
	}

	public static interface TruncateRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isTruncate();
		}
	}

	public static interface ClearRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isClear();
		}
	}

	public static interface CountRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isCount();
		}
	}

	public static interface CreateRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isCreate();
		}
	}

	public static interface DeleteRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isDelete();
		}
	}

	public static interface DropRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isDrop();
		}
	}

	public static interface ExistsRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isExists();
		}
	}

	public static interface PrepareRule extends SQLQueryableRule {

		void executePrepare(RuleHookType hookType, SQLQueryable<?> queryable, Connection c, Object data);

		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isPrepare();
		}

		@Override
		default boolean shouldRunPrepare() {
			return true;
		}

	}

	public static interface BeforeRule extends SQLQueryableRule {

		void executeBefore(RuleHookType hookType, SQLQueryable<?> queryable, Statement pstmt, Object data);

		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isBefore();
		}

		@Override
		default boolean shouldRunBefore() {
			return true;
		}

	}

	public static interface DuringRule extends SQLQueryableRule {

		void executeDuring(RuleHookType hookType, SQLQueryable<?> queryable, Statement pstmt, Object data);

		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isDuring();
		}

		@Override
		default boolean shouldRunDuring() {
			return true;
		}

	}

	public static interface AfterRule extends SQLQueryableRule {

		void executeAfter(RuleHookType hookType, SQLQueryable<?> queryable, Statement pstmt, Object data);

		@Override
		default boolean shouldRun(RuleHookType hookType, SQLQueryable<?> queryable) {
			return hookType.isAfter();
		}

		@Override
		default boolean shouldRunAfter() {
			return true;
		}

	}

}
