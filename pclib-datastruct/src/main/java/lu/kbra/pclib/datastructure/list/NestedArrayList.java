package lu.kbra.pclib.datastructure.list;

import java.util.AbstractList;
import java.util.Arrays;

import lu.kbra.pclib.datastructure.DeepCloneable;

/**
 * A list backed by dynamically growing arrays of blade references, blade lengths, and cumulative
 * lengths. Each blade holds at most {@code m} elements. Removing elements does not shrink the
 * underlying arrays or rebalance the blades.
 *
 * <p>
 * <b>Time complexity:</b>
 * </p>
 * <table border="1">
 * <tr>
 * <th>Operation</th>
 * <th>Average</th>
 * <th>Worst</th>
 * </tr>
 * <tr>
 * <td>get(index)</td>
 * <td>O(log b)</td>
 * <td>O(log b)</td>
 * </tr>
 * <tr>
 * <td>set(index, value)</td>
 * <td>O(log b)</td>
 * <td>O(log b)</td>
 * </tr>
 * <tr>
 * <td>add(value)</td>
 * <td>O(1) amortized</td>
 * <td>O(b + m)</td>
 * </tr>
 * <tr>
 * <td>add(index, value)</td>
 * <td>O(b + m)</td>
 * <td>O(b + m)</td>
 * </tr>
 * <tr>
 * <td>remove(index)</td>
 * <td>O(b + m)</td>
 * <td>O(b + m)</td>
 * </tr>
 * <tr>
 * <td>size()</td>
 * <td>O(1)</td>
 * <td>O(1)</td>
 * </tr>
 * <tr>
 * <td>clear()</td>
 * <td>O(1)</td>
 * <td>O(1)</td>
 * </tr>
 * <tr>
 * <td>trimToSize()</td>
 * <td>O(n + b)</td>
 * <td>O(n + b)</td>
 * </tr>
 * <tr>
 * <td>pack()</td>
 * <td>O(n + b)</td>
 * <td>O(n + b)</td>
 * </tr>
 * <tr>
 * <td>compact()</td>
 * <td>O(n + b)</td>
 * <td>O(n + b)</td>
 * </tr>
 * </table>
 *
 * <ul>
 * <li><b>n</b> - Total number of elements in the list ({@code size()}).</li>
 * <li><b>m</b> - Maximum number of elements per blade ({@code maxBladeSize}).</li>
 * <li><b>b</b> - Current number of blades in the list.</li>
 * </ul>
 *
 * <p>
 * When fully packed, the number of blades is {@code b = ceil(n / m)}. After deletions, partially
 * filled blades may increase {@code b}.
 * </p>
 *
 * <p>
 * <b>Example:</b> With {@code n = 1000} and {@code m = 256}, the list requires {@code b = 4} blades
 * when fully packed (256 + 256 + 256 + 232 elements).
 * </p>
 *
 * <p>
 * Indexed access uses binary search over cumulative blade lengths. Appending is amortized constant
 * time. Indexed insertion and removal may require shifting blade references, updating cumulative
 * lengths, and moving up to {@code m} elements within a blade.
 * </p>
 *
 * @param <T> the type of elements in this list
 */
public class NestedArrayList<T> extends AbstractList<T> implements Cloneable, DeepCloneable {

	private static final int DEFAULT_MAX_BLADE_SIZE = 256;
	private static final int INITIAL_CAPACITY = 4;

	private final int maxBladeSize;

	private Object[][] elements = new Object[0][];
	private int[] lengths = new int[0];
	private int[] cumulativeLengths = new int[0];

	private int bladeCount = 0;
	private int size = 0;

	/** Creates an empty list with the default maximum blade size. */
	public NestedArrayList() {
		this(0, NestedArrayList.DEFAULT_MAX_BLADE_SIZE);
	}

	/** Creates an empty list with the specified maximum number of elements per blade. */
	public NestedArrayList(final int maxBladeSize) {
		this(0, maxBladeSize);
	}

