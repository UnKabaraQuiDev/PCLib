package lu.kbra.pclib.db.utils.impl;

import java.sql.Statement;

import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.hook.RuleHookType;
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

	default boolean shouldRunError() {
		return false;
	}

	public interface InsertRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isInsert();
		}
	}

	public interface LoadRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isLoad();
		}
	}

	public interface UpdateRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isUpdate();
		}
	}

	public interface QueryRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isQuery();
		}
	}

	public interface TruncateRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isTruncate();
		}
	}

	public interface ClearRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isClear();
		}
	}

	public interface CountRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isCount();
		}
	}

	public interface CreateRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isCreate();
		}
	}

	public interface DeleteRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isDelete();
		}
	}

	public interface DropRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isDrop();
		}
	}

	public interface ExistsRule extends SQLQueryableRule {
		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isExists();
		}
	}

	public interface PrepareRule extends SQLQueryableRule {

		void executePrepare(RuleHookType hookType, SQLQueryable<?> queryable, AbstractConnection c, Object data);

		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isPrepare();
		}

		@Override
		default boolean shouldRunPrepare() {
			return true;
		}

	}

	public interface BeforeRule extends SQLQueryableRule {

		void executeBefore(RuleHookType hookType, SQLQueryable<?> queryable, AbstractConnection c, Statement pstmt, Object data);

		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isBefore();
		}

		@Override
		default boolean shouldRunBefore() {
			return true;
		}

	}

	public interface DuringRule extends SQLQueryableRule {

		void executeDuring(RuleHookType hookType, SQLQueryable<?> queryable, AbstractConnection c, Statement pstmt, Object data);

		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isDuring();
		}

		@Override
		default boolean shouldRunDuring() {
			return true;
		}

	}

	public interface AfterRule extends SQLQueryableRule {

		void executeAfter(RuleHookType hookType, SQLQueryable<?> queryable, AbstractConnection c, Statement pstmt, Object data);

		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isAfter();
		}

		@Override
		default boolean shouldRunAfter() {
			return true;
		}

	}

	public interface ErrorRule extends SQLQueryableRule {

		void executeError(RuleHookType hookType, SQLQueryable<?> queryable, AbstractConnection c, Throwable t, Object data)
				throws Throwable;

		@Override
		default boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
			return hookType.isError();
		}

		@Override
		default boolean shouldRunError() {
			return true;
		}

	}

}
