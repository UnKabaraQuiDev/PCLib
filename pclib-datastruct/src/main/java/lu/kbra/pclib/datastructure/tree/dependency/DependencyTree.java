package lu.kbra.pclib.datastructure.tree.dependency;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @see {@link DependencyResolver}
 */
public final class DependencyTree<ITEM, KEY> {

	private final Map<KEY, ITEM> itemsByKey;
	private final Map<KEY, Set<KEY>> dependentsByKey;
	private final List<KEY> roots;
	private final Map<KEY, Set<KEY>> parentsByKey;

	DependencyTree(final Map<KEY, ITEM> itemsByKey, final Map<KEY, Set<KEY>> dependentsByKey, final List<KEY> roots) {
		this.itemsByKey = new HashMap<>(itemsByKey);

		this.dependentsByKey = new HashMap<>();
		for (final Map.Entry<KEY, Set<KEY>> entry : dependentsByKey.entrySet()) {
			this.dependentsByKey.put(entry.getKey(), new LinkedHashSet<>(entry.getValue()));
		}

		this.parentsByKey = new HashMap<>();
		for (final Map.Entry<KEY, Set<KEY>> entry : dependentsByKey.entrySet()) {
			for (final KEY child : entry.getValue()) {
				this.parentsByKey.computeIfAbsent(child, k -> new LinkedHashSet<>()).add(entry.getKey());
			}
		}

		this.roots = new ArrayList<>(roots);
	}

	public List<ITEM> getRoots() {
		final List<ITEM> result = new ArrayList<>(this.roots.size());
		for (final KEY root : this.roots) {
			result.add(this.itemsByKey.get(root));
		}
		return result;
	}

	public void printTree(final PrintWriter writer, final Function<ITEM, String> labelFunction) {

		Objects.requireNonNull(writer, "writer");
		Objects.requireNonNull(labelFunction, "labelFunction");

		for (final KEY root : this.roots) {
			this.printNode(writer, labelFunction, root, null, true);
		}

		writer.flush();
	}

	private void printNode(
			final PrintWriter writer,
			final Function<ITEM, String> labelFunction,
			final KEY key,
			final String prefix,
			final boolean isLast) {

		final ITEM item = this.itemsByKey.get(key);

		if (prefix == null) {
			writer.println(labelFunction.apply(item));
		} else {
			writer.println(prefix + (isLast ? "\\- " : "+- ") + labelFunction.apply(item));
		}

		final List<KEY> children = new ArrayList<>(this.dependentsByKey.getOrDefault(key, Collections.emptySet()));

		children.sort(Comparator.comparing(String::valueOf));

		for (int i = 0; i < children.size(); i++) {
			final KEY child = children.get(i);
			final boolean childIsLast = i == children.size() - 1;

			final String childPrefix;

			if (prefix == null) {
				childPrefix = "";
			} else {
				childPrefix = prefix + (isLast ? "   " : "|  ");
			}

			this.printNode(writer, labelFunction, child, childPrefix, childIsLast);
		}
	}

	public void traverseToRoot(final KEY start, final Consumer<ITEM> consumer) {
		Objects.requireNonNull(start);
		Objects.requireNonNull(consumer);

		final Set<KEY> visited = new HashSet<>();
		this.traverseToRoot(start, visited, consumer);
	}

	private void traverseToRoot(final KEY key, final Set<KEY> visited, final Consumer<ITEM> consumer) {
		if (!visited.add(key)) {
			return;
		}

		consumer.accept(this.itemsByKey.get(key));

		for (final KEY parent : this.parentsByKey.getOrDefault(key, Collections.emptySet())) {
			this.traverseToRoot(parent, visited, consumer);
		}
	}

	public List<ITEM> getDependencyPath(final KEY start) {
		Objects.requireNonNull(start);

		final List<ITEM> path = new ArrayList<>();
		final Set<KEY> visited = new HashSet<>();

		this.collectPathToRoot(start, visited, path);

		return path;
	}

	private void collectPathToRoot(final KEY key, final Set<KEY> visited, final List<ITEM> path) {
		if (!visited.add(key)) {
			return;
		}

		path.add(this.itemsByKey.get(key));

		for (final KEY parent : this.parentsByKey.getOrDefault(key, Collections.emptySet())) {
			this.collectPathToRoot(parent, visited, path);
		}
	}

	public Set<ITEM> getParents(final KEY key) {
		final Set<ITEM> result = new LinkedHashSet<>();

		for (final KEY parent : this.parentsByKey.getOrDefault(key, Collections.emptySet())) {
			result.add(this.itemsByKey.get(parent));
		}

		return result;
	}

	public List<ITEM> toList() {
		return this.toList(ArrayList::new);
	}

	public List<ITEM> toList(final Supplier<List<ITEM>> provider) {
		final List<ITEM> list = provider.get();
		this.traverse(list::add);
		return list;
	}

	public void traverse(final Consumer<ITEM> consumer) {
		Objects.requireNonNull(consumer);

		final Map<KEY, Integer> remainingParents = new HashMap<>();

		for (final KEY key : this.itemsByKey.keySet()) {
			remainingParents.put(key, this.parentsByKey.getOrDefault(key, Collections.emptySet()).size());
		}

		final PriorityQueue<KEY> ready = new PriorityQueue<>(Comparator.comparing(String::valueOf));

		ready.addAll(this.roots);

		while (!ready.isEmpty()) {
			final KEY key = ready.poll();

			consumer.accept(this.itemsByKey.get(key));

			for (final KEY child : this.dependentsByKey.getOrDefault(key, Collections.emptySet())) {

				final int remaining = remainingParents.merge(child, -1, Integer::sum);

				if (remaining == 0) {
					ready.add(child);
				}
			}
		}
	}

}
