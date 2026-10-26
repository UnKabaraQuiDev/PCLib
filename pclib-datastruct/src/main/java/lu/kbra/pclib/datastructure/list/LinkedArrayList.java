package lu.kbra.pclib.datastructure.list;

import java.util.AbstractList;
import java.util.Arrays;

import lu.kbra.pclib.datastructure.DeepCloneable;

/**
 * A list backed by a linked chain of dynamically growing arrays (blades). Each blade holds at most
 * {@code m} elements. Removing elements does not shrink the underlying arrays or rebalance the
 * blades.
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
 * <td>O(b)</td>
 * <td>O(b)</td>
 * </tr>
 * <tr>
 * <td>set(index, value)</td>
 * <td>O(b)</td>
 * <td>O(b)</td>
 * </tr>
 * <tr>
 * <td>add(value)</td>
 * <td>O(1) amortized</td>
 * <td>O(m)</td>
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
 * <td>O(n * m)</td>
 * <td>O(n * m)</td>
 * </tr>
 * <tr>
 * <td>compact()</td>
 * <td>O(n * m)</td>
 * <td>O(n * m)</td>
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
 * Indexed access traverses the blades sequentially. Appending is amortized constant time. Indexed
 * insertion and removal may require shifting up to {@code m} elements within a blade.
 * </p>
 *
 * @param <T> the type of elements in this list
 */
public class LinkedArrayList<T> extends AbstractList<T> implements Cloneable, DeepCloneable {

	private static final int DEFAULT_MAX_BLADE_SIZE = 256;
	private static final int INITIAL_CAPACITY = 4;

	private final int maxBladeSize;

	private Blade<T> head;
	private Blade<T> tail;
	private int size = 0;

	private static class Blade<T> {
		Object[] elements;
		int length = 0;
		Blade<T> next;

		Blade(final int capacity) {
			this.elements = new Object[capacity];
		}

		void ensureCapacity(final int required, final int maxCapacity) {
			if (required <= this.elements.length) {
				return;
			}

			final int newCapacity = Math.min(maxCapacity, Math.max(required, this.elements.length * 2));

			this.elements = Arrays.copyOf(this.elements, newCapacity);
		}

		void reserve(final int size, final int maxCapacity) {
			if (size < 0 || size > maxCapacity) {
				throw new IllegalArgumentException("Invalid blade size: " + size);
			}

			this.ensureCapacity(size, maxCapacity);
			this.length = size;
		}

		void trimToSize() {
			if (this.elements.length != this.length) {
				this.elements = Arrays.copyOf(this.elements, this.length);
			}
		}

	}

	/** Creates an empty list with the default maximum blade size. */
	public LinkedArrayList() {
		this(0, LinkedArrayList.DEFAULT_MAX_BLADE_SIZE);
	}

	/** Creates an empty list with the specified maximum number of elements per blade. */
	public LinkedArrayList(final int maxBladeSize) {
		this(0, maxBladeSize);
	}

	/** Creates a list with the specified number of null elements and maximum blade size. */
	public LinkedArrayList(final int initialSize, final int maxBladeSize) {
		if (maxBladeSize <= 0) {
			throw new IllegalArgumentException("Maximum blade size must be positive");
		}

		if (initialSize < 0) {
			throw new IllegalArgumentException("Initial size cannot be negative");
		}

		this.maxBladeSize = maxBladeSize;
		this.size = initialSize;

		int remaining = initialSize;

		while (remaining > 0) {
			final int bladeSize = Math.min(remaining, maxBladeSize);

			final Blade<T> blade = this.createBlade();
			blade.reserve(bladeSize, maxBladeSize);

			if (this.head == null) {
				this.head = blade;
			} else {
				this.tail.next = blade;
			}

			this.tail = blade;
			remaining -= bladeSize;
		}
	}

	/**
	 * Creates a list containing the specified number of null elements using the default maximum blade
	 * size.
	 */
	public static <T> LinkedArrayList<T> withInitialSize(final int initialSize) {
		return new LinkedArrayList<>(initialSize, LinkedArrayList.DEFAULT_MAX_BLADE_SIZE);
	}

	private Blade<T> createBlade() {
		return new Blade<>(Math.min(LinkedArrayList.INITIAL_CAPACITY, this.maxBladeSize));
	}

	private static class Location<T> {
		Blade<T> blade;
		int offset;

		Location(final Blade<T> blade, final int offset) {
			this.blade = blade;
			this.offset = offset;
		}
	}

	private Location<T> locate(final int index) {
		if (index < 0 || index >= this.size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
		}

		Blade<T> current = this.head;
		int remaining = index;

		while (current != null) {
			if (remaining < current.length) {
				return new Location<>(current, remaining);
			}

			remaining -= current.length;
			current = current.next;
		}

		throw new IllegalStateException("Invalid list structure");
	}

	/** Returns the element at the specified index. */
	@SuppressWarnings("unchecked")
	@Override
	public T get(final int index) {
		final Location<T> location = this.locate(index);

		return (T) location.blade.elements[location.offset];
	}

	/** Replaces the element at the specified index and returns its previous value. */
	@Override
	public T set(final int index, final T element) {
		final Location<T> location = this.locate(index);

		@SuppressWarnings("unchecked") final T previous = (T) location.blade.elements[location.offset];

		location.blade.elements[location.offset] = element;

		return previous;
	}