	/** Creates a list with the specified number of null elements and maximum blade size. */
	public NestedArrayList(final int initialSize, final int maxBladeSize) {
		if (maxBladeSize <= 0) {
			throw new IllegalArgumentException("Maximum blade size must be positive");
		}

		if (initialSize < 0) {
			throw new IllegalArgumentException("Initial size cannot be negative");
		}

		this.maxBladeSize = maxBladeSize;
		this.size = initialSize;

		final int requiredBlades = initialSize / maxBladeSize + (initialSize % maxBladeSize == 0 ? 0 : 1);

		this.ensureBladeCapacity(requiredBlades);

		int remaining = initialSize;
		int cumulative = 0;

		while (remaining > 0) {
			final int length = Math.min(remaining, maxBladeSize);

			this.elements[this.bladeCount] = new Object[length];
			this.lengths[this.bladeCount] = length;

			cumulative += length;
			this.cumulativeLengths[this.bladeCount] = cumulative;

			this.bladeCount++;
			remaining -= length;
		}
	}

	/**
	 * Creates a list containing the specified number of null elements using the default maximum blade
	 * size.
	 */
	public static <T> NestedArrayList<T> withInitialSize(final int initialSize) {
		return new NestedArrayList<>(initialSize, NestedArrayList.DEFAULT_MAX_BLADE_SIZE);
	}

	/**
	 * Ensures that the three parallel arrays can hold the required number of blades.
	 */
	private void ensureBladeCapacity(final int required) {
		if (required <= this.elements.length) {
			return;
		}

		final int newCapacity = Math.max(required, Math.max(NestedArrayList.INITIAL_CAPACITY, this.elements.length * 2));

		this.elements = Arrays.copyOf(this.elements, newCapacity);

		this.lengths = Arrays.copyOf(this.lengths, newCapacity);

		this.cumulativeLengths = Arrays.copyOf(this.cumulativeLengths, newCapacity);
	}

	/**
	 * Ensures that a blade has enough element capacity.
	 */
	private void ensureElementCapacity(final int blade, final int required) {

		if (required <= this.elements[blade].length) {
			return;
		}

		final int newCapacity = Math.min(this.maxBladeSize, Math.max(required, this.elements[blade].length * 2));

		this.elements[blade] = Arrays.copyOf(this.elements[blade], newCapacity);
	}

	/**
	 * Finds the blade containing the specified element index.
	 */
	private int findBlade(final int index) {
		if (index < 0 || index >= this.size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
		}

		// Find the first cumulative length greater than index.
		int low = 0;
		int high = this.bladeCount - 1;

		while (low < high) {
			final int middle = low + high >>> 1;

			if (index < this.cumulativeLengths[middle]) {
				high = middle;
			} else {
				low = middle + 1;
			}
		}

		return low;
	}

	/**
	 * Returns the element's local index within its blade.
	 */
	private int getOffset(final int blade, final int index) {
		final int previousLength = blade == 0 ? 0 : this.cumulativeLengths[blade - 1];

		return index - previousLength;
	}

	/**
	 * Recalculates cumulative lengths starting at a blade.
	 */
	private void updateCumulativeLengths(final int start) {
		int cumulative = start == 0 ? 0 : this.cumulativeLengths[start - 1];

		for (int i = start; i < this.bladeCount; i++) {
			cumulative += this.lengths[i];
			this.cumulativeLengths[i] = cumulative;
		}
	}

	/**
	 * Inserts a new blade at the specified position.
	 */
	private void insertBlade(final int index, final int capacity) {
		this.ensureBladeCapacity(this.bladeCount + 1);

		final int moveCount = this.bladeCount - index;

		if (moveCount > 0) {
			System.arraycopy(this.elements, index, this.elements, index + 1, moveCount);

			System.arraycopy(this.lengths, index, this.lengths, index + 1, moveCount);

			System.arraycopy(this.cumulativeLengths, index, this.cumulativeLengths, index + 1, moveCount);
		}

		this.elements[index] = new Object[capacity];
		this.lengths[index] = 0;
		this.cumulativeLengths[index] = 0;
		this.bladeCount++;
	}

