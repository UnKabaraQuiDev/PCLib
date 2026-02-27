package lu.kbra.pclib.datastructure.list;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * You're probably looking for {@link java.util.LinkedList}, this was just a
 * test
 */
@Deprecated
public class LinkedList<T> implements Iterable<T> {

	protected LinkedListNode<T> root;

	public LinkedList(int length, Function<Integer, T> valueSupplier) {
		if (length > 0) {
			append(length, valueSupplier);
		}
	}

	public LinkedList(int length) {
		if (length > 0) {
			append(length, (T) null);
		}
	}

	public T get(final int index) {
		return getNode(index).data;
	}

	protected LinkedListNode<T> getNode(final int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}

		LinkedListNode<T> currentNode = root;
		int currentIndex = 0;

		while (currentIndex < index && currentNode != null) {
			currentNode = currentNode.next;
			currentIndex++;
		}

		if (currentNode == null) {
			throw new IndexOutOfBoundsException(Integer.toString(index) + " >= " + Integer.toString(currentIndex));
		}

		return currentNode;
	}

	public T set(final int index, final T newValue) {
		final LinkedListNode<T> node = getNode(index);
		final T oldData = node.data;
		node.data = newValue;
		return oldData;
	}

	public void add(final T value) {
		if (root == null) {
			root = new LinkedListNode<>(value);
		} else {
			final LinkedListNode<T> latest = getLastNode();
			latest.next = new LinkedListNode<>(value);
		}
	}

	public void insert(final int index, final T value) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}

		if (index == 0) {
			root = new LinkedListNode<>(value, root);
		} else {
			final LinkedListNode<T> parent = getNode(index - 1);
			final LinkedListNode<T> child1 = parent.next; // node to be removed

			parent.next = new LinkedListNode<>(value, child1);
		}
	}

	public T remove(final int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}

		if (index == 0) {
			final T oldRootData = root.data;

			root = root.next;

			return oldRootData;
		} else {
			final LinkedListNode<T> parent = getNode(index - 1);

			if (parent.next == null) {
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}

			final LinkedListNode<T> child1 = parent.next; // node to be removed
			final LinkedListNode<T> child2 = child1.next; // child to fill in the gap

			parent.next = child2;

			return child1.data;
		}
	}

	public int removeIf(Predicate<T> condition) {
		int removedCount = 0;
		LinkedListNode<T> previous = null, current = root;

		while (current != null) {

			if (condition.test(current.data)) {
				removedCount++;

				if (previous == null) {
					root = current.next;
				} else {
					previous.next = current.next;
				}
			} else {
				previous = current;
			}

			current = current.next;
		}

		return removedCount;
	}

	public int length() {
		if (root == null) {
			return 0;
		}

		int count = 0;

		LinkedListNode<T> latest = root;

		while (latest != null) {
			if (latest.next != null) {
				latest = latest.next;
				count++;
			} else {
				return count;
			}
		}

		return count;
	}

	protected LinkedListNode<T> getLastNode() {
		if (root == null) {
			return null;
		}

		LinkedListNode<T> latest = root;

		while (latest != null) {
			if (latest.next != null) {
				latest = latest.next;
			} else {
				return latest;
			}
		}

		return latest;
	}

	public T getLast() {
		if (root == null)
			return null;
		return getLastNode().data;
	}

	public void append(int count, T value) {
		if (root == null) {
			root = new LinkedListNode<>(value);
			count--;
		}

		LinkedListNode<T> latest = root;

		for (int i = 0; i < count; i++) {
			latest = latest.next = new LinkedListNode<>(value);
		}
	}

	public void append(int count, Function<Integer, T> valueSupplier) {
		int gen = 0;

		if (root == null) {
			root = new LinkedListNode<>(valueSupplier.apply(gen++));
			count--;
		}

		LinkedListNode<T> latest = root;

		for (int i = 0; i < count; i++) {
			latest = (latest.next = new LinkedListNode<>(valueSupplier.apply(gen++)));
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			LinkedListNode<T> current = root;

			@Override
			public boolean hasNext() {
				return current != null;
			}

			@Override
			public T next() {
				final T currentData = current.data;
				current = current.next;
				return currentData;
			}
		};
	}

	@SuppressWarnings("hiding")
	public class LinkedListNode<T> {

		private T data;
		private LinkedListNode<T> next;

		public LinkedListNode(T data) {
			this.data = data;
		}

		public LinkedListNode(T data, LinkedListNode<T> next) {
			this.data = data;
			this.next = next;
		}

		@Override
		public String toString() {
			return Objects.toString(data);
		}

	}

}
