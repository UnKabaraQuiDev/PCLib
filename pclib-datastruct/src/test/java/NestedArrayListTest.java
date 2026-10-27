
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
import lu.kbra.pclib.datastructure.list.NestedArrayList;

public class NestedArrayListTest {

	private NestedArrayList<Integer> list;

	@BeforeEach
	void setUp() {
		this.list = new NestedArrayList<>(4);
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
		final NestedArrayList<Integer> defaultList = new NestedArrayList<>();

		Assertions.assertTrue(defaultList.isEmpty());
		Assertions.assertTrue(defaultList.getMaxBladeSize() > 0);
	}

	@Test
	@DisplayName("Custom maximum blade size")
	void testCustomBladeSize() {
		final NestedArrayList<Integer> custom = new NestedArrayList<>(8);

		Assertions.assertEquals(8, custom.getMaxBladeSize());
	}

	@Test
	@DisplayName("Reject invalid maximum blade sizes")
	void testInvalidBladeSize() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new NestedArrayList<>(0));
		Assertions.assertThrows(IllegalArgumentException.class, () -> new NestedArrayList<>(-1));
	}

	@Test
	@DisplayName("Constructor with initial size")
	void testInitialSize() {
		final NestedArrayList<Integer> initialized = new NestedArrayList<>(10, 4);

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

		final Field elementsField = NestedArrayList.class.getDeclaredField("elements");
		elementsField.setAccessible(true);

		final Object[][] elements = (Object[][]) elementsField.get(this.list);
		final Object[] originalArray = elements[0];
		final int originalCapacity = originalArray.length;

		this.list.remove(1);

		final Object[][] currentElements = (Object[][]) elementsField.get(this.list);
		final Object[] currentArray = currentElements[0];

		Assertions.assertSame(originalArray, currentArray, "Removing an element must not replace the blade array");

		Assertions.assertEquals(originalCapacity, currentArray.length);
		Assertions.assertArrayEquals(new int[] { 3 }, this.getBladeLengths(this.list));
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
		final NestedArrayList<Integer> small = new NestedArrayList<>(1);

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

		final NestedArrayList<Integer> actual = new NestedArrayList<>(4);
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
			final NestedArrayList<Integer> actual = new NestedArrayList<>(bladeSize);

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
		final NestedArrayList<Integer> list = new NestedArrayList<>(4);

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
		final NestedArrayList<Integer> list = new NestedArrayList<>(4);
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
		final NestedArrayList<Integer> list = new NestedArrayList<>(4);

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
		final NestedArrayList<Integer> list = new NestedArrayList<>(4);

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
		final NestedArrayList<Integer> list = new NestedArrayList<>(4);

		Assertions.assertDoesNotThrow(list::pack);

		Assertions.assertEquals(0, list.size());
		Assertions.assertTrue(this.getBlades(list).isEmpty());
	}

	@Test
	@DisplayName("Pack is idempotent")
	void testPackIdempotent() throws Exception {
		final NestedArrayList<Integer> list = new NestedArrayList<>(4);

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
		final NestedArrayList<Integer> list = new NestedArrayList<>(4);

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
		final NestedArrayList<Integer> list = new NestedArrayList<>(16);
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
			final NestedArrayList<Integer> list = new NestedArrayList<>(maxBladeSize);

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
		final NestedArrayList<Integer> list = new NestedArrayList<>(4);
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
		final NestedArrayList<Integer> list = new NestedArrayList<>(4);

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
	@DisplayName("Cumulative lengths are correct after adding elements")
	void testCumulativeLengthsAfterAdd() throws Exception {
		for (int i = 0; i < 10; i++) {
			this.list.add(i);
		}

		Assertions.assertArrayEquals(new int[] { 4, 4, 2 }, this.getBladeLengths(this.list));

		Assertions.assertArrayEquals(new int[] { 4, 8, 10 }, this.getCumulativeLengths(this.list));
	}

	@Test
	@DisplayName("Cumulative lengths are updated after removing elements")
	void testCumulativeLengthsAfterRemove() throws Exception {
		for (int i = 0; i < 12; i++) {
			this.list.add(i);
		}

		this.list.remove(1);
		this.list.remove(4);
		this.list.remove(7);

		Assertions.assertArrayEquals(new int[] { 3, 3, 3 }, this.getBladeLengths(this.list));

		Assertions.assertArrayEquals(new int[] { 3, 6, 9 }, this.getCumulativeLengths(this.list));
	}

	@Test
	@DisplayName("Cumulative lengths are updated after inserting into a full blade")
	void testCumulativeLengthsAfterInsert() throws Exception {
		this.list.addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));

		this.list.add(2, 99);

		Assertions.assertArrayEquals(new int[] { 4, 1, 4 }, this.getBladeLengths(this.list));

		Assertions.assertArrayEquals(new int[] { 4, 5, 9 }, this.getCumulativeLengths(this.list));

		Assertions.assertEquals(Arrays.asList(0, 1, 99, 2, 3, 4, 5, 6, 7), this.list);
	}

	@Test
	@DisplayName("Packing recalculates cumulative lengths")
	void testCumulativeLengthsAfterPack() throws Exception {
		for (int i = 0; i < 12; i++) {
			this.list.add(i);
		}

		this.list.remove(1);
		this.list.remove(4);
		this.list.remove(7);

		Assertions.assertArrayEquals(new int[] { 3, 6, 9 }, this.getCumulativeLengths(this.list));

		this.list.pack();

		Assertions.assertArrayEquals(new int[] { 4, 4, 1 }, this.getBladeLengths(this.list));

		Assertions.assertArrayEquals(new int[] { 4, 8, 9 }, this.getCumulativeLengths(this.list));
	}

	@Test
	@DisplayName("Trimming does not modify cumulative lengths")
	void testCumulativeLengthsAfterTrim() throws Exception {
		for (int i = 0; i < 12; i++) {
			this.list.add(i);
		}

		this.list.remove(1);
		this.list.remove(4);

		final int[] originalCumulativeLengths = this.getCumulativeLengths(this.list);

		this.list.trimToSize();

		Assertions.assertArrayEquals(originalCumulativeLengths, this.getCumulativeLengths(this.list));

		Assertions.assertArrayEquals(this.getBladeLengths(this.list), this.getBladeCapacities(this.list));
	}

	@Test
	@DisplayName("All internal arrays remain synchronized")
	void testInternalArraySynchronization() throws Exception {
		final Random random = new Random(12345);
		final List<Integer> expected = new ArrayList<>();

		for (int i = 0; i < 1000; i++) {
			final int operation = expected.isEmpty() ? 0 : random.nextInt(6);

			switch (operation) {
			case 0: {
				final int value = random.nextInt(10000);
				this.list.add(value);
				expected.add(value);
				break;
			}

			case 1: {
				final int index = random.nextInt(expected.size() + 1);
				final int value = random.nextInt(10000);

				this.list.add(index, value);
				expected.add(index, value);
				break;
			}

			case 2: {
				final int index = random.nextInt(expected.size());

				Assertions.assertEquals(expected.remove(index), this.list.remove(index));
				break;
			}

			case 3:
				this.list.pack();
				break;

			case 4:
				this.list.trimToSize();
				break;

			case 5:
				this.list.compact();
				break;

			default:
				Assertions.fail("Unknown operation");
			}

			final int[] lengths = this.getBladeLengths(this.list);
			final int[] cumulative = this.getCumulativeLengths(this.list);
			final int[] capacities = this.getBladeCapacities(this.list);

			Assertions.assertEquals(lengths.length, cumulative.length);
			Assertions.assertEquals(lengths.length, capacities.length);

			int total = 0;

			for (int j = 0; j < lengths.length; j++) {
				Assertions.assertTrue(lengths[j] > 0, "Empty blade at operation " + i);

				Assertions.assertTrue(lengths[j] <= this.list.getMaxBladeSize(), "Blade exceeds maximum size at operation " + i);

				Assertions.assertTrue(capacities[j] >= lengths[j], "Blade capacity smaller than length at operation " + i);

				Assertions.assertTrue(capacities[j] <= this.list.getMaxBladeSize(), "Blade capacity exceeds maximum at operation " + i);

				total += lengths[j];

				Assertions.assertEquals(total, cumulative[j], "Invalid cumulative length at operation " + i);
			}

			Assertions.assertEquals(this.list.size(), total);
			Assertions.assertEquals(expected, this.list, "Contents mismatch at operation " + i);
		}
	}

	@Test
	@DisplayName("Shallow clone preserves elements and structure")
	void testClone() {
		final NestedArrayList<StringBuilder> original = new NestedArrayList<>(4);

		final StringBuilder element = new StringBuilder("Original");

		original.add(element);
		original.add(new StringBuilder("Second"));

		final NestedArrayList<StringBuilder> clone = original.clone();

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
		final NestedArrayList<String> original = new NestedArrayList<>(4);

		original.add("A");
		original.add(null);
		original.add("B");

		final NestedArrayList<String> clone = original.clone();

		Assertions.assertEquals(original, clone);
		Assertions.assertNotSame(original, clone);
		Assertions.assertNull(clone.get(1));
	}

	@Test
	@DisplayName("Clone remains independent after modifications")
	void testCloneIndependence() {
		final NestedArrayList<Integer> original = new NestedArrayList<>(4);

		for (int i = 0; i < 20; i++) {
			original.add(i);
		}

		original.remove(2);
		original.remove(5);

		final NestedArrayList<Integer> clone = original.clone();

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
				} catch (final CloneNotSupportedException e) {
					throw new AssertionError(null, e);
				}
			}

			@Override
			public TestElement deepClone() {
				return new TestElement(this.value);
			}
		}

		final NestedArrayList<TestElement> original = new NestedArrayList<>(4);

		original.add(new TestElement(10));
		original.add(new TestElement(20));

		final NestedArrayList<TestElement> clone = original.deepClone();

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
		final NestedArrayList<String> original = new NestedArrayList<>(4);

		original.add(null);
		original.add("A");
		original.add(null);

		final NestedArrayList<String> clone = original.deepClone();

		Assertions.assertEquals(original, clone);
		Assertions.assertNotSame(original, clone);
	}

	@Test
	@DisplayName("Cloning an empty list")
	void testCloneEmptyList() {
		final NestedArrayList<Integer> original = new NestedArrayList<>(4);

		final NestedArrayList<Integer> shallow = original.clone();
		final NestedArrayList<Integer> deep = original.deepClone();

		Assertions.assertTrue(shallow.isEmpty());
		Assertions.assertTrue(deep.isEmpty());

		Assertions.assertNotSame(original, shallow);
		Assertions.assertNotSame(original, deep);
	}

	private List<Object> getBlades(final NestedArrayList<?> list) throws Exception {
		final Field elementsField = NestedArrayList.class.getDeclaredField("elements");
		elementsField.setAccessible(true);

		final Field bladeCountField = NestedArrayList.class.getDeclaredField("bladeCount");
		bladeCountField.setAccessible(true);

		final Object[][] elements = (Object[][]) elementsField.get(list);
		final int bladeCount = bladeCountField.getInt(list);

		final List<Object> blades = new ArrayList<>(bladeCount);

		for (int i = 0; i < bladeCount; i++) {
			blades.add(elements[i]);
		}

		return blades;
	}

	private int[] getBladeLengths(final NestedArrayList<?> list) throws Exception {
		final Field lengthsField = NestedArrayList.class.getDeclaredField("lengths");
		lengthsField.setAccessible(true);

		final Field bladeCountField = NestedArrayList.class.getDeclaredField("bladeCount");
		bladeCountField.setAccessible(true);

		final int[] lengths = (int[]) lengthsField.get(list);
		final int bladeCount = bladeCountField.getInt(list);

		return Arrays.copyOf(lengths, bladeCount);
	}

	private int[] getBladeCapacities(final NestedArrayList<?> list) throws Exception {
		final Field elementsField = NestedArrayList.class.getDeclaredField("elements");
		elementsField.setAccessible(true);

		final Field bladeCountField = NestedArrayList.class.getDeclaredField("bladeCount");
		bladeCountField.setAccessible(true);

		final Object[][] elements = (Object[][]) elementsField.get(list);
		final int bladeCount = bladeCountField.getInt(list);

		final int[] capacities = new int[bladeCount];

		for (int i = 0; i < bladeCount; i++) {
			capacities[i] = elements[i].length;
		}

		return capacities;
	}

	private int[] getCumulativeLengths(final NestedArrayList<?> list) throws Exception {
		final Field cumulativeField = NestedArrayList.class.getDeclaredField("cumulativeLengths");
		cumulativeField.setAccessible(true);

		final Field bladeCountField = NestedArrayList.class.getDeclaredField("bladeCount");
		bladeCountField.setAccessible(true);

		final int[] cumulativeLengths = (int[]) cumulativeField.get(list);
		final int bladeCount = bladeCountField.getInt(list);

		return Arrays.copyOf(cumulativeLengths, bladeCount);
	}

}
