package lu.kbra.pclib.db.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.DatabaseTable;
import lu.kbra.pclib.db.utils.impl.RuleHookType;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.AfterRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.BeforeRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.DuringRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.PrepareRule;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class SQLQueryableHookManager {

	protected final List<SQLQueryableRule> databaseEntryRules;
	protected List<PrepareRule> prepareRules;
	protected List<BeforeRule> beforeRules;
	protected List<DuringRule> duringRules;
	protected List<AfterRule> afterRules;

	public SQLQueryableHookManager() {
		this.databaseEntryRules = new ArrayList<>();
	}

	public void ensureCache() {
		if (this.beforeRules == null || this.duringRules == null || this.afterRules == null) {
			this.computeCache();
		}
	}

	public void computeCache() {
		this.prepareRules = new ArrayList<>();
		this.beforeRules = new ArrayList<>();
		this.duringRules = new ArrayList<>();
		this.afterRules = new ArrayList<>();

		for (final SQLQueryableRule rule : this.databaseEntryRules) {
			this.computeCache(rule);
		}
	}

	private void computeCache(final SQLQueryableRule rule) {
		if (rule.shouldRunPrepare()) {
			this.prepareRules.add((PrepareRule) rule);
		}
		if (rule.shouldRunBefore()) {
			this.beforeRules.add((BeforeRule) rule);
		}
		if (rule.shouldRunDuring()) {
			this.duringRules.add((DuringRule) rule);
		}
		if (rule.shouldRunAfter()) {
			this.afterRules.add((AfterRule) rule);
		}
	}

	public void add(final SQLQueryableRule rule) {
		this.databaseEntryRules.add(rule);

		// Keep the cache up-to-date if it already exists.
		if (this.prepareRules != null || this.beforeRules != null || this.duringRules != null || this.afterRules != null) {
			this.computeCache(rule);
		}
	}

	public void addBefore(final Class<? extends SQLQueryableRule> type, final SQLQueryableRule rule) {
		for (int i = 0; i < this.databaseEntryRules.size(); i++) {
			if (type.isInstance(this.databaseEntryRules.get(i))) {
				this.databaseEntryRules.add(i, rule);
				this.invalidateCache();
				return;
			}
		}

		throw new IllegalArgumentException("No rule of type " + type.getName() + " found.");
	}

	public void addAfter(final Class<? extends SQLQueryableRule> type, final SQLQueryableRule rule) {
		for (int i = 0; i < this.databaseEntryRules.size(); i++) {
			if (type.isInstance(this.databaseEntryRules.get(i))) {
				this.databaseEntryRules.add(i + 1, rule);
				this.invalidateCache();
				return;
			}
		}

		throw new IllegalArgumentException("No rule of type " + type.getName() + " found.");
	}

	public void invalidateCache() {
		this.prepareRules = null;
		this.beforeRules = null;
		this.duringRules = null;
		this.afterRules = null;
	}

	public void executePrepare(final RuleHookType hookType, final SQLQueryable<?> queryable, final Connection c, final Object data) {
		this.ensureCache();

		if (!hookType.isPrepare()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		this.prepareRules.forEach(r -> {
			if (r.shouldRun(hookType, queryable)) {
				r.executePrepare(hookType, queryable, c, data);
			}
		});
	}

	public void executeBefore(final RuleHookType hookType, final SQLQueryable<?> queryable, final Statement pstmt, final Object data) {
		this.ensureCache();

		if (!hookType.isBefore()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		this.beforeRules.forEach(r -> {
			if (r.shouldRun(hookType, queryable)) {
				r.executeBefore(hookType, queryable, pstmt, data);
			}
		});
	}

	public void
			executeDuring(final RuleHookType hookType, final SQLQueryable<?> queryable, final PreparedStatement pstmt, final Object data) {
		this.ensureCache();

		if (!hookType.isDuring()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		this.duringRules.forEach(r -> {
			if (r.shouldRun(hookType, queryable)) {
				r.executeDuring(hookType, queryable, pstmt, data);
			}
		});

	}

	public void executeAfter(final RuleHookType hookType, final SQLQueryable<?> queryable, final Statement pstmt, final Object data) {
		this.ensureCache();

		if (!hookType.isAfter()) {
			throw new IllegalArgumentException("Invalid hook: " + hookType);
		}

		this.afterRules.forEach(r -> {
			if (r.shouldRun(hookType, queryable)) {
				r.executeAfter(hookType, queryable, pstmt, data);
			}
		});

	}

	@Override
	public SQLQueryableHookManager clone() {
		return new SQLQueryableHookManager(new ArrayList<>(databaseEntryRules),
				new ArrayList<>(prepareRules),
				new ArrayList<>(beforeRules),
				new ArrayList<>(duringRules),
				new ArrayList<>(afterRules));
	}

}
