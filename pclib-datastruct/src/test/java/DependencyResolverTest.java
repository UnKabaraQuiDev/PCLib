import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
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
		final DependencyResolver<Item, String> resolver = this.resolver();

		Assertions.assertTrue(resolver.resolve().isEmpty());
	}

	@Test
	public void resolveSingleItem() {
		final DependencyResolver<Item, String> resolver = this.resolver(this.item("A"));

		DependencyResolverTest.assertKeys(resolver.resolve(), "A");
	}

	@Test
	public void resolveSimpleDependencyChain() {
		/*
		 * A depends on B B depends on C
		 *
		 * C -> B -> A
		 */
		final DependencyResolver<Item, String> resolver = this.resolver(this.item("A", "B"), this.item("B", "C"), this.item("C"));

		DependencyResolverTest.assertKeys(resolver.resolve(), "C", "B", "A");
	}

	@Test
	public void resolveMultipleDependencies() {
		/*
		 * A / \ B C \ / D
		 *
		 * A depends on B and C B and C depend on D
		 */
		final DependencyResolver<Item, String> resolver = this
				.resolver(this.item("A", "B", "C"), this.item("B", "D"), this.item("C", "D"), this.item("D"));

		final List<Item> result = resolver.resolve();

		Assertions.assertEquals(4, result.size());

		DependencyResolverTest.assertBefore(result, "D", "B");
		DependencyResolverTest.assertBefore(result, "D", "C");
		DependencyResolverTest.assertBefore(result, "B", "A");
		DependencyResolverTest.assertBefore(result, "C", "A");
	}

	@Test
	public void resolveMultipleRoots() {
		final DependencyResolver<Item, String> resolver = this.resolver(this.item("B"), this.item("A"));

		DependencyResolverTest.assertKeys(resolver.resolve(), "A", "B");
	}

	@Test
	public void resolveIsIndependentOfInputOrder() {
		final DependencyResolver<Item, String> first = this.resolver(this.item("A", "B"), this.item("B", "C"), this.item("C"));

		final DependencyResolver<Item, String> second = this.resolver(this.item("C"), this.item("B", "C"), this.item("A", "B"));

		DependencyResolverTest.assertKeys(first.resolve(), "C", "B", "A");
		DependencyResolverTest.assertKeys(second.resolve(), "C", "B", "A");
	}

	@Test
	public void resolveOptionalMissingDependency() {
		final DependencyResolver<Item, String> resolver = this.resolver(this.item("A", "MISSING"));

		final List<Item> result = resolver.resolve((ownerKey, dependencyKey) -> true);

		DependencyResolverTest.assertKeys(result, "A");
	}

	@Test
	public void resolveRequiredMissingDependencyFails() {
		final DependencyResolver<Item, String> resolver = this.resolver(this.item("A", "MISSING"));

		final IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> resolver.resolve());

		Assertions.assertEquals("Missing dependency: MISSING required by A", exception.getMessage());
	}

	@Test
	public void resolveOptionalDependencyOnlyForSpecificDependency() {
		final DependencyResolver<Item, String> resolver = this.resolver(this.item("A", "MISSING"));

		final List<Item> result = resolver.resolve((ownerKey, dependencyKey) -> "A".equals(ownerKey) && "MISSING".equals(dependencyKey));

		DependencyResolverTest.assertKeys(result, "A");
	}

	@Test
	public void resolveBooleanOptionalDependencies() {
		final DependencyResolver<Item, String> resolver = this.resolver(this.item("A", "MISSING"));

		Assertions.assertThrows(IllegalStateException.class, () -> resolver.resolve(false));

		DependencyResolverTest.assertKeys(resolver.resolve(true), "A");
	}

	@Test
	public void duplicateKeysFail() {
		final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> this.resolver(this.item("A"), this.item("A")));

		Assertions.assertEquals("Duplicate key: A", exception.getMessage());
	}

	@Test
	public void dependencyCycleFails() {
		/*
		 * A -> B -> C -> A
		 */
		final DependencyResolver<Item, String> resolver = this.resolver(this.item("A", "B"), this.item("B", "C"), this.item("C", "A"));

		final IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> resolver.resolve());

		Assertions.assertTrue(exception.getMessage().startsWith("Dependency cycle:"));
		Assertions.assertTrue(exception.getMessage().contains("A"));
		Assertions.assertTrue(exception.getMessage().contains("B"));
		Assertions.assertTrue(exception.getMessage().contains("C"));
	}

	@Test
	public void nullDependenciesAreAllowed() {
		final Item item = new Item("A") {

			@Override
			public Set<String> getDependencies() {
				return null;
			}

		};

		DependencyResolverTest.assertKeys(DependencyResolver.of(Collections.singletonList(item)).resolve(), "A");
	}

	// -------------------------------------------------------------------------
	// DependencyTree
	// -------------------------------------------------------------------------

	@Test
	public void treeGetRoots() {
		final DependencyTree<Item, String> tree = this.resolver(this.item("B"), this.item("A")).getTree();

		DependencyResolverTest.assertKeys(tree.getRoots(), "A", "B");
	}

	@Test
	public void treeGetRootsForDependencyTree() {
		/*
		 * A depends on B
		 *
		 * B | A
		 */
		final DependencyTree<Item, String> tree = this.resolver(this.item("A", "B"), this.item("B")).getTree();

		DependencyResolverTest.assertKeys(tree.getRoots(), "B");
	}

	@Test
	public void treeGetParents() {
		/*
		 * A depends on B and C
		 *
		 * B \ A / C
		 */
		final DependencyTree<Item, String> tree = this.resolver(this.item("A", "B", "C"), this.item("B"), this.item("C")).getTree();

		DependencyResolverTest.assertKeys(tree.getParents("A"), "B", "C");
		Assertions.assertTrue(tree.getParents("B").isEmpty());
		Assertions.assertTrue(tree.getParents("C").isEmpty());
	}

	@Test
	public void treeGetPathToRoot() {
		/*
		 * C | B | A
		 */
		final DependencyTree<Item, String> tree = this.resolver(this.item("A", "B"), this.item("B", "C"), this.item("C")).getTree();

		DependencyResolverTest.assertKeys(tree.getDependencyPath("A"), "A", "B", "C");
		DependencyResolverTest.assertKeys(tree.getDependencyPath("B"), "B", "C");
		DependencyResolverTest.assertKeys(tree.getDependencyPath("C"), "C");
	}

	@Test
	public void treeTraverseToRoot() {
		final DependencyTree<Item, String> tree = this.resolver(this.item("A", "B"), this.item("B", "C"), this.item("C")).getTree();

		final List<String> visited = new java.util.ArrayList<>();

		tree.traverseToRoot("A", item -> visited.add(item.getKey()));

		Assertions.assertEquals(Arrays.asList("A", "B", "C"), visited);
	}

	@Test
	public void treeTraverseToRootDoesNotVisitSameItemTwice() {
		/*
		 * A / \ B C \ / D
		 */
		final DependencyTree<Item, String> tree = this
				.resolver(this.item("A", "B", "C"), this.item("B", "D"), this.item("C", "D"), this.item("D"))
				.getTree();

		final List<String> visited = new java.util.ArrayList<>();

		tree.traverseToRoot("A", item -> visited.add(item.getKey()));

		Assertions.assertEquals(4, visited.size());
		Assertions.assertEquals(1, Collections.frequency(visited, "D"));
	}

	@Test
	public void treeToListUsesDependencyOrder() {
		final DependencyTree<Item, String> tree = this.resolver(this.item("A", "B"), this.item("B", "C"), this.item("C")).getTree();

		DependencyResolverTest.assertKeys(tree.toList(), "C", "B", "A");
	}

	@Test
	public void treeToListUsesProvidedListImplementation() {
		final DependencyTree<Item, String> tree = this.resolver(this.item("A"), this.item("B")).getTree();

		final LinkedHashSet<Item> set = new LinkedHashSet<>();

		final List<Item> result = tree.toList(() -> new java.util.ArrayList<>(set));

		Assertions.assertNotNull(result);
		Assertions.assertEquals(2, result.size());
	}

	@Test
	public void treeTraverseUsesDependencyOrder() {
		final DependencyTree<Item, String> tree = this.resolver(this.item("A", "B"), this.item("B", "C"), this.item("C")).getTree();

		final List<String> visited = new java.util.ArrayList<>();

		tree.traverse(item -> visited.add(item.getKey()));

		Assertions.assertEquals(Arrays.asList("C", "B", "A"), visited);
	}

	@Test
	public void treePrintTree() {
		/*
		 * B | A
		 */
		final DependencyTree<Item, String> tree = this.resolver(this.item("A", "B"), this.item("B")).getTree();

		final StringWriter output = new StringWriter();
		final PrintWriter writer = new PrintWriter(output);

		tree.printTree(writer, Item::getKey);

		Assertions.assertEquals("B\n\\- A\n", output.toString());
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
		final DependencyTree<Item, String> tree = this.resolver(this.item("A", "C", "B"), this.item("C"), this.item("B")).getTree();

		final StringWriter output = new StringWriter();

		tree.printTree(new PrintWriter(output), Item::getKey);

		Assertions.assertEquals("B\n" + "\\- A\n" + "C\n" + "\\- A\n", output.toString());
	}

	// -------------------------------------------------------------------------
	// Validation
	// -------------------------------------------------------------------------

	@Test
	public void getTreeFailsForMissingDependency() {
		final DependencyResolver<Item, String> resolver = this.resolver(this.item("A", "MISSING"));

		final IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> resolver.getTree());

		Assertions.assertEquals("Missing dependency: MISSING required by A", exception.getMessage());
	}

	@Test
	public void getTreeFailsForCycle() {
		final DependencyResolver<Item, String> resolver = this.resolver(this.item("A", "B"), this.item("B", "A"));

		final IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () -> resolver.getTree());

		Assertions.assertTrue(exception.getMessage().startsWith("Dependency cycle:"));
	}

	// -------------------------------------------------------------------------
	// Null checks
	// -------------------------------------------------------------------------

	@Test
	public void resolverRejectsNullItems() {
		Assertions.assertThrows(NullPointerException.class,
				() -> new DependencyResolver<Item, String>(null, Item::getDependencies, Item::getKey));
	}

	@Test
	public void resolverRejectsNullKey() {
		final Item item = new Item("A") {
			@Override
			public String getKey() {
				return null;
			}
		};

		Assertions.assertThrows(NullPointerException.class, () -> DependencyResolver.of(Collections.singletonList(item)));
	}

	@Test
	public void resolverRejectsNullDependenciesSupplier() {
		Assertions.assertThrows(NullPointerException.class,
				() -> new DependencyResolver<Item, String>(Collections.emptyList(), null, Item::getKey));
	}

	@Test
	public void resolverRejectsNullKeySupplier() {
		Assertions.assertThrows(NullPointerException.class,
				() -> new DependencyResolver<Item, String>(Collections.emptyList(), Item::getDependencies, null));
	}

	@Test
	public void treePrintTreeRejectsNullWriter() {
		final DependencyTree<Item, String> tree = this.resolver(this.item("A")).getTree();

		Assertions.assertThrows(NullPointerException.class, () -> tree.printTree(null, Item::getKey));
	}

	@Test
	public void treePrintTreeRejectsNullLabelFunction() {
		final DependencyTree<Item, String> tree = this.resolver(this.item("A")).getTree();

		Assertions.assertThrows(NullPointerException.class, () -> tree.printTree(new PrintWriter(new StringWriter()), null));
	}

	@Test
	public void treeTraverseToRootRejectsNullStart() {
		final DependencyTree<Item, String> tree = this.resolver(this.item("A")).getTree();

		Assertions.assertThrows(NullPointerException.class, () -> tree.traverseToRoot(null, item -> {
		}));
	}

	@Test
	public void treeTraverseToRootRejectsNullConsumer() {
		final DependencyTree<Item, String> tree = this.resolver(this.item("A")).getTree();

		Assertions.assertThrows(NullPointerException.class, () -> tree.traverseToRoot("A", null));
	}

	@Test
	public void treeGetPathToRootRejectsNullStart() {
		final DependencyTree<Item, String> tree = this.resolver(this.item("A")).getTree();

		Assertions.assertThrows(NullPointerException.class, () -> tree.getDependencyPath(null));
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private static void assertKeys(final List<Item> actual, final String... expected) {

		Assertions.assertEquals(Arrays.asList(expected), DependencyResolverTest.keys(actual));
	}

	private static void assertKeys(final Set<Item> actual, final String... expected) {

		Assertions.assertEquals(new LinkedHashSet<>(Arrays.asList(expected)), DependencyResolverTest.keys(actual));
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

		final int firstIndex = DependencyResolverTest.indexOf(items, first);
		final int secondIndex = DependencyResolverTest.indexOf(items, second);

		Assertions.assertTrue(firstIndex < secondIndex,
				first + " should come before " + second + " but was " + DependencyResolverTest.keys(items));
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