	/**
	 * Removes an empty blade from the parallel arrays.
	 */
	private void removeBlade(final int index) {
		final int moveCount = this.bladeCount - index - 1;

		if (moveCount > 0) {
			System.arraycopy(this.elements, index + 1, this.elements, index, moveCount);

			System.arraycopy(this.lengths, index + 1, this.lengths, index, moveCount);

			System.arraycopy(this.cumulativeLengths, index + 1, this.cumulativeLengths, index, moveCount);
		}

		this.bladeCount--;

		this.elements[this.bladeCount] = null;
		this.lengths[this.bladeCount] = 0;
		this.cumulativeLengths[this.bladeCount] = 0;
	}

	/** Returns the element at the specified index. */
	@Override
	@SuppressWarnings("unchecked")
	public T get(final int index) {
		final int blade = this.findBlade(index);
		final int offset = this.getOffset(blade, index);

		return (T) this.elements[blade][offset];
	}

	/** Replaces the element at the specified index and returns its previous value. */
	@Override
	@SuppressWarnings("unchecked")
	public T set(final int index, final T element) {
		final int blade = this.findBlade(index);
		final int offset = this.getOffset(blade, index);

		final T previous = (T) this.elements[blade][offset];

		this.elements[blade][offset] = element;

		return previous;
	}

	/** Appends an element to the end of the list. */
	@Override
	public boolean add(final T element) {
		if (this.bladeCount == 0 || this.lengths[this.bladeCount - 1] == this.maxBladeSize) {

			this.insertBlade(this.bladeCount, Math.min(NestedArrayList.INITIAL_CAPACITY, this.maxBladeSize));
		}

		final int blade = this.bladeCount - 1;
		final int length = this.lengths[blade];

		this.ensureElementCapacity(blade, length + 1);

		this.elements[blade][length] = element;
		this.lengths[blade]++;

		this.size++;
		this.cumulativeLengths[blade] = this.size;
		this.modCount++;

		return true;
	}

	/** Inserts an element at the specified index, shifting subsequent elements to the right. */
	@Override
	public void add(final int index, final T element) {
		if (index < 0 || index > this.size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
		}

		if (index == this.size) {
			this.add(element);
			return;
		}

		final int blade = this.findBlade(index);
		final int offset = this.getOffset(blade, index);

		if (this.lengths[blade] == this.maxBladeSize) {

			// Move the last element into a new blade.
			final Object last = this.elements[blade][this.lengths[blade] - 1];

			this.insertBlade(blade + 1, Math.min(NestedArrayList.INITIAL_CAPACITY, this.maxBladeSize));

			this.elements[blade + 1][0] = last;
			this.lengths[blade + 1] = 1;

			this.elements[blade][--this.lengths[blade]] = null;
		}

		final int length = this.lengths[blade];

		this.ensureElementCapacity(blade, length + 1);

		System.arraycopy(this.elements[blade], offset, this.elements[blade], offset + 1, length - offset);

		this.elements[blade][offset] = element;
		this.lengths[blade]++;

		this.size++;
		this.updateCumulativeLengths(blade);
		this.modCount++;
	}

	/** Removes and returns the element at the specified index without shrinking blade arrays. */
	@Override
	@SuppressWarnings("unchecked")
	public T remove(final int index) {
		final int blade = this.findBlade(index);
		final int offset = this.getOffset(blade, index);

		final T removed = (T) this.elements[blade][offset];

		final int length = this.lengths[blade];

		// Shift elements without resizing the blade.
		System.arraycopy(this.elements[blade], offset + 1, this.elements[blade], offset, length - offset - 1);

		this.elements[blade][--this.lengths[blade]] = null;

		// Remove empty blades.
		if (this.lengths[blade] == 0) {
			this.removeBlade(blade);
		}

		this.size--;
		this.updateCumulativeLengths(blade);
		this.modCount++;

		return removed;
	}

	/** Returns the total number of elements in the list. */
	@Override
	public int size() {
		return this.size;
	}

