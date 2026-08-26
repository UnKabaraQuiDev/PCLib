import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.datastructure.tree.dependency.DependencyOwner;
import lu.kbra.pclib.datastructure.tree.dependency.DependencyResolver;
import lu.kbra.pclib.datastructure.tree.dependency.DependencyTree;

public class DependencyResolverTest {

	private static class Item implements DependencyOwner<String> {

		private final String key;
		private final Set<String> dependencies;

		Item(final String key, final String... dependencies) {
			this.key = key;
			this.dependencies = new LinkedHashSet<>(Arrays.asList(dependencies));
		}

		@Override
		public Set<String> getDependencies() {
			return this.dependencies;
		}

		@Override
		public String getKey() {
			return this.key;
		}

		@Override
		public String toString() {
			return this.key;
		}
	}

	private Item item(final String key, final String... dependencies) {
		return new Item(key, dependencies);
	}

	private DependencyResolver<Item, String> resolver(final Item... items) {
		return DependencyResolver.of(Arrays.asList(items));
	}

	// -------------------------------------------------------------------------
	// Resolver
	// -------------------------------------------------------------------------

	@Test
	public void resolveEmpty() {
		final DependencyResolver<Item, String> resolver = resolver();

		assertTrue(resolver.resolve().isEmpty());
	}

	@Test
	public void resolveSingleItem() {
		final DependencyResolver<Item, String> resolver = resolver(item("A"));

		assertKeys(resolver.resolve(), "A");
	}

	@Test
	public void resolveSimpleDependencyChain() {
		/*
		 * A depends on B B depends on C
		 *
		 * C -> B -> A
		 */
		final DependencyResolver<Item, String> resolver = resolver(item("A", "B"), item("B", "C"), item("C"));

		assertKeys(resolver.resolve(), "C", "B", "A");
	}

	@Test
	public void resolveMultipleDependencies() {
		/*
		 * A / \ B C \ / D
		 *
		 * A depends on B and C B and C depend on D
		 */
		final DependencyResolver<Item, String> resolver = resolver(item("A", "B", "C"), item("B", "D"), item("C", "D"), item("D"));

		final List<Item> result = resolver.resolve();

		assertEquals(4, result.size());

		assertBefore(result, "D", "B");
		assertBefore(result, "D", "C");
		assertBefore(result, "B", "A");
		assertBefore(result, "C", "A");
	}

	@Test
	public void resolveMultipleRoots() {
		final DependencyResolver<Item, String> resolver = resolver(item("B"), item("A"));

		assertKeys(resolver.resolve(), "A", "B");
	}

	@Test
	public void resolveIsIndependentOfInputOrder() {
		final DependencyResolver<Item, String> first = resolver(item("A", "B"), item("B", "C"), item("C"));

		final DependencyResolver<Item, String> second = resolver(item("C"), item("B", "C"), item("A", "B"));

		assertKeys(first.resolve(), "C", "B", "A");
		assertKeys(second.resolve(), "C", "B", "A");
	}

	@Test
	public void resolveOptionalMissingDependency() {
		final DependencyResolver<Item, String> resolver = resolver(item("A", "MISSING"));

		final List<Item> result = resolver.resolve((ownerKey, dependencyKey) -> true);

		assertKeys(result, "A");
	}

	@Test
	public void resolveRequiredMissingDependencyFails() {
		final DependencyResolver<Item, String> resolver = resolver(item("A", "MISSING"));

		final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> resolver.resolve());

