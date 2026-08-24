package lu.kbra.pclib.db.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

import org.springframework.beans.factory.BeanFactory;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.utils.DatabaseRuleChainTemplate.RuleAction.AddAfterRule;
import lu.kbra.pclib.db.utils.DatabaseRuleChainTemplate.RuleAction.AddBeforeRule;
import lu.kbra.pclib.db.utils.DatabaseRuleChainTemplate.RuleAction.AddRule;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule;

public class DatabaseRuleChainTemplate {

	private final List<RuleAction> actions = new ArrayList<>();
	private final List<BiPredicate<String, String>> matchers = new ArrayList<>();

	public void tryApply(final DatabaseEntryUtils utils, final String connectorQualifier, final BeanFactory beanFactory) {
		if (this.matches(utils.getDbmsQualifierName(), connectorQualifier)) {
			utils.setQueryableHookManager(this.build(beanFactory));
		}
	}

	public DatabaseRuleChainTemplate add(final SQLQueryableRule rule) {
		this.actions.add(new RuleAction.AddRule(new RuleReference.RuleInstance(rule)));
		return this;
	}

	public DatabaseRuleChainTemplate add(final Class<? extends SQLQueryableRule> type) {
		this.actions.add(new RuleAction.AddRule(new RuleReference.RuleClass(type)));
		return this;
	}

	public DatabaseRuleChainTemplate add(final String beanName) {
		this.actions.add(new RuleAction.AddRule(new RuleReference.RuleName(beanName)));
		return this;
	}

	public DatabaseRuleChainTemplate addBefore(final Class<? extends SQLQueryableRule> anchor, final SQLQueryableRule rule) {
		this.actions.add(new RuleAction.AddBeforeRule(anchor, new RuleReference.RuleInstance(rule)));
		return this;
	}

	public DatabaseRuleChainTemplate
			addBefore(final Class<? extends SQLQueryableRule> anchor, final Class<? extends SQLQueryableRule> ruleType) {
		this.actions.add(new RuleAction.AddBeforeRule(anchor, new RuleReference.RuleClass(ruleType)));
		return this;
	}

	public DatabaseRuleChainTemplate addBefore(final Class<? extends SQLQueryableRule> anchor, final String beanName) {
		this.actions.add(new RuleAction.AddBeforeRule(anchor, new RuleReference.RuleName(beanName)));
		return this;
	}

	public DatabaseRuleChainTemplate addAfter(final Class<? extends SQLQueryableRule> anchor, final SQLQueryableRule rule) {
		this.actions.add(new RuleAction.AddAfterRule(anchor, new RuleReference.RuleInstance(rule)));
		return this;
	}

	public DatabaseRuleChainTemplate
			addAfter(final Class<? extends SQLQueryableRule> anchor, final Class<? extends SQLQueryableRule> ruleType) {
		this.actions.add(new RuleAction.AddAfterRule(anchor, new RuleReference.RuleClass(ruleType)));
		return this;
	}

	public DatabaseRuleChainTemplate addAfter(final Class<? extends SQLQueryableRule> anchor, final String beanName) {
		this.actions.add(new RuleAction.AddAfterRule(anchor, new RuleReference.RuleName(beanName)));
		return this;
	}

	public DatabaseRuleChainTemplate match(final String dbmsPattern, final String connectorPattern) {
		final Pattern dbms = Pattern.compile(PCUtils.globToRegex(dbmsPattern));
		final Pattern connector = Pattern.compile(PCUtils.globToRegex(connectorPattern));

		this.matchers.add((dbmsQualifier, connectorQualifier) -> dbms.matcher(dbmsQualifier).matches()
				&& connector.matcher(connectorQualifier).matches());

		return this;
	}

	protected DatabaseRuleChainTemplate matchAny(final String dbmsPattern, final String connectorPattern) {
		final Pattern dbms = Pattern.compile(PCUtils.globToRegex(dbmsPattern));
		final Pattern connector = Pattern.compile(PCUtils.globToRegex(connectorPattern));

		this.matchers.add((dbmsQualifier, connectorQualifier) -> dbms.matcher(dbmsQualifier).matches()
				|| connector.matcher(connectorQualifier).matches());

		return this;
	}

	public DatabaseRuleChainTemplate matchDbms(final String pattern) {
		final Pattern regex = Pattern.compile(PCUtils.globToRegex(pattern));

		this.matchers.add((dbmsQualifier, connectorQualifier) -> regex.matcher(dbmsQualifier).matches());

		return this;
	}

	public DatabaseRuleChainTemplate matchConnector(final String pattern) {
		final Pattern regex = Pattern.compile(PCUtils.globToRegex(pattern));

		this.matchers.add((dbmsQualifier, connectorQualifier) -> regex.matcher(connectorQualifier).matches());

		return this;
	}

	public SQLQueryableHookManager build(final BeanFactory beanFactory) {
		final SQLQueryableHookManager manager = new SQLQueryableHookManager();

		for (final RuleAction action : this.actions) {
			action.apply(manager, beanFactory);
		}

		return manager;
	}

	public boolean matches(final String dbmsQualifierName, final String connectorQualifier) {
		if (this.matchers.isEmpty()) {
			return true;
		}

		for (final BiPredicate<String, String> matcher : this.matchers) {
			if (!matcher.test(dbmsQualifierName, connectorQualifier)) {
				return false;
			}
		}

		return true;
	}

	public static class DefaultDbRuleChainTemplate extends DatabaseRuleChainTemplate {

		@Override
		public boolean matches(final String dbmsQualifierName, final String connectorQualifier) {
			return true;
		}
	}

	sealed interface RuleAction permits AddRule, AddBeforeRule, AddAfterRule {

		void apply(SQLQueryableHookManager manager, BeanFactory beanFactory);

		record AddRule(RuleReference rule) implements RuleAction {
			@Override
			public void apply(SQLQueryableHookManager manager, BeanFactory beanFactory) {
				manager.add(rule.resolve(beanFactory));
			}
		}

		record AddBeforeRule(Class<? extends SQLQueryableRule> anchor, RuleReference rule) implements RuleAction {

			@Override
			public void apply(SQLQueryableHookManager manager, BeanFactory beanFactory) {
				manager.addBefore(anchor, rule.resolve(beanFactory));
			}
		}

		record AddAfterRule(Class<? extends SQLQueryableRule> anchor, RuleReference rule) implements RuleAction {

			@Override
			public void apply(SQLQueryableHookManager manager, BeanFactory beanFactory) {
				manager.addAfter(anchor, rule.resolve(beanFactory));
			}
		}

	}

	public interface RuleReference {

		SQLQueryableRule resolve(BeanFactory beanFactory);

		public record RuleInstance(SQLQueryableRule rule) implements RuleReference {

			@Override
			public SQLQueryableRule resolve(final BeanFactory beanFactory) {
				return this.rule;
			}
		}

		public record RuleClass(Class<? extends SQLQueryableRule> type) implements RuleReference {

			@Override
			public SQLQueryableRule resolve(final BeanFactory beanFactory) {
				return beanFactory.getBean(this.type);
			}
		}

		public record RuleName(String beanName) implements RuleReference {

			@Override
			public SQLQueryableRule resolve(final BeanFactory beanFactory) {
				return beanFactory.getBean(this.beanName, SQLQueryableRule.class);
			}
		}

	}

}
