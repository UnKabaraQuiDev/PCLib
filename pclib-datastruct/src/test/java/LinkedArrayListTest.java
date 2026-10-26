
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.datastructure.list.LinkedArrayList;

public class LinkedArrayListTest {

	private LinkedArrayList<Integer> list;

	@BeforeEach
	void setUp() {
		this.list = new LinkedArrayList<>(4);
	}

	@Test
	@DisplayName("New list is empty")
	void testEmptyList() {
		Assertions.assertTrue(this.list.isEmpty());
		Assertions.assertEquals(0, this.list.size());
	}

	@Test
	@DisplayName("Default constructor")
	void testDefaultConstructor() {
		final LinkedArrayList<Integer> defaultList = new LinkedArrayList<>();

		Assertions.assertTrue(defaultList.isEmpty());
		Assertions.assertTrue(defaultList.getMaxBladeSize() > 0);
	}

	@Test
	@DisplayName("Custom maximum blade size")
	void testCustomBladeSize() {
		final LinkedArrayList<Integer> custom = new LinkedArrayList<>(8);

		Assertions.assertEquals(8, custom.getMaxBladeSize());
	}

	@Test
	@DisplayName("Reject invalid maximum blade sizes")
	void testInvalidBladeSize() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new LinkedArrayList<>(0));
		Assertions.assertThrows(IllegalArgumentException.class, () -> new LinkedArrayList<>(-1));
	}

	@Test
	@DisplayName("Constructor with initial size")
	void testInitialSize() {
		final LinkedArrayList<Integer> initialized = new LinkedArrayList<>(10, 4);

		Assertions.assertEquals(10, initialized.size());

		for (final Integer element : initialized) {
			Assertions.assertNull(element);
		}
	}

	@Test
	@DisplayName("Add and retrieve elements")
	void testAddAndGet() {
		for (int i = 0; i < 10; i++) {
			Assertions.assertTrue(this.list.add(i));
		}

		Assertions.assertEquals(10, this.list.size());

		for (int i = 0; i < 10; i++) {
			Assertions.assertEquals(i, this.list.get(i));
		}
	}

	@Test
	@DisplayName("Access elements across blade boundaries")
	void testBladeBoundaries() {
		for (int i = 0; i < 20; i++) {
			this.list.add(i);
		}

		Assertions.assertEquals(3, this.list.get(3));
		Assertions.assertEquals(4, this.list.get(4));
		Assertions.assertEquals(7, this.list.get(7));
		Assertions.assertEquals(8, this.list.get(8));
		Assertions.assertEquals(15, this.list.get(15));
		Assertions.assertEquals(16, this.list.get(16));
		Assertions.assertEquals(19, this.list.get(19));
	}

	@Test
	@DisplayName("Add thousands of elements")
	void testDynamicGrowth() {
		for (int i = 0; i < 10_000; i++) {
			this.list.add(i);
		}

		Assertions.assertEquals(10_000, this.list.size());

		for (int i = 0; i < 10_000; i++) {
			Assertions.assertEquals(i, this.list.get(i));
		}
	}

	@Test
	@DisplayName("Insert at beginning")
	void testInsertAtBeginning() {
		this.list.addAll(Arrays.asList(1, 2, 3, 4));

		this.list.add(0, 99);

		Assertions.assertEquals(Arrays.asList(99, 1, 2, 3, 4), this.list);
	}

	@Test
	@DisplayName("Insert in middle")
	void testInsertInMiddle() {
		this.list.addAll(Arrays.asList(0, 1, 2, 3, 4, 5));

		this.list.add(3, 99);

		Assertions.assertEquals(Arrays.asList(0, 1, 2, 99, 3, 4, 5), this.list);
	}

	@Test
	@DisplayName("Insert at end")
	void testInsertAtEnd() {
		this.list.addAll(Arrays.asList(1, 2, 3));

		this.list.add(this.list.size(), 4);

		Assertions.assertEquals(Arrays.asList(1, 2, 3, 4), this.list);
	}

	@Test
	@DisplayName("Insert into full blade")
	void testInsertIntoFullBlade() {
		this.list.addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));

		this.list.add(2, 99);

		Assertions.assertEquals(Arrays.asList(0, 1, 99, 2, 3, 4, 5, 6, 7), this.list);
	}

	@Test
	@DisplayName("Remove first element")
	void testRemoveFirst() {
		this.list.addAll(Arrays.asList(0, 1, 2, 3, 4));

		Assertions.assertEquals(0, this.list.remove(0));
		Assertions.assertEquals(Arrays.asList(1, 2, 3, 4), this.list);
	}

	@Test
	@DisplayName("Remove middle element")
	void testRemoveMiddle() {
		this.list.addAll(Arrays.asList(0, 1, 2, 3, 4, 5));

		Assertions.assertEquals(2, this.list.remove(2));
		Assertions.assertEquals(Arrays.asList(0, 1, 3, 4, 5), this.list);
	}

	@Test
	@DisplayName("Remove last element")
	void testRemoveLast() {
		this.list.addAll(Arrays.asList(0, 1, 2, 3, 4));

		Assertions.assertEquals(4, this.list.remove(4));
		Assertions.assertEquals(Arrays.asList(0, 1, 2, 3), this.list);
	}

	@Test
	@DisplayName("Remove across blade boundaries")
	void testRemoveAcrossBlades() {
		for (int i = 0; i < 12; i++) {
			this.list.add(i);
		}

		this.list.remove(3);
		this.list.remove(3);
		this.list.remove(3);

		Assertions.assertEquals(Arrays.asList(0, 1, 2, 6, 7, 8, 9, 10, 11), this.list);
	}

	@Test
	@DisplayName("Remove all elements one by one")
	void testRemoveAllElements() {
		for (int i = 0; i < 20; i++) {
			this.list.add(i);
		}

		while (!this.list.isEmpty()) {
			this.list.remove(0);
		}

		Assertions.assertEquals(0, this.list.size());
		Assertions.assertTrue(this.list.isEmpty());

		this.list.add(42);

		Assertions.assertEquals(42, this.list.get(0));
	}

	@Test
	@DisplayName("Deleting does not shrink the blade array")
	void testRemoveDoesNotShrinkArray() throws Exception {
		this.list.addAll(Arrays.asList(0, 1, 2, 3));

		final Field headField = LinkedArrayList.class.getDeclaredField("head");
		headField.setAccessible(true);

		final Object blade = headField.get(this.list);

		final Field elementsField = blade.getClass().getDeclaredField("elements");
		elementsField.setAccessible(true);

		final Field lengthField = blade.getClass().getDeclaredField("length");
		lengthField.setAccessible(true);

		final Object[] originalArray = (Object[]) elementsField.get(blade);
		final int originalCapacity = originalArray.length;

		this.list.remove(1);

		final Object[] currentArray = (Object[]) elementsField.get(blade);

		Assertions.assertSame(originalArray, currentArray, "Removing an element must not replace the array");
		Assertions.assertEquals(originalCapacity, currentArray.length);
		Assertions.assertEquals(3, lengthField.getInt(blade));
		Assertions.assertEquals(Arrays.asList(0, 2, 3), this.list);
	}

	@Test
	@DisplayName("Set replaces element and returns old value")
	void testSet() {
		this.list.addAll(Arrays.asList(1, 2, 3, 4, 5));

		Assertions.assertEquals(3, this.list.set(2, 99));

		Assertions.assertEquals(Arrays.asList(1, 2, 99, 4, 5), this.list);
	}

	@Test
	@DisplayName("Support null elements")
	void testNullElements() {
		this.list.add(null);
		this.list.add(1);
		this.list.add(null);

		Assertions.assertEquals(3, this.list.size());
		Assertions.assertNull(this.list.get(0));
		Assertions.assertEquals(1, this.list.get(1));
		Assertions.assertNull(this.list.get(2));
		Assertions.assertNull(this.list.remove(0));
		Assertions.assertEquals(Arrays.asList(1, null), this.list);
	}

	@Test
	@DisplayName("Invalid get indices throw exceptions")
	void testInvalidGetIndex() {
		this.list.add(1);

		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> this.list.get(-1));
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> this.list.get(1));
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> this.list.get(100));
	}

	@Test
	@DisplayName("Invalid insertion indices throw exceptions")
	void testInvalidInsertIndex() {
		this.list.add(1);

		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> this.list.add(-1, 99));
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> this.list.add(2, 99));
	}

	@Test
	@DisplayName("Invalid removal indices throw exceptions")
	void testInvalidRemoveIndex() {
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> this.list.remove(0));

		this.list.add(1);

		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> this.list.remove(-1));
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> this.list.remove(1));
	}

	@Test
	@DisplayName("Clear removes all elements")
	void testClear() {
		for (int i = 0; i < 100; i++) {
			this.list.add(i);
		}

		this.list.clear();

		Assertions.assertEquals(0, this.list.size());
		Assertions.assertTrue(this.list.isEmpty());

		this.list.add(42);

		Assertions.assertEquals(42, this.list.get(0));
	}

	@Test
	@DisplayName("Iterator traverses all blades")
	void testIterator() {
		for (int i = 0; i < 20; i++) {
			this.list.add(i);
		}

		int expected = 0;

		for (final Integer value : this.list) {
			Assertions.assertEquals(expected++, value);
		}

		Assertions.assertEquals(20, expected);
	}

	@Test
	@DisplayName("Iterator detects structural modification")
	void testConcurrentModification() {
		this.list.addAll(Arrays.asList(1, 2, 3));

		final Iterator<Integer> iterator = this.list.iterator();

		iterator.next();
		this.list.add(4);

		Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
	}

	@Test
	@DisplayName("Works with maximum blade size of one")
	void testBladeSizeOne() {
		final LinkedArrayList<Integer> small = new LinkedArrayList<>(1);

		small.addAll(Arrays.asList(1, 2, 3, 4));

		small.add(2, 99);

		Assertions.assertEquals(Arrays.asList(1, 2, 99, 3, 4), small);

		small.remove(1);

		Assertions.assertEquals(Arrays.asList(1, 99, 3, 4), small);
	}

	@Test
	@DisplayName("Randomized comparison with ArrayList")
	void testRandomizedOperations() {
		final int operations = 5_000;

		final Random random = new Random(12345);

		final LinkedArrayList<Integer> actual = new LinkedArrayList<>(4);
		final List<Integer> expected = new ArrayList<>();

		for (int i = 0; i < operations; i++) {
			int operation = random.nextInt(4);

			if (expected.isEmpty()) {
				operation = 0;
			}

			switch (operation) {
			case 0: {
				final int value = random.nextInt(10000);

				actual.add(value);
				expected.add(value);
				break;
			}

			case 1: {
				final int index = random.nextInt(expected.size() + 1);
				final int value = random.nextInt(10000);

				actual.add(index, value);
				expected.add(index, value);
				break;
			}

			case 2: {
				final int index = random.nextInt(expected.size());

				Assertions.assertEquals(expected.remove(index), actual.remove(index));
				break;
			}

			case 3: {
				final int index = random.nextInt(expected.size());
				final int value = random.nextInt(10000);

				Assertions.assertEquals(expected.set(index, value), actual.set(index, value));
				break;
			}

			default:
				Assertions.fail("Unknown operation");
			}

			Assertions.assertEquals(expected.size(), actual.size(), "Size mismatch after operation " + i);

			Assertions.assertEquals(expected, actual, "Contents mismatch after operation " + i);
		}
	}

	@Test
	@DisplayName("Stress test with different blade sizes")
	void testDifferentBladeSizes() {
		final int[] bladeSizes = { 1, 2, 3, 4, 8, 16, 32, 64, 100 };

		for (final int bladeSize : bladeSizes) {
			final LinkedArrayList<Integer> actual = new LinkedArrayList<>(bladeSize);

			final List<Integer> expected = new ArrayList<>();

			for (int i = 0; i < 200; i++) {
				actual.add(i);
				expected.add(i);
			}

			for (int i = 0; i < 50; i++) {
				final int index = expected.size() / 2;

				Assertions.assertEquals(expected.remove(index), actual.remove(index));
			}

			for (int i = 0; i < 50; i++) {
				final int index = expected.size() / 2;

				actual.add(index, -i);
				expected.add(index, -i);
			}

			Assertions.assertEquals(expected, actual, "Failed with blade size " + bladeSize);
		}
	}

	@Test
	@DisplayName("Pack fills earlier blades and removes unnecessary blades")
	void testPack() throws Exception {
		final LinkedArrayList<Integer> list = new LinkedArrayList<>(4);

		for (int i = 0; i < 12; i++) {
			list.add(i);
		}

		// Produce partially filled blades.
		list.remove(1);
		list.remove(4);
		list.remove(7);

		final List<Integer> expected = new ArrayList<>(list);

		Assertions.assertArrayEquals(new int[] { 3, 3, 3 }, this.getBladeLengths(list));

		list.pack();

		Assertions.assertArrayEquals(new int[] { 4, 4, 1 }, this.getBladeLengths(list));

		Assertions.assertEquals(expected, list);
		Assertions.assertEquals(9, list.size());
	}

	@Test
	@DisplayName("Pack preserves element order")
	void testPackPreservesOrder() {
		final LinkedArrayList<Integer> list = new LinkedArrayList<>(4);
		final List<Integer> expected = new ArrayList<>();

		for (int i = 0; i < 30; i++) {
			list.add(i);
			expected.add(i);
		}

		for (int i = 0; i < 10; i++) {
			final int index = i * 2;

			Assertions.assertEquals(expected.remove(index), list.remove(index));
		}

		list.pack();

		Assertions.assertEquals(expected, list);
	}

	@Test
	@DisplayName("Pack reduces the number of blades")
	void testPackReducesBladeCount() throws Exception {
		final LinkedArrayList<Integer> list = new LinkedArrayList<>(4);

		for (int i = 0; i < 20; i++) {
			list.add(i);
		}

		// Leave two elements in each original blade.
		for (int i = 0; i < 5; i++) {
			list.remove(i * 2);
			list.remove(i * 2);
		}

		Assertions.assertEquals(5, this.getBlades(list).size());

		list.pack();

		// 10 elements with m=4 require only 3 blades.
		Assertions.assertEquals(3, this.getBlades(list).size());

		Assertions.assertArrayEquals(new int[] { 4, 4, 2 }, this.getBladeLengths(list));
	}

	@Test
	@DisplayName("Pack does not change an already packed list")
	void testPackAlreadyPacked() throws Exception {
		final LinkedArrayList<Integer> list = new LinkedArrayList<>(4);

		for (int i = 0; i < 10; i++) {
			list.add(i);
		}

		final int[] originalLengths = this.getBladeLengths(list);
		final List<Integer> expected = new ArrayList<>(list);

		list.pack();

		Assertions.assertArrayEquals(originalLengths, this.getBladeLengths(list));
		Assertions.assertEquals(expected, list);
	}

	@Test
	@DisplayName("Pack handles an empty list")
	void testPackEmptyList() throws Exception {
		final LinkedArrayList<Integer> list = new LinkedArrayList<>(4);

		Assertions.assertDoesNotThrow(list::pack);

		Assertions.assertEquals(0, list.size());
		Assertions.assertTrue(this.getBlades(list).isEmpty());
	}

	@Test
	@DisplayName("Pack is idempotent")
	void testPackIdempotent() throws Exception {
		final LinkedArrayList<Integer> list = new LinkedArrayList<>(4);

		for (int i = 0; i < 20; i++) {
			list.add(i);
		}

		for (int i = 0; i < 8; i++) {
			list.remove(i);
		}

		list.pack();

		final int[] firstLengths = this.getBladeLengths(list);
		final int[] firstCapacities = this.getBladeCapacities(list);
		final List<Integer> expected = new ArrayList<>(list);

		list.pack();

		Assertions.assertArrayEquals(firstLengths, this.getBladeLengths(list));
		Assertions.assertArrayEquals(firstCapacities, this.getBladeCapacities(list));
		Assertions.assertEquals(expected, list);
	}

	@Test
	@DisplayName("Compact packs and trims all blades")
	void testCompact() throws Exception {
		final LinkedArrayList<Integer> list = new LinkedArrayList<>(4);

		for (int i = 0; i < 12; i++) {
			list.add(i);
		}

		list.remove(1);
		list.remove(4);
		list.remove(7);

		final List<Integer> expected = new ArrayList<>(list);

		list.compact();

		Assertions.assertArrayEquals(new int[] { 4, 4, 1 }, this.getBladeLengths(list));

		Assertions.assertArrayEquals(new int[] { 4, 4, 1 }, this.getBladeCapacities(list));

		Assertions.assertEquals(3, this.getBlades(list).size());
		Assertions.assertEquals(expected, list);
	}

	@Test
	@DisplayName("Compact preserves all elements")
	void testCompactPreservesElements() {
		final LinkedArrayList<Integer> list = new LinkedArrayList<>(16);
		final List<Integer> expected = new ArrayList<>();

		for (int i = 0; i < 500; i++) {
			list.add(i);
			expected.add(i);
		}

		final Random random = new Random(12345);

		for (int i = 0; i < 200; i++) {
			final int index = random.nextInt(expected.size());

			Assertions.assertEquals(expected.remove(index), list.remove(index));
		}

		list.compact();

		Assertions.assertEquals(expected, list);
	}

	@Test
	@DisplayName("Compact produces the minimum number of blades")
	void testCompactMinimumBladeCount() throws Exception {
		final int[] bladeSizes = { 1, 2, 3, 4, 8, 16, 32, 64 };

		for (final int maxBladeSize : bladeSizes) {
			final LinkedArrayList<Integer> list = new LinkedArrayList<>(maxBladeSize);

			for (int i = 0; i < 200; i++) {
				list.add(i);
			}

			for (int i = 0; i < 75; i++) {
				list.remove(i);
			}

			list.compact();

			final int expectedBladeCount = (list.size() + maxBladeSize - 1) / maxBladeSize;

			Assertions.assertEquals(expectedBladeCount, this.getBlades(list).size(), "Incorrect blade count for m=" + maxBladeSize);

			Assertions.assertArrayEquals(this.getBladeLengths(list), this.getBladeCapacities(list));
		}
	}

	@Test
	@DisplayName("List remains usable after compact")
	void testOperationsAfterCompact() {
		final LinkedArrayList<Integer> list = new LinkedArrayList<>(4);
		final List<Integer> expected = new ArrayList<>();

		for (int i = 0; i < 20; i++) {
			list.add(i);
			expected.add(i);
		}

		for (int i = 0; i < 5; i++) {
			list.remove(2);
			expected.remove(2);
		}

		list.compact();

		list.add(0, 99);
		expected.add(0, 99);

		list.add(42);
		expected.add(42);

		list.remove(3);
		expected.remove(3);

		list.set(5, 123);
		expected.set(5, 123);

		Assertions.assertEquals(expected, list);
	}

	@Test
	@DisplayName("Compact is idempotent")
	void testCompactIdempotent() throws Exception {
		final LinkedArrayList<Integer> list = new LinkedArrayList<>(4);

		for (int i = 0; i < 30; i++) {
			list.add(i);
		}

		for (int i = 0; i < 10; i++) {
			list.remove(i);
		}

		list.compact();

		final int[] lengths = this.getBladeLengths(list);
		final int[] capacities = this.getBladeCapacities(list);
		final List<Integer> expected = new ArrayList<>(list);

		list.compact();

		Assertions.assertArrayEquals(lengths, this.getBladeLengths(list));
		Assertions.assertArrayEquals(capacities, this.getBladeCapacities(list));
		Assertions.assertEquals(expected, list);
	}

	@Test
	@DisplayName("Shallow clone preserves elements and structure")
	void testClone() {
		final LinkedArrayList<StringBuilder> original = new LinkedArrayList<>(4);

		final StringBuilder element = new StringBuilder("Original");

		original.add(element);
		original.add(new StringBuilder("Second"));

		final LinkedArrayList<StringBuilder> clone = original.clone();

		Assertions.assertNotSame(original, clone);
		Assertions.assertEquals(original.size(), clone.size());
		Assertions.assertEquals(original.getMaxBladeSize(), clone.getMaxBladeSize());

		// The same element references are shared.
		Assertions.assertSame(original.get(0), clone.get(0));

		// Structural changes are independent.
		clone.remove(0);

		Assertions.assertEquals(2, original.size());
		Assertions.assertEquals(1, clone.size());

		// Mutating a shared element affects both lists.
		element.append(" modified");

		Assertions.assertEquals("Original modified", original.get(0).toString());
	}

	@Test
	@DisplayName("Clone preserves null elements")
	void testCloneWithNull() {
		final LinkedArrayList<String> original = new LinkedArrayList<>(4);

		original.add("A");
		original.add(null);
		original.add("B");

		final LinkedArrayList<String> clone = original.clone();

		Assertions.assertEquals(original, clone);
		Assertions.assertNotSame(original, clone);
		Assertions.assertNull(clone.get(1));
	}

	@Test
	@DisplayName("Clone remains independent after modifications")
	void testCloneIndependence() {
		final LinkedArrayList<Integer> original = new LinkedArrayList<>(4);

		for (int i = 0; i < 20; i++) {
			original.add(i);
		}

		original.remove(2);
		original.remove(5);

		final LinkedArrayList<Integer> clone = original.clone();

		clone.add(99);
		clone.remove(0);
		clone.pack();
		clone.trimToSize();

		Assertions.assertEquals(18, original.size());
		Assertions.assertEquals(18, clone.size());

		Assertions.assertNotEquals(original, clone);
	}

	@Test
	@DisplayName("Deep clone creates independent DeepCloneable elements")
	void testDeepClone() {
		class TestElement implements Cloneable, DeepCloneable {
			int value;

			TestElement(final int value) {
				this.value = value;
			}

			@Override
			public TestElement clone() {
				try {
					return (TestElement) super.clone();
				} catch (CloneNotSupportedException e) {
					throw new AssertionError(null, e);
				}
			}

			@Override
			public TestElement deepClone() {
				return new TestElement(this.value);
			}
		}

		final LinkedArrayList<TestElement> original = new LinkedArrayList<>(4);

		original.add(new TestElement(10));
		original.add(new TestElement(20));

		final LinkedArrayList<TestElement> clone = original.deepClone();

		Assertions.assertNotSame(original, clone);
		Assertions.assertNotSame(original.get(0), clone.get(0));
		Assertions.assertNotSame(original.get(1), clone.get(1));

		Assertions.assertEquals(10, clone.get(0).value);
		Assertions.assertEquals(20, clone.get(1).value);

		clone.get(0).value = 99;

		Assertions.assertEquals(10, original.get(0).value);
		Assertions.assertEquals(99, clone.get(0).value);
	}

	@Test
	@DisplayName("Deep clone preserves null elements")
	void testDeepCloneWithNull() {
		final LinkedArrayList<String> original = new LinkedArrayList<>(4);

		original.add(null);
		original.add("A");
		original.add(null);

		final LinkedArrayList<String> clone = original.deepClone();

		Assertions.assertEquals(original, clone);
		Assertions.assertNotSame(original, clone);
	}

	@Test
	@DisplayName("Cloning an empty list")
	void testCloneEmptyList() {
		final LinkedArrayList<Integer> original = new LinkedArrayList<>(4);

		final LinkedArrayList<Integer> shallow = original.clone();
		final LinkedArrayList<Integer> deep = original.deepClone();

		Assertions.assertTrue(shallow.isEmpty());
		Assertions.assertTrue(deep.isEmpty());

		Assertions.assertNotSame(original, shallow);
		Assertions.assertNotSame(original, deep);
	}

	private List<Object> getBlades(final LinkedArrayList<?> list) throws Exception {
		final List<Object> blades = new ArrayList<>();

		final Field headField = LinkedArrayList.class.getDeclaredField("head");
		headField.setAccessible(true);

		Object current = headField.get(list);

		if (current == null) {
			return blades;
		}

		final Field nextField = current.getClass().getDeclaredField("next");
		nextField.setAccessible(true);

		while (current != null) {
			blades.add(current);
			current = nextField.get(current);
		}

		return blades;
	}

	private int[] getBladeLengths(final LinkedArrayList<?> list) throws Exception {
		final List<Object> blades = this.getBlades(list);
		final int[] lengths = new int[blades.size()];

		for (int i = 0; i < blades.size(); i++) {
			final Field lengthField = blades.get(i).getClass().getDeclaredField("length");
			lengthField.setAccessible(true);

			lengths[i] = lengthField.getInt(blades.get(i));
		}

		return lengths;
	}

	private int[] getBladeCapacities(final LinkedArrayList<?> list) throws Exception {
		final List<Object> blades = this.getBlades(list);
		final int[] capacities = new int[blades.size()];

		for (int i = 0; i < blades.size(); i++) {
			final Field elementsField = blades.get(i).getClass().getDeclaredField("elements");
			elementsField.setAccessible(true);

			final Object[] elements = (Object[]) elementsField.get(blades.get(i));
			capacities[i] = elements.length;
		}

		return capacities;
	}

}