	/** Appends an element to the end of the list. */
	@Override
	public boolean add(final T element) {
		if (this.tail == null || this.tail.length == this.maxBladeSize) {
			final Blade<T> blade = this.createBlade();

			if (this.tail == null) {
				this.head = blade;
			} else {
				this.tail.next = blade;
			}

			this.tail = blade;
		}

		this.tail.ensureCapacity(this.tail.length + 1, this.maxBladeSize);
		this.tail.elements[this.tail.length++] = element;

		this.size++;
		this.modCount++;

		return true;
	}

	/** Inserts an element at the specified index, shifting subsequent elements to the right. */
	@Override
	public void add(final int index, final T element) {
		if (index < 0 || index >= this.size + 1) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + (this.size + 1));
		}

		if (index == this.size) {
			this.add(element);
			return;
		}

		final Location<T> location = this.locate(index);
		final Blade<T> blade = location.blade;
		final int offset = location.offset;

		if (blade.length == this.maxBladeSize) {
			final Blade<T> newBlade = this.createBlade();

			// last element -> new blade
			newBlade.elements[0] = blade.elements[blade.length - 1];
			newBlade.length = 1;

			newBlade.next = blade.next;
			blade.next = newBlade;

			if (this.tail == blade) {
				this.tail = newBlade;
			}

			blade.elements[--blade.length] = null;
		}

		blade.ensureCapacity(blade.length + 1, this.maxBladeSize);

		System.arraycopy(blade.elements, offset, blade.elements, offset + 1, blade.length - offset);

		blade.elements[offset] = element;
		blade.length++;

		this.size++;
		this.modCount++;
	}

	/** Removes and returns the element at the specified index without shrinking blade arrays. */
	@Override
	public T remove(final int index) {
		if (index < 0 || index >= this.size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
		}

		Blade<T> current = this.head;
		Blade<T> previous = null;
		int remaining = index;

		while (remaining >= current.length) {
			remaining -= current.length;
			previous = current;
			current = current.next;
		}

		@SuppressWarnings("unchecked") final T removed = (T) current.elements[remaining];

		// Shift elements, but don't resize the array
		System.arraycopy(current.elements, remaining + 1, current.elements, remaining, current.length - remaining - 1);

		current.elements[--current.length] = null;

		// Unlink empty blades
		if (current.length == 0) {
			if (previous == null) {
				this.head = current.next;
			} else {
				previous.next = current.next;
			}

			if (this.tail == current) {
				this.tail = previous;
			}
		}

		this.size--;
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
		this.head = null;
		this.tail = null;
		this.size = 0;
		this.modCount++;
	}

	/** Returns the maximum number of elements that a single blade can hold. */
	public int getMaxBladeSize() {
		return this.maxBladeSize;
	}

	/** Shrinks each blade's array to its logical length without moving elements between blades. */
	public void trimToSize() {
		Blade<T> current = this.head;

		while (current != null) {
			current.trimToSize();
			current = current.next;
		}
	}

	/**
	 * Moves elements into earlier blades to minimize the number of blades while preserving element
	 * order.
	 */
	public void pack() {
		Blade<T> current = this.head;
		boolean modified = false;

		while (current != null && current.next != null) {

			// Already full, continue with the next blade.
			if (current.length == this.maxBladeSize) {
				current = current.next;
				continue;
			}

			final Blade<T> next = current.next;

			// Number of elements that fit into the current blade.
			final int available = this.maxBladeSize - current.length;
			final int count = Math.min(available, next.length);

			// Grow the destination array if necessary.
			current.ensureCapacity(current.length + count, this.maxBladeSize);

			// Move elements from the next blade.
			System.arraycopy(next.elements, 0, current.elements, current.length, count);

			current.length += count;

			// Shift the remaining elements in the next blade.
			System.arraycopy(next.elements, count, next.elements, 0, next.length - count);

			// Clear unused references without shrinking the array.
			Arrays.fill(next.elements, next.length - count, next.length, null);

			next.length -= count;
			modified |= count > 0;

			// Remove the next blade if it is empty.
			if (next.length == 0) {
				current.next = next.next;

				if (this.tail == next) {
					this.tail = current;
				}

				modified = true;
			} else {
				// Current blade must now be full.
				current = next;
			}
		}

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

	/** Creates a shallow copy with independent blades and arrays but shared element references. */
	@Override
	public LinkedArrayList<T> clone() {
		final LinkedArrayList<T> clone = new LinkedArrayList<>(this.maxBladeSize);

		clone.size = this.size;

		Blade<T> current = this.head;

		while (current != null) {
			final Blade<T> copied = new Blade<>(current.elements.length);

			copied.elements = Arrays.copyOf(current.elements, current.elements.length);
			copied.length = current.length;

			if (clone.head == null) {
				clone.head = copied;
			} else {
				clone.tail.next = copied;
			}

			clone.tail = copied;
			current = current.next;
		}

		return clone;
	}

	/** Creates a copy with independent blades, arrays, and cloneable elements. */
	@Override
	public LinkedArrayList<T> deepClone() {
		final LinkedArrayList<T> clone = this.clone();

		Blade<T> current = clone.head;

		while (current != null) {
			for (int i = 0; i < current.length; i++) {
				final Object element = current.elements[i];

				if (element instanceof DeepCloneable) {
					current.elements[i] = ((DeepCloneable) element).deepClone();
				}
			}

			current = current.next;
		}

		return clone;
	}

}
