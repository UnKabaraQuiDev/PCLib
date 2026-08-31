package lu.kbra.pclib.db.utils;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.list.WeakArrayList;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.domain.table.TreeStringConvertible;
import lu.kbra.pclib.db.hook.RuleHookType;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.AfterRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.BeforeRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.DuringRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.ErrorRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.PrepareRule;

public class SQLQueryableHookManager implements TreeStringConvertible {

	protected SQLQueryableHookManager parent;
	protected final WeakArrayList<SQLQueryableHookManager> linkedChildren = new WeakArrayList<>();

	protected final List<SQLQueryableRule> databaseEntryRules;
	protected List<PrepareRule> prepareRules;
	protected List<BeforeRule> beforeRules;
	protected List<DuringRule> duringRules;
	protected List<AfterRule> afterRules;
	protected List<ErrorRule> errorRules;

	public SQLQueryableHookManager() {
		this.parent = null;
		this.databaseEntryRules = new ArrayList<>();
	}

	protected SQLQueryableHookManager(final SQLQueryableHookManager parent) {
		this.parent = parent;
		this.parent.linkChild(this);
		this.databaseEntryRules = new ArrayList<>();
	}

	public SQLQueryableHookManager(final SQLQueryableHookManager parent, final List<SQLQueryableRule> databaseEntryRules) {
		this.parent = parent;
		this.parent.linkChild(this);
		this.databaseEntryRules = databaseEntryRules;
	}

	public SQLQueryableHookManager(
			final SQLQueryableHookManager parent,
			final List<SQLQueryableRule> databaseEntryRules,
			final List<PrepareRule> prepareRules,
			final List<BeforeRule> beforeRules,
			final List<DuringRule> duringRules,
			final List<AfterRule> afterRules,
			final List<ErrorRule> errorRules) {
		this.parent = parent;
		this.parent.linkChild(this);
		this.databaseEntryRules = databaseEntryRules;
		this.prepareRules = prepareRules;
		this.beforeRules = beforeRules;
		this.duringRules = duringRules;
		this.afterRules = afterRules;
		this.errorRules = errorRules;
	}

	public final SQLQueryableHookManager add(final SQLQueryableRule rule) {
		this.databaseEntryRules.add(rule);

		// Keep the cache up-to-date if it already exists.
		if (this.prepareRules != null && this.beforeRules != null && this.duringRules != null && this.afterRules != null
				&& this.errorRules != null) {
			this.computeCache(rule);
		}

		return this;
	}

	public final SQLQueryableHookManager addAfter(final Class<? extends SQLQueryableRule> type, final SQLQueryableRule rule) {
		for (int i = 0; i < this.databaseEntryRules.size(); i++) {
			if (type.isInstance(this.databaseEntryRules.get(i))) {
				this.databaseEntryRules.add(i + 1, rule);
				this.invalidateCache();
				return this;
			}
		}

		throw new IllegalArgumentException("No rule of type " + type.getName() + " found.");
	}

	protected void addAfterRule(final AfterRule rule) {
		this.afterRules.add(rule);
	}

	public final SQLQueryableHookManager addBefore(final Class<? extends SQLQueryableRule> type, final SQLQueryableRule rule) {
		for (int i = 0; i < this.databaseEntryRules.size(); i++) {
			if (type.isInstance(this.databaseEntryRules.get(i))) {
				this.databaseEntryRules.add(i, rule);
				this.invalidateCache();
				return this;
			}
		}

		throw new IllegalArgumentException("No rule of type " + type.getName() + " found.");
	}

	protected void addBeforeRule(final BeforeRule rule) {
		this.beforeRules.add(rule);
	}

	protected void addDuringRule(final DuringRule rule) {
		this.duringRules.add(rule);
	}

	protected void addErrorRule(final ErrorRule rule) {
		this.errorRules.add(rule);
	}

	protected void addPrepareRule(final PrepareRule rule) {
		this.prepareRules.add(rule);
	}

	@Override
	public SQLQueryableHookManager clone() {
		return new SQLQueryableHookManager(this.parent,
				new ArrayList<>(this.databaseEntryRules),
				new ArrayList<>(this.prepareRules),
				new ArrayList<>(this.beforeRules),
				new ArrayList<>(this.duringRules),
				new ArrayList<>(this.afterRules),
				new ArrayList<>(this.errorRules));
	}

	public SQLQueryableHookManager cloneLinked() {
		return new SQLQueryableHookManager(this, new ArrayList<>());
	}

	public void computeCache() {
		this.prepareRules = new ArrayList<>();
		this.beforeRules = new ArrayList<>();
		this.duringRules = new ArrayList<>();
		this.afterRules = new ArrayList<>();
		this.errorRules = new ArrayList<>();

		if (this.parent != null) {
			this.parent.ensureCache();

			this.prepareRules.addAll(this.parent.getPrepareRules());
			this.beforeRules.addAll(this.parent.getBeforeRules());
			this.duringRules.addAll(this.parent.getDuringRules());
			this.afterRules.addAll(this.parent.getAfterRules());
			this.errorRules.addAll(this.parent.getErrorRules());
		}

		for (final SQLQueryableRule rule : this.databaseEntryRules) {
			this.computeCache(rule);
		}

		Collections.reverse(afterRules);
		Collections.reverse(errorRules);
	}

