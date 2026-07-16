package lu.kbra.pclib.db.utils;

import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.db.utils.impl.DatabaseEntryRule;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class EntryHookManager {

	protected List<DatabaseEntryRule> databaseEntryRules = new ArrayList<>();

	public void addRule(final DatabaseEntryRule rule) {
		this.databaseEntryRules.add(rule);
	}

	public void addRuleAfter(final Class<? extends DatabaseEntryRule> clazz, final DatabaseEntryRule rule) {
		for (int i = 0; i < this.databaseEntryRules.size(); i++) {
			if (clazz.isInstance(this.databaseEntryRules.get(i))) {
				this.databaseEntryRules.add(i + 1, rule);
				return;
			}
		}

		// No matching rule found
		this.databaseEntryRules.add(rule);
	}

	public void addRuleBefore(final Class<? extends DatabaseEntryRule> clazz, final DatabaseEntryRule rule) {
		for (int i = 0; i < this.databaseEntryRules.size(); i++) {
			if (clazz.isInstance(this.databaseEntryRules.get(i))) {
				this.databaseEntryRules.add(i, rule);
				return;
			}
		}

		// No matching rule found
		this.databaseEntryRules.add(rule);
	}

}