		assertEquals("Missing dependency: MISSING required by A", exception.getMessage());
	}

	@Test
	public void resolveOptionalDependencyOnlyForSpecificDependency() {
		final DependencyResolver<Item, String> resolver = resolver(item("A", "MISSING"));

		final List<Item> result = resolver.resolve((ownerKey, dependencyKey) -> "A".equals(ownerKey) && "MISSING".equals(dependencyKey));

		assertKeys(result, "A");
	}

	@Test
	public void resolveBooleanOptionalDependencies() {
		final DependencyResolver<Item, String> resolver = resolver(item("A", "MISSING"));

		assertThrows(IllegalStateException.class, () -> resolver.resolve(false));

		assertKeys(resolver.resolve(true), "A");
	}

	@Test
	public void duplicateKeysFail() {
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> resolver(item("A"), item("A")));

		assertEquals("Duplicate key: A", exception.getMessage());
	}

	@Test
	public void dependencyCycleFails() {
		/*
		 * A -> B -> C -> A
		 */
		final DependencyResolver<Item, String> resolver = resolver(item("A", "B"), item("B", "C"), item("C", "A"));

		final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> resolver.resolve());

		assertTrue(exception.getMessage().startsWith("Dependency cycle:"));
		assertTrue(exception.getMessage().contains("A"));
		assertTrue(exception.getMessage().contains("B"));
		assertTrue(exception.getMessage().contains("C"));
	}

	@Test
	public void nullDependenciesAreAllowed() {
		final Item item = new Item("A") {

			@Override
			public Set<String> getDependencies() {
				return null;
			}

		};

		assertKeys(DependencyResolver.of(Collections.singletonList(item)).resolve(), "A");
	}

	// -------------------------------------------------------------------------
	// DependencyTree
	// -------------------------------------------------------------------------

	@Test
	public void treeGetRoots() {
		final DependencyTree<Item, String> tree = resolver(item("B"), item("A")).getTree();

		assertKeys(tree.getRoots(), "A", "B");
	}

	@Test
	public void treeGetRootsForDependencyTree() {
		/*
		 * A depends on B
		 *
		 * B | A
		 */
		final DependencyTree<Item, String> tree = resolver(item("A", "B"), item("B")).getTree();

		assertKeys(tree.getRoots(), "B");
	}

	@Test
	public void treeGetParents() {
		/*
		 * A depends on B and C
		 *
		 * B \ A / C
		 */
		final DependencyTree<Item, String> tree = resolver(item("A", "B", "C"), item("B"), item("C")).getTree();

		assertKeys(tree.getParents("A"), "B", "C");
		assertTrue(tree.getParents("B").isEmpty());
		assertTrue(tree.getParents("C").isEmpty());
	}

	@Test
	public void treeGetPathToRoot() {
		/*
		 * C | B | A
		 */
		final DependencyTree<Item, String> tree = resolver(item("A", "B"), item("B", "C"), item("C")).getTree();

		assertKeys(tree.getDependencyPath("A"), "A", "B", "C");
		assertKeys(tree.getDependencyPath("B"), "B", "C");
		assertKeys(tree.getDependencyPath("C"), "C");
	}

	@Test
	public void treeTraverseToRoot() {
		final DependencyTree<Item, String> tree = resolver(item("A", "B"), item("B", "C"), item("C")).getTree();

		final List<String> visited = new java.util.ArrayList<>();

		tree.traverseToRoot("A", item -> visited.add(item.getKey()));

		assertEquals(Arrays.asList("A", "B", "C"), visited);
	}

	@Test
	public void treeTraverseToRootDoesNotVisitSameItemTwice() {
		/*
		 * A / \ B C \ / D
		 */
		final DependencyTree<Item, String> tree = resolver(item("A", "B", "C"), item("B", "D"), item("C", "D"), item("D")).getTree();

		final List<String> visited = new java.util.ArrayList<>();

		tree.traverseToRoot("A", item -> visited.add(item.getKey()));

		assertEquals(4, visited.size());
		assertEquals(1, Collections.frequency(visited, "D"));
	}

	@Test
	public void treeToListUsesDependencyOrder() {
		final DependencyTree<Item, String> tree = resolver(item("A", "B"), item("B", "C"), item("C")).getTree();

		assertKeys(tree.toList(), "C", "B", "A");
	}

	@Test
	public void treeToListUsesProvidedListImplementation() {
		final DependencyTree<Item, String> tree = resolver(item("A"), item("B")).getTree();

		final LinkedHashSet<Item> set = new LinkedHashSet<>();

		final List<Item> result = tree.toList(() -> new java.util.ArrayList<>(set));

		assertNotNull(result);
		assertEquals(2, result.size());
	}

	@Test
	public void treeTraverseUsesDependencyOrder() {
		final DependencyTree<Item, String> tree = resolver(item("A", "B"), item("B", "C"), item("C")).getTree();

		final List<String> visited = new java.util.ArrayList<>();

		tree.traverse(item -> visited.add(item.getKey()));

		assertEquals(Arrays.asList("C", "B", "A"), visited);
	}

	@Test
	public void treePrintTree() {
		/*
		 * B | A
		 */
		final DependencyTree<Item, String> tree = resolver(item("A", "B"), item("B")).getTree();

		final StringWriter output = new StringWriter();
		final PrintWriter writer = new PrintWriter(output);

		tree.printTree(writer, Item::getKey);

		assertEquals("B\n\\- A\n", output.toString());
	}

	@Test
	public void treePrintTreeSortsChildrenByKey() {
		/*
		 * A depends on C and B.
		 *
		 * B | A
		 *
		 * C | A
		 *
		 * Children should be printed B, C.
		 */
		final DependencyTree<Item, String> tree = resolver(item("A", "C", "B"), item("C"), item("B")).getTree();

		final StringWriter output = new StringWriter();

		tree.printTree(new PrintWriter(output), Item::getKey);

		assertEquals("B\n" + "\\- A\n" + "C\n" + "\\- A\n", output.toString());
	}

	// -------------------------------------------------------------------------
	// Validation
	// -------------------------------------------------------------------------

	@Test
	public void getTreeFailsForMissingDependency() {
		final DependencyResolver<Item, String> resolver = resolver(item("A", "MISSING"));

		final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> resolver.getTree());

		assertEquals("Missing dependency: MISSING required by A", exception.getMessage());
	}

	@Test
	public void getTreeFailsForCycle() {
		final DependencyResolver<Item, String> resolver = resolver(item("A", "B"), item("B", "A"));

		final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> resolver.getTree());

		assertTrue(exception.getMessage().startsWith("Dependency cycle:"));
	}

	// -------------------------------------------------------------------------
	// Null checks
	// -------------------------------------------------------------------------

	@Test
	public void resolverRejectsNullItems() {
		assertThrows(NullPointerException.class, () -> new DependencyResolver<Item, String>(null, Item::getDependencies, Item::getKey));
	}

	@Test
	public void resolverRejectsNullKey() {
		final Item item = new Item("A") {
			@Override
			public String getKey() {
				return null;
			}
		};

		assertThrows(NullPointerException.class, () -> DependencyResolver.of(Collections.singletonList(item)));
	}

	@Test
	public void resolverRejectsNullDependenciesSupplier() {
		assertThrows(NullPointerException.class, () -> new DependencyResolver<Item, String>(Collections.emptyList(), null, Item::getKey));
	}

	@Test
	public void resolverRejectsNullKeySupplier() {
		assertThrows(NullPointerException.class,
				() -> new DependencyResolver<Item, String>(Collections.emptyList(), Item::getDependencies, null));
	}

	@Test
	public void treePrintTreeRejectsNullWriter() {
		final DependencyTree<Item, String> tree = resolver(item("A")).getTree();

		assertThrows(NullPointerException.class, () -> tree.printTree(null, Item::getKey));
	}

	@Test
	public void treePrintTreeRejectsNullLabelFunction() {
		final DependencyTree<Item, String> tree = resolver(item("A")).getTree();

		assertThrows(NullPointerException.class, () -> tree.printTree(new PrintWriter(new StringWriter()), null));
	}

	@Test
	public void treeTraverseToRootRejectsNullStart() {
		final DependencyTree<Item, String> tree = resolver(item("A")).getTree();

		assertThrows(NullPointerException.class, () -> tree.traverseToRoot(null, item -> {
		}));
	}

	@Test
	public void treeTraverseToRootRejectsNullConsumer() {
		final DependencyTree<Item, String> tree = resolver(item("A")).getTree();

		assertThrows(NullPointerException.class, () -> tree.traverseToRoot("A", null));
	}

	@Test
	public void treeGetPathToRootRejectsNullStart() {
		final DependencyTree<Item, String> tree = resolver(item("A")).getTree();

		assertThrows(NullPointerException.class, () -> tree.getDependencyPath(null));
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private static void assertKeys(final List<Item> actual, final String... expected) {

		assertEquals(Arrays.asList(expected), keys(actual));
	}

	private static void assertKeys(final Set<Item> actual, final String... expected) {

		assertEquals(new LinkedHashSet<>(Arrays.asList(expected)), keys(actual));
	}

	private static List<String> keys(final List<Item> items) {
		final java.util.ArrayList<String> result = new java.util.ArrayList<>();

		for (final Item item : items) {
			result.add(item.getKey());
		}

		return result;
	}

	private static Set<String> keys(final Set<Item> items) {
		final LinkedHashSet<String> result = new LinkedHashSet<>();

		for (final Item item : items) {
			result.add(item.getKey());
		}

		return result;
	}

	private static void assertBefore(final List<Item> items, final String first, final String second) {

		final int firstIndex = indexOf(items, first);
		final int secondIndex = indexOf(items, second);

		assertTrue(firstIndex < secondIndex, first + " should come before " + second + " but was " + keys(items));
	}

	private static int indexOf(final List<Item> items, final String key) {

		for (int i = 0; i < items.size(); i++) {
			if (key.equals(items.get(i).getKey())) {
				return i;
			}
		}

		return -1;
	}
}
