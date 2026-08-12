package lu.kbra.pclib.db.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.kbra.pclib.datastructure.list.WeakArrayList;
import lu.kbra.pclib.db.domain.table.TreeStringConvertible;
import lu.kbra.pclib.db.hook.RuleHookType;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.AfterRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.BeforeRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.DuringRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.PrepareRule;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class SQLQueryableHookManager implements TreeStringConvertible {

	protected final SQLQueryableHookManager parent;
	protected final WeakArrayList<SQLQueryableHookManager> linkedChildren = new WeakArrayList<>();

	protected final List<SQLQueryableRule> databaseEntryRules;
	protected List<PrepareRule> prepareRules;
	protected List<BeforeRule> beforeRules;
	protected List<DuringRule> duringRules;
	protected List<AfterRule> afterRules;

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
			final List<AfterRule> afterRules) {
		this.parent = parent;
		this.parent.linkChild(this);
		this.databaseEntryRules = databaseEntryRules;
		this.prepareRules = prepareRules;
		this.beforeRules = beforeRules;
		this.duringRules = duringRules;
		this.afterRules = afterRules;
	}

	public final SQLQueryableHookManager add(final SQLQueryableRule rule) {
		this.databaseEntryRules.add(rule);

		// Keep the cache up-to-date if it already exists.
		if (this.prepareRules != null && this.beforeRules != null && this.duringRules != null && this.afterRules != null) {
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
				new ArrayList<>(this.afterRules));
	}

	public SQLQueryableHookManager cloneLinked() {
		return new SQLQueryableHookManager(this, new ArrayList<>());
	}

	public void computeCache() {
		this.prepareRules = new ArrayList<>();
		this.beforeRules = new ArrayList<>();
		this.duringRules = new ArrayList<>();
		this.afterRules = new ArrayList<>();

		if (this.parent != null) {
			this.parent.ensureCache();

			this.prepareRules.addAll(this.parent.getPrepareRules());
			this.beforeRules.addAll(this.parent.getBeforeRules());
			this.duringRules.addAll(this.parent.getDuringRules());
			this.afterRules.addAll(this.parent.getAfterRules());
		}

		for (final SQLQueryableRule rule : this.databaseEntryRules) {
			this.computeCache(rule);
		}
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
	}

	public final void ensureCache() {
		if (this.beforeRules == null || this.duringRules == null || this.afterRules == null || this.prepareRules == null) {
			this.computeCache();
		}
	}

	public void executeAfter(final RuleHookType hookType, final SQLQueryable<?> queryable, final Statement pstmt, final Object data) {
		if (!hookType.isAfter()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		this.getAfterRules().forEach(r -> {
			if (r.shouldRun(hookType, queryable)) {
				r.executeAfter(hookType, queryable, pstmt, data);
			}
		});

	}

	public void executeBefore(final RuleHookType hookType, final SQLQueryable<?> queryable, final Statement pstmt, final Object data) {
		if (!hookType.isBefore()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		this.getBeforeRules().forEach(r -> {
			if (r.shouldRun(hookType, queryable)) {
				r.executeBefore(hookType, queryable, pstmt, data);
			}
		});
	}

	public void
			executeDuring(final RuleHookType hookType, final SQLQueryable<?> queryable, final PreparedStatement pstmt, final Object data) {
		if (!hookType.isDuring()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		this.getDuringRules().forEach(r -> {
			if (r.shouldRun(hookType, queryable)) {
				r.executeDuring(hookType, queryable, pstmt, data);
			}
		});

	}

	public void executePrepare(final RuleHookType hookType, final SQLQueryable<?> queryable, final Connection c, final Object data) {
		if (!hookType.isPrepare()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		this.getPrepareRules().forEach(r -> {
			if (r.shouldRun(hookType, queryable)) {
				r.executePrepare(hookType, queryable, c, data);
			}
		});
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

		map.put("databaseEntryRules", this.databaseEntryRules);
		map.put("prepareRules", this.prepareRules);
		map.put("beforeRules", this.beforeRules);
		map.put("duringRules", this.duringRules);
		map.put("afterRules", this.afterRules);

		return map;
	}

	protected void unlinkChild(final SQLQueryableHookManager child) {
		if (child == null) {
			return;
		}
		this.linkedChildren.remove(child);
	}

}