	protected void computeCache(final SQLQueryableRule rule) {
		if (rule.shouldRunPrepare()) {
			this.addPrepareRule((PrepareRule) rule);
		}
		if (rule.shouldRunBefore()) {
			this.addBeforeRule((BeforeRule) rule);
		}
		if (rule.shouldRunDuring()) {
			this.addDuringRule((DuringRule) rule);
		}
		if (rule.shouldRunAfter()) {
			this.addAfterRule((AfterRule) rule);
		}
		if (rule.shouldRunError()) {
			this.addErrorRule((ErrorRule) rule);
		}
	}

	public final void ensureCache() {
		if (this.beforeRules == null || this.duringRules == null || this.afterRules == null || this.prepareRules == null
				|| this.errorRules == null) {
			this.computeCache();
		}
	}

	public void executeAfter(
			final RuleHookType hookType,
			final SQLQueryable<?> queryable,
			final AbstractConnection c,
			final Statement pstmt,
			final Object data) {
		if (!hookType.isAfter()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		this.getAfterRules()
				.stream()
				.filter(r -> r.shouldRun(hookType, queryable))
				.forEach(r -> r.executeAfter(hookType, queryable, c, pstmt, data));
	}

	public void executeBefore(
			final RuleHookType hookType,
			final SQLQueryable<?> queryable,
			final AbstractConnection c,
			final Statement pstmt,
			final Object data) {
		if (!hookType.isBefore()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		this.getBeforeRules()
				.stream()
				.filter(r -> r.shouldRun(hookType, queryable))
				.forEach(r -> r.executeBefore(hookType, queryable, c, pstmt, data));
	}

	public void executeDuring(
			final RuleHookType hookType,
			final SQLQueryable<?> queryable,
			final AbstractConnection c,
			final Statement pstmt,
			final Object data) {
		if (!hookType.isDuring()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		this.getDuringRules()
				.stream()
				.filter(r -> r.shouldRun(hookType, queryable))
				.forEach(r -> r.executeDuring(hookType, queryable, c, pstmt, data));
	}

	public List<Throwable> executeError(
			final RuleHookType hookType,
			final SQLQueryable<?> queryable,
			final AbstractConnection c,
			final Throwable t,
			final Object data) {
		if (!hookType.isError()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		List<Throwable> e = null;
		for (final ErrorRule r : this.getErrorRules()) {
			try {
				if (r.shouldRun(hookType, queryable)) {
					r.executeError(hookType, queryable, c, t, data);
				}
			} catch (final Throwable t2) {
				if (e == null) {
					e = new ArrayList<>();
				}
				e.add(t2);
			}
		}

		return e;
	}

	public void
			executePrepare(final RuleHookType hookType, final SQLQueryable<?> queryable, final AbstractConnection c, final Object data) {
		if (!hookType.isPrepare()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		this.getPrepareRules()
				.stream()
				.filter(r -> r.shouldRun(hookType, queryable))
				.forEach(r -> r.executePrepare(hookType, queryable, c, data));
	}

	protected List<AfterRule> getAfterRules() {
		this.ensureCache();
		return this.afterRules;
	}

	protected List<BeforeRule> getBeforeRules() {
		this.ensureCache();
		return this.beforeRules;
	}

	protected List<SQLQueryableRule> getDatabaseEntryRules() {
		return this.databaseEntryRules;
	}

	protected List<DuringRule> getDuringRules() {
		this.ensureCache();
		return this.duringRules;
	}

	protected List<ErrorRule> getErrorRules() {
		this.ensureCache();
		return this.errorRules;
	}

	protected List<PrepareRule> getPrepareRules() {
		this.ensureCache();
		return this.prepareRules;
	}

	public final SQLQueryableHookManager invalidateCache() {
		this.prepareRules = null;
		this.beforeRules = null;
		this.duringRules = null;
		this.afterRules = null;

		for (final SQLQueryableHookManager child : this.linkedChildren) {
			child.invalidateCache();
		}

		return this;
	}

	protected void linkChild(final SQLQueryableHookManager child) {
		if (child == null) {
			return;
		}
		this.linkedChildren.add(child);
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();

		map.put("parent", PCUtils.toSimpleIdentityString(parent));
		map.put("databaseEntryRules", this.databaseEntryRules);
		map.put("prepareRules", this.prepareRules);
		map.put("beforeRules", this.beforeRules);
		map.put("duringRules", this.duringRules);
		map.put("afterRules", this.afterRules);
		map.put("errorRules", this.errorRules);

		return map;
	}

	public void unlink() {
		if (this.parent == null) {
			return;
		}
		this.parent.unlinkChild(this);
		this.parent = null;
	}

	protected void unlinkChild(final SQLQueryableHookManager child) {
		if (child == null) {
			return;
		}
		this.linkedChildren.remove(child);
	}

	@Override
	public String toString() {
		return "SQLQueryableHookManager [parent=" + PCUtils.toSimpleIdentityString(parent) + ", linkedChildren=" + linkedChildren
				+ ", databaseEntryRules=" + databaseEntryRules + ", prepareRules=" + prepareRules + ", beforeRules=" + beforeRules
				+ ", duringRules=" + duringRules + ", afterRules=" + afterRules + ", errorRules=" + errorRules + "]";
	}

}
