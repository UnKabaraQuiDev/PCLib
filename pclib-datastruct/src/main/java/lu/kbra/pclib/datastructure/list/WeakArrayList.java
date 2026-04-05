package lu.kbra.pclib.datastructure.list;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class WeakArrayList<T> implements WeakList<T> {

	private final List<WeakReference<T>> backing = new ArrayList<>();

	@Override
	public T get(final int index) {
		this.cleanup();

		int aliveIndex = 0;

		for (final WeakReference<T> ref : this.backing) {
			final T value = ref.get();
			if (value != null) {
				if (aliveIndex == index) {
					return value;
				}
				aliveIndex++;
			}
		}

		throw new IndexOutOfBoundsException("Index: " + index);
	}

	@Override
	public Optional<T> optGet(final int index) {
		this.cleanup();

		int aliveIndex = 0;

		for (final WeakReference<T> ref : this.backing) {
			final T value = ref.get();
			if (value != null) {
				if (aliveIndex == index) {
					return Optional.of(value);
				}
				aliveIndex++;
			}
		}

		return Optional.empty();
	}

	@Override
	public void add(final T value) {
		this.cleanup();
		if (value == null) {
			return;
		}
		this.backing.add(new WeakReference<>(value));
	}

	@Override
	public boolean remove(final T value) {
		boolean removed = false;

		final Iterator<WeakReference<T>> it = this.backing.iterator();
		while (it.hasNext()) {
			final T ref = it.next().get();
			if (ref == null || ref.equals(value)) {
				it.remove();
				if (value.equals(ref)) {
					removed = true;
				}
			}
		}

		return removed;
	}

	@Override
	public int size() {
		this.cleanup();
		return this.backing.size();
	}

	@Override
	public void clear() {
		this.backing.clear();
	}

	@Override
	public boolean isEmpty() {
		this.cleanup();
		return this.backing.isEmpty();
	}

	protected void cleanup() {
		this.backing.removeIf(ref -> ref.get() == null);
	}

	@Override
	public Iterator<T> iterator() {
		this.cleanup();

		final Iterator<WeakReference<T>> it = this.backing.iterator();

		return new Iterator<T>() {

			T next = this.advance();

			private T advance() {
				while (it.hasNext()) {
					final T value = it.next().get();
					if (value != null) {
						return value;
					}
				}
				return null;
			}

			@Override
			public boolean hasNext() {
				return this.next != null;
			}

			@Override
			public T next() {
				if (this.next == null) {
					throw new NoSuchElementException();
				}
				final T current = this.next;
				this.next = this.advance();
				return current;
			}
		};
	}

	@Override
	public void forEach(final Consumer<? super T> action) {
		Objects.requireNonNull(action);
		this.cleanup();
		for (final T value : this) {
			action.accept(value);
		}
	}

	@Override
	public void sort(final Comparator<? super T> comparator) {
		Objects.requireNonNull(comparator);

		this.cleanup();

		final List<T> values = new ArrayList<>(this.backing.size());
		for (final WeakReference<T> ref : this.backing) {
			final T v = ref.get();
			if (v != null) {
				values.add(v);
			}
		}

		values.sort(comparator);

		this.backing.clear();
		for (final T v : values) {
			this.backing.add(new WeakReference<>(v));
		}
	}

	@Override
	public String toString() {
		return "WeakArrayList@" + System.identityHashCode(this) + " [backing=" + this.backing + "]";
	}

}
