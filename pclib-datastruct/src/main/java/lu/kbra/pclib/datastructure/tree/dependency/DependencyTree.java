package lu.kbra.pclib.datastructure.tree.dependency;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public final class DependencyTree<T, V> {

	private final Map<V, T> itemsByKey;
	private final Map<V, Set<V>> dependentsByKey;
	private final List<V> roots;

	DependencyTree(final Map<V, T> itemsByKey, final Map<V, Set<V>> dependentsByKey, final List<V> roots) {
		this.itemsByKey = new HashMap<>(itemsByKey);

		this.dependentsByKey = new HashMap<>();
		for (final Map.Entry<V, Set<V>> entry : dependentsByKey.entrySet()) {
			this.dependentsByKey.put(entry.getKey(), new LinkedHashSet<>(entry.getValue()));
		}

		this.roots = new ArrayList<>(roots);
	}

	public List<T> getRoots() {
		final List<T> result = new ArrayList<>(this.roots.size());
		for (final V root : this.roots) {
			result.add(this.itemsByKey.get(root));
		}
		return result;
	}

	public void printTree(final PrintWriter writer, final Function<T, String> labelFunction) {
		Objects.requireNonNull(writer, "writer");
		Objects.requireNonNull(labelFunction, "labelFunction");

		for (int i = 0; i < this.roots.size(); i++) {
			final V root = this.roots.get(i);
			final boolean isLast = i == this.roots.size() - 1;
			this.printNode(writer, labelFunction, root, "", isLast);
		}

		writer.flush();
	}

	private void printNode(
			final PrintWriter writer,
			final Function<T, String> labelFunction,
			final V key,
			final String prefix,
			final boolean isLast) {
		final T item = this.itemsByKey.get(key);

		if (prefix.isEmpty()) {
			writer.println(labelFunction.apply(item));
		} else {
			writer.println(prefix + (isLast ? "\\- " : "+- ") + labelFunction.apply(item));
		}

		final List<V> children = new ArrayList<>(this.dependentsByKey.getOrDefault(key, new HashSet<>()));
		children.sort(Comparator.comparing(String::valueOf));

		for (int i = 0; i < children.size(); i++) {
			final V child = children.get(i);
			final boolean childIsLast = i == children.size() - 1;
			final String childPrefix = prefix + (prefix.isEmpty() ? ""
					: isLast ? "   "
					: "|  ");
			this.printNode(writer, labelFunction, child, childPrefix, childIsLast);
		}
	}

}
