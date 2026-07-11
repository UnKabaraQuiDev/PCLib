package lu.kbra.pclib.datastructure.tree.dependency;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

public final class DependencyResolver<ITEM, KEY> {

	private enum State {
		VISITING,
		VISITED
	}

	public static <ITEM extends DependencyOwner<KEY>, KEY> DependencyResolver<ITEM, KEY> of(final Collection<? extends ITEM> items) {
		return new DependencyResolver<>(items, DependencyOwner::getDependencies, DependencyOwner::getKey);
	}

	private final Map<KEY, ITEM> itemsByKey;
	private final Function<ITEM, Set<KEY>> dependenciesSupplier;
	private final Function<ITEM, KEY> keySupplier;

	public DependencyResolver(
			final Collection<? extends ITEM> items,
			final Function<ITEM, Set<KEY>> dependenciesSupplier,
			final Function<ITEM, KEY> keySupplier) {
		this.dependenciesSupplier = Objects.requireNonNull(dependenciesSupplier, "dependenciesSupplier");
		this.keySupplier = Objects.requireNonNull(keySupplier, "keySupplier");
		Objects.requireNonNull(items, "items");

		this.itemsByKey = new HashMap<>();

		for (final ITEM item : items) {
			final KEY key = Objects.requireNonNull(this.keySupplier.apply(item), "key must not be null");
			final ITEM previous = this.itemsByKey.putIfAbsent(key, item);
			if (previous != null) {
				throw new IllegalArgumentException("Duplicate key: " + key);
			}
		}
	}

	public DependencyTree<ITEM, KEY> getTree() {
		this.validate();

		final Map<KEY, Set<KEY>> dependentsByKey = new HashMap<>();
		for (final KEY key : this.itemsByKey.keySet()) {
			dependentsByKey.put(key, new LinkedHashSet<>());
		}

		for (final Map.Entry<KEY, ITEM> entry : this.itemsByKey.entrySet()) {
			final KEY ownerKey = entry.getKey();
			final Set<KEY> dependencies = this.dependenciesSupplier.apply(entry.getValue());

			if (dependencies == null) {
				continue;
			}

			for (final KEY dependencyKey : dependencies) {
				dependentsByKey.get(dependencyKey).add(ownerKey);
			}
		}

		final List<KEY> roots = new ArrayList<>();
		for (final Map.Entry<KEY, Set<KEY>> entry : dependentsByKey.entrySet()) {
			if (entry.getValue().isEmpty()) {
				roots.add(entry.getKey());
			}
		}

		roots.sort(Comparator.comparing(String::valueOf));

		return new DependencyTree<>(this.itemsByKey, dependentsByKey, roots);
	}

	public List<ITEM> resolve() {
		return this.resolve((ownerKey, dependencyKey) -> false);
	}

	public List<ITEM> resolve(final BiPredicate<KEY, KEY> optionalDependency) {
		Objects.requireNonNull(optionalDependency, "optionalDependency");

		final Map<KEY, State> state = new HashMap<>();
		final List<ITEM> result = new ArrayList<>();

		for (final KEY key : this.itemsByKey.keySet()) {
			if (state.get(key) == null) {
				this.dfs(key, state, result, new ArrayDeque<>(), optionalDependency);
			}
		}

		return result;
	}

	public List<ITEM> resolve(final boolean optionalDependencies) {
		return this.resolve((ownerKey, dependencyKey) -> optionalDependencies);
	}

	private String buildCycle(final KEY start, final Deque<KEY> stack) {
		final StringBuilder sb = new StringBuilder();

		for (final KEY entry : stack) {
			sb.append(entry).append(" -> ");
			if (Objects.equals(entry, start)) {
				break;
			}
		}

		sb.append(start);
		return sb.toString();
	}

	private void dfs(
			final KEY key,
			final Map<KEY, State> state,
			final List<ITEM> out,
			final Deque<KEY> stack,
			final BiPredicate<KEY, KEY> optionalDependency) {
		final ITEM item = this.itemsByKey.get(key);
		if (item == null) {
			throw new IllegalStateException("Missing dependency: " + key);
		}

		state.put(key, State.VISITING);
		stack.push(key);

		final Set<KEY> dependencies = this.dependenciesSupplier.apply(item);
		if (dependencies != null) {
			for (final KEY dependencyKey : dependencies) {
				if (!this.itemsByKey.containsKey(dependencyKey)) {
					if (optionalDependency.test(key, dependencyKey)) {
						continue;
					}
					throw new IllegalStateException("Missing dependency: " + dependencyKey + " required by " + key);
				}

				final State dependencyState = state.get(dependencyKey);

				if (dependencyState == State.VISITING) {
					throw new IllegalStateException("Dependency cycle: " + this.buildCycle(dependencyKey, stack));
				}

				if (dependencyState == null) {
					this.dfs(dependencyKey, state, out, stack, optionalDependency);
				}
			}
		}

		stack.pop();
		state.put(key, State.VISITED);
		out.add(item);
	}

	private void validate() {
		final Map<KEY, State> state = new HashMap<>();

		for (final KEY key : this.itemsByKey.keySet()) {
			if (state.get(key) == null) {
				this.validateDfs(key, state, new ArrayDeque<>());
			}
		}
	}

	private void validateDfs(final KEY key, final Map<KEY, State> state, final Deque<KEY> stack) {
		final ITEM item = this.itemsByKey.get(key);
		if (item == null) {
			throw new IllegalStateException("Missing dependency: " + key);
		}

		state.put(key, State.VISITING);
		stack.push(key);

		final Set<KEY> dependencies = this.dependenciesSupplier.apply(item);
		if (dependencies != null) {
			for (final KEY dependencyKey : dependencies) {
				if (!this.itemsByKey.containsKey(dependencyKey)) {
					throw new IllegalStateException("Missing dependency: " + dependencyKey + " required by " + key);
				}

				final State dependencyState = state.get(dependencyKey);
				if (dependencyState == State.VISITING) {
					throw new IllegalStateException("Dependency cycle: " + this.buildCycle(dependencyKey, stack));
				}

				if (dependencyState == null) {
					this.validateDfs(dependencyKey, state, stack);
				}
			}
		}

		stack.pop();
		state.put(key, State.VISITED);
	}

}
