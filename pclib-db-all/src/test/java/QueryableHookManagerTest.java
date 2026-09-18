import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.hook.VersionRule;
import lu.kbra.pclib.db.utils.SQLQueryableHookManager;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.AfterRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.BeforeRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.DuringRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.ErrorRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.PrepareRule;

import shared.CaptureRule;
import shared.PrintRule;

public class QueryableHookManagerTest {

	public class MockSQLQueryableHookManager extends SQLQueryableHookManager {

		public MockSQLQueryableHookManager() {
		}

		public MockSQLQueryableHookManager(
				final SQLQueryableHookManager parent,
				final List<SQLQueryableRule> databaseEntryRules,
				final List<PrepareRule> prepareRules,
				final List<BeforeRule> beforeRules,
				final List<DuringRule> duringRules,
				final List<AfterRule> afterRules,
				final List<ErrorRule> errorRules) {
			super(parent, databaseEntryRules, prepareRules, beforeRules, duringRules, afterRules, errorRules);
		}

		public MockSQLQueryableHookManager(final SQLQueryableHookManager parent, final List<SQLQueryableRule> databaseEntryRules) {
			super(parent, databaseEntryRules);
		}

		public MockSQLQueryableHookManager(final SQLQueryableHookManager parent) {
			super(parent);
		}

		public List<AfterRule> getAfterRulesDirect() {
			return super.afterRules;
		}

		public List<BeforeRule> getBeforeRulesDirect() {
			return super.beforeRules;
		}

		public List<DuringRule> getDuringRulesDirect() {
			return super.duringRules;
		}

		public List<ErrorRule> getErrorRulesDirect() {
			return super.errorRules;
		}

		public List<PrepareRule> getPrepareRulesDirect() {
			return super.prepareRules;
		}

		@Override
		public List<AfterRule> getAfterRules() {
			return super.getAfterRules();
		}

		@Override
		public List<BeforeRule> getBeforeRules() {
			return super.getBeforeRules();
		}

		@Override
		public List<DuringRule> getDuringRules() {
			return super.getDuringRules();
		}

		@Override
		public List<ErrorRule> getErrorRules() {
			return super.getErrorRules();
		}

		@Override
		public List<PrepareRule> getPrepareRules() {
			return super.getPrepareRules();
		}

		@Override
		public MockSQLQueryableHookManager cloneLinked() {
			return new MockSQLQueryableHookManager(this, new ArrayList<>());
		}

	}

	@Test
	public void testParentChildPropagation() {
		final MockSQLQueryableHookManager parent = new MockSQLQueryableHookManager();
		final MockSQLQueryableHookManager child1 = parent.cloneLinked();

		parent.add(new CaptureRule());

		Assert.assertEquals(1, parent.getBeforeRules().size());
		Assert.assertEquals(1, child1.getBeforeRules().size());
		Assert.assertTrue(child1.getDatabaseEntryRules().isEmpty());

		parent.add(new VersionRule());

		// should've propagated VersionRule to the child
		Assert.assertEquals(1, parent.getBeforeRules().size());
		Assert.assertEquals(1, child1.getBeforeRules().size());
		Assert.assertEquals(1, parent.getPrepareRules().size());
		Assert.assertEquals(1, child1.getPrepareRules().size());
		Assert.assertEquals(0, child1.getDatabaseEntryRules().size());

		parent.invalidateCache();

		// this should've invalidated all caches
		Assert.assertNull(parent.getBeforeRulesDirect());
		Assert.assertNull(child1.getBeforeRulesDirect());
		Assert.assertNull(parent.getPrepareRulesDirect());
		Assert.assertNull(child1.getPrepareRulesDirect());

		parent.computeCache();

		// this should've regenerated all caches
		Assert.assertEquals(1, parent.getBeforeRulesDirect().size());
		Assert.assertEquals(1, child1.getBeforeRulesDirect().size());
		Assert.assertEquals(1, parent.getPrepareRulesDirect().size());
		Assert.assertEquals(1, child1.getPrepareRulesDirect().size());
		Assert.assertEquals(0, child1.getDatabaseEntryRules().size());

		final PrintRule printRule;
		child1.add(printRule = new PrintRule());

		// this shouldn't have affected the parent
		Assert.assertEquals(1, parent.getBeforeRulesDirect().size());
		Assert.assertEquals(2, child1.getBeforeRulesDirect().size());
		Assert.assertEquals(1, parent.getPrepareRulesDirect().size());
		Assert.assertEquals(1, child1.getPrepareRulesDirect().size());
		Assert.assertEquals(1, child1.getDatabaseEntryRules().size());

		child1.remove(printRule);

		// this should've directly affected the cache
		Assert.assertEquals(1, parent.getBeforeRulesDirect().size());
		Assert.assertEquals(1, child1.getBeforeRulesDirect().size());
		Assert.assertEquals(1, parent.getPrepareRulesDirect().size());
		Assert.assertEquals(1, child1.getPrepareRulesDirect().size());
		Assert.assertEquals(0, child1.getDatabaseEntryRules().size());

		child1.invalidateCache();
		child1.add(printRule);

		// this shouldn't have affected the parent
		Assert.assertNull(parent.getBeforeRulesDirect());
		Assert.assertNull(child1.getBeforeRulesDirect());
		Assert.assertNull(parent.getPrepareRulesDirect());
		Assert.assertNull(child1.getPrepareRulesDirect());
		Assert.assertEquals(1, child1.getDatabaseEntryRules().size());

		child1.computeCache();

		// this should've affected the parent
		Assert.assertEquals(1, parent.getBeforeRulesDirect().size());
		Assert.assertEquals(2, child1.getBeforeRulesDirect().size());
		Assert.assertEquals(1, parent.getPrepareRulesDirect().size());
		Assert.assertEquals(1, child1.getPrepareRulesDirect().size());
		Assert.assertEquals(1, child1.getDatabaseEntryRules().size());
	}

}
