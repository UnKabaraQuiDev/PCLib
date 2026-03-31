package lu.kbra.pclib.datastructure.list;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * You're probably looking for {@link java.util.LinkedList}, this was just a test
 */
@Deprecated
public class LinkedList<T> implements Iterable<T> {

	@Deprecated
	protected LinkedListNode<T> root;

	@Deprecated
	public LinkedList(final int length, final Function<Integer, T> valueSupplier) {
		if (length > 0) {
			this.append(length, valueSupplier);
		}
	}

	@Deprecated
	public LinkedList(final int length) {
		if (length > 0) {
			this.append(length, (T) null);
		}
	}

	@Deprecated
	public T get(final int index) {
		return this.getNode(index).data;
	}

	@Deprecated
	protected LinkedListNode<T> getNode(final int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}

		LinkedListNode<T> currentNode = this.root;
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

	@Deprecated
	public T set(final int index, final T newValue) {
		final LinkedListNode<T> node = this.getNode(index);
		final T oldData = node.data;
		node.data = newValue;
		return oldData;
	}

	@Deprecated
	public void add(final T value) {
		if (this.root == null) {
			this.root = new LinkedListNode<>(value);
		} else {
			final LinkedListNode<T> latest = this.getLastNode();
			latest.next = new LinkedListNode<>(value);
		}
	}

	@Deprecated
	public void insert(final int index, final T value) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}

		if (index == 0) {
			this.root = new LinkedListNode<>(value, this.root);
		} else {
			final LinkedListNode<T> parent = this.getNode(index - 1);
			final LinkedListNode<T> child1 = parent.next; // node to be removed

			parent.next = new LinkedListNode<>(value, child1);
		}
	}

	@Deprecated
	public T remove(final int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}

		if (index == 0) {
			final T oldRootData = this.root.data;

			this.root = this.root.next;

			return oldRootData;
		} else {
			final LinkedListNode<T> parent = this.getNode(index - 1);

			if (parent.next == null) {
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}

			final LinkedListNode<T> child1 = parent.next; // node to be removed
			final LinkedListNode<T> child2 = child1.next; // child to fill in the gap

			parent.next = child2;

			return child1.data;
		}
	}

	@Deprecated
	public int removeIf(final Predicate<T> condition) {
		int removedCount = 0;
		LinkedListNode<T> previous = null, current = this.root;

		while (current != null) {

			if (condition.test(current.data)) {
				removedCount++;

				if (previous == null) {
					this.root = current.next;
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

	@Deprecated
	public int length() {
		if (this.root == null) {
			return 0;
		}

		int count = 0;

		LinkedListNode<T> latest = this.root;

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

	@Deprecated
	protected LinkedListNode<T> getLastNode() {
		if (this.root == null) {
			return null;
		}

		LinkedListNode<T> latest = this.root;

		while (latest != null) {
			if (latest.next != null) {
				latest = latest.next;
			} else {
				return latest;
			}
		}

		return latest;
	}

	@Deprecated
	public T getLast() {
		if (this.root == null) {
			return null;
		}
		return this.getLastNode().data;
	}

	@Deprecated
	public void append(int count, final T value) {
		if (this.root == null) {
			this.root = new LinkedListNode<>(value);
			count--;
		}

		LinkedListNode<T> latest = this.root;

		for (int i = 0; i < count; i++) {
			latest = latest.next = new LinkedListNode<>(value);
		}
	}

	@Deprecated
	public void append(int count, final Function<Integer, T> valueSupplier) {
		int gen = 0;

		if (this.root == null) {
			this.root = new LinkedListNode<>(valueSupplier.apply(gen++));
			count--;
		}

		LinkedListNode<T> latest = this.root;

		for (int i = 0; i < count; i++) {
			latest = latest.next = new LinkedListNode<>(valueSupplier.apply(gen++));
		}
	}

	@Deprecated
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			LinkedListNode<T> current = LinkedList.this.root;

			@Override
			public boolean hasNext() {
				return this.current != null;
			}

			@Override
			public T next() {
				final T currentData = this.current.data;
				this.current = this.current.next;
				return currentData;
			}
		};
	}

	@Deprecated
	@SuppressWarnings("hiding")
	public class LinkedListNode<T> {

		private T data;
		private LinkedListNode<T> next;

		public LinkedListNode(final T data) {
			this.data = data;
		}

		public LinkedListNode(final T data, final LinkedListNode<T> next) {
			this.data = data;
			this.next = next;
		}

		@Override
		public String toString() {
			return Objects.toString(this.data);
		}

	}

}
