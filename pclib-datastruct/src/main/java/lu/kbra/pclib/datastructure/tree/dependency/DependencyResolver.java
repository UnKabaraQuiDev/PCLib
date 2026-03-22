package lu.kbra.pclib.datastructure.tree.dependency;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public final class DependencyResolver<T, V> {

	private final Map<V, T> itemsByKey;
	private final Function<T, Set<V>> dependenciesSupplier;
	private final Function<T, V> keySupplier;

	public DependencyResolver(final Collection<? extends T> items, final Function<T, Set<V>> dependenciesSupplier,
			final Function<T, V> keySupplier) {
		this.dependenciesSupplier = Objects.requireNonNull(dependenciesSupplier, "dependenciesSupplier");
		this.keySupplier = Objects.requireNonNull(keySupplier, "keySupplier");
		Objects.requireNonNull(items, "items");

		this.itemsByKey = new HashMap<>();

		for (final T item : items) {
			final V key = Objects.requireNonNull(this.keySupplier.apply(item), "key must not be null");
			final T previous = this.itemsByKey.putIfAbsent(key, item);
			if (previous != null) {
				throw new IllegalArgumentException("Duplicate key: " + key);
			}
		}
	}

	public static <T extends DependencyOwner<V>, V> DependencyResolver<T, V> of(final Collection<? extends T> items) {
		return new DependencyResolver<>(items, DependencyOwner::getDependencies, DependencyOwner::getKey);
	}

	public List<T> resolve() {
		final Map<V, State> state = new HashMap<>();
		final List<T> result = new ArrayList<>();

		for (final V key : this.itemsByKey.keySet()) {
			if (state.get(key) == null) {
				this.dfs(key, state, result, new ArrayDeque<>());
			}
		}

		Collections.reverse(result);
		return result;
	}

	public DependencyTree<T, V> getTree() {
		this.validate();

		final Map<V, Set<V>> dependentsByKey = new HashMap<>();
		for (final V key : this.itemsByKey.keySet()) {
			dependentsByKey.put(key, new LinkedHashSet<>());
		}

		for (final Map.Entry<V, T> entry : this.itemsByKey.entrySet()) {
			final V ownerKey = entry.getKey();
			final Set<V> dependencies = this.dependenciesSupplier.apply(entry.getValue());

			if (dependencies == null) {
				continue;
			}

			for (final V dependencyKey : dependencies) {
				dependentsByKey.get(dependencyKey).add(ownerKey);
			}
		}

		final List<V> roots = new ArrayList<>();
		for (final Map.Entry<V, Set<V>> entry : dependentsByKey.entrySet()) {
			if (entry.getValue().isEmpty()) {
				roots.add(entry.getKey());
			}
		}

		roots.sort(Comparator.comparing(String::valueOf));

		return new DependencyTree<>(this.itemsByKey, dependentsByKey, roots);
	}

	private void validate() {
		final Map<V, State> state = new HashMap<>();

		for (final V key : this.itemsByKey.keySet()) {
			if (state.get(key) == null) {
				this.validateDfs(key, state, new ArrayDeque<>());
			}
		}
	}

	private void validateDfs(final V key, final Map<V, State> state, final Deque<V> stack) {
		final T item = this.itemsByKey.get(key);
		if (item == null) {
			throw new IllegalStateException("Missing dependency: " + key);
		}

		state.put(key, State.VISITING);
		stack.push(key);

		final Set<V> dependencies = this.dependenciesSupplier.apply(item);
		if (dependencies != null) {
			for (final V dependencyKey : dependencies) {
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

	private void dfs(final V key, final Map<V, State> state, final List<T> out, final Deque<V> stack) {
		final T item = this.itemsByKey.get(key);
		if (item == null) {
			throw new IllegalStateException("Missing dependency: " + key);
		}

		state.put(key, State.VISITING);
		stack.push(key);

		final Set<V> dependencies = this.dependenciesSupplier.apply(item);
		if (dependencies != null) {
			for (final V dependencyKey : dependencies) {
				if (!this.itemsByKey.containsKey(dependencyKey)) {
					throw new IllegalStateException("Missing dependency: " + dependencyKey + " required by " + key);
				}

				final State dependencyState = state.get(dependencyKey);

				if (dependencyState == State.VISITING) {
					throw new IllegalStateException("Dependency cycle: " + this.buildCycle(dependencyKey, stack));
				}

				if (dependencyState == null) {
					this.dfs(dependencyKey, state, out, stack);
				}
			}
		}

		stack.pop();
		state.put(key, State.VISITED);
		out.add(item);
	}

	private String buildCycle(final V start, final Deque<V> stack) {
		final StringBuilder sb = new StringBuilder();

		for (final V entry : stack) {
			sb.append(entry).append(" -> ");
			if (Objects.equals(entry, start)) {
				break;
			}
		}

		sb.append(start);
		return sb.toString();
	}

	private enum State {
		VISITING, VISITED
	}

}