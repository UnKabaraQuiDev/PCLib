package lu.kbra.pclib.datastructure.list;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

public interface WeakList<T> extends Iterable<T> {

	void add(final T value);

	void clear();

	@Override
	void forEach(final Consumer<? super T> action);

	T get(final int index);

	boolean isEmpty();

	@Override
	Iterator<T> iterator();

	Optional<T> optGet(final int index);

	boolean remove(final T value);

	int size();

	void sort(Comparator<? super T> comparator);

}
