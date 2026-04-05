package lu.kbra.pclib.datastructure.list;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

public interface WeakList<T> extends Iterable<T> {

	@Override
	void forEach(final Consumer<? super T> action);

	@Override
	Iterator<T> iterator();

	boolean isEmpty();

	void clear();

	int size();

	boolean remove(final T value);

	void add(final T value);

	Optional<T> optGet(final int index);

	T get(final int index);

	void sort(Comparator<? super T> comparator);

}