	/** Removes all elements and releases all blades. */
	@Override
	public void clear() {
		this.elements = new Object[0][];
		this.lengths = new int[0];
		this.cumulativeLengths = new int[0];

		this.bladeCount = 0;
		this.size = 0;
		this.modCount++;
	}

	/** Returns the maximum number of elements that a single blade can hold. */
	public int getMaxBladeSize() {
		return this.maxBladeSize;
	}

	/** Shrinks each blade's array to its logical length without moving elements between blades. */
	public void trimToSize() {
		for (int i = 0; i < this.bladeCount; i++) {
			if (this.elements[i].length != this.lengths[i]) {
				this.elements[i] = Arrays.copyOf(this.elements[i], this.lengths[i]);
			}
		}
	}

	/**
	 * Moves elements into earlier blades to minimize the number of blades while preserving element
	 * order.
	 */
	public void pack() {
		if (this.bladeCount <= 1) {
			return;
		}

		final int requiredBlades = this.size / this.maxBladeSize + (this.size % this.maxBladeSize == 0 ? 0 : 1);

		int destinationBlade = 0;
		int destinationOffset = 0;

		// Read all elements in order and write them into earlier blades.
		// Original blade lengths stay unchanged during this pass.
		for (int sourceBlade = 0; sourceBlade < this.bladeCount; sourceBlade++) {

			final int sourceLength = this.lengths[sourceBlade];

			for (int sourceOffset = 0; sourceOffset < sourceLength; sourceOffset++) {

				if (destinationOffset == this.maxBladeSize) {
					destinationBlade++;
					destinationOffset = 0;
				}

				this.ensureElementCapacity(destinationBlade, destinationOffset + 1);

				this.elements[destinationBlade][destinationOffset] = this.elements[sourceBlade][sourceOffset];

				destinationOffset++;
			}
		}

		boolean modified = this.bladeCount != requiredBlades;

		// Set the new blade lengths and clear unused references.
		int remaining = this.size;

		for (int i = 0; i < requiredBlades; i++) {
			final int newLength = Math.min(remaining, this.maxBladeSize);

			if (this.lengths[i] != newLength) {
				modified = true;
			}

			Arrays.fill(this.elements[i], newLength, this.elements[i].length, null);

			this.lengths[i] = newLength;
			remaining -= newLength;
		}

		// Release unused blades.
		for (int i = requiredBlades; i < this.bladeCount; i++) {
			this.elements[i] = null;
			this.lengths[i] = 0;
			this.cumulativeLengths[i] = 0;
		}

		this.bladeCount = requiredBlades;
		this.updateCumulativeLengths(0);

		if (modified) {
			this.modCount++;
		}
	}

	/**
	 * Packs the elements into the fewest blades and trims all remaining arrays to their logical
	 * lengths.
	 */
	public void compact() {
		this.pack();
		this.trimToSize();
	}

	/** Creates a shallow copy with independent internal arrays but shared element references. */
	@Override
	public NestedArrayList<T> clone() {
		final NestedArrayList<T> clone = new NestedArrayList<>(this.maxBladeSize);

		clone.size = this.size;
		clone.bladeCount = this.bladeCount;

		clone.elements = Arrays.copyOf(this.elements, this.elements.length);
		clone.lengths = Arrays.copyOf(this.lengths, this.lengths.length);
		clone.cumulativeLengths = Arrays.copyOf(this.cumulativeLengths, this.cumulativeLengths.length);

		for (int i = 0; i < this.bladeCount; i++) {
			clone.elements[i] = Arrays.copyOf(this.elements[i], this.elements[i].length);
		}

		return clone;
	}

	/** Creates a copy with independent internal arrays and cloneable elements. */
	@Override
	public NestedArrayList<T> deepClone() {
		final NestedArrayList<T> clone = this.clone();

		for (int blade = 0; blade < clone.bladeCount; blade++) {
			for (int i = 0; i < clone.lengths[blade]; i++) {
				final Object element = clone.elements[blade][i];

				if (element instanceof DeepCloneable) {
					clone.elements[blade][i] = ((DeepCloneable) element).deepClone();
				}
			}
		}

		return clone;
	}

}
