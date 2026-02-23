package lu.kbra.pclib.db.loader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import lu.kbra.pclib.db.impl.SQLThrowingFunction;

public class BufferedResultSetEnumeration<B> implements Enumeration<B> {

	private final List<B> list;
	private final Iterator<B> listIterator;

	public BufferedResultSetEnumeration(ResultSet rs, SQLThrowingFunction<B> mapper) throws SQLException {
		list = new ArrayList<>();
		while (rs.next()) {
			list.add(mapper.apply(rs));
		}
		listIterator = list.iterator();
	}

	public Stream<B> stream() {
		return list.stream();
	}

	public Stream<B> parallelStream() {
		return list.parallelStream();
	}

	public List<B> asList() {
		return Collections.unmodifiableList(list);
	}

	public void forEach(Consumer<? super B> action) {
		list.forEach(action);
	}

	public void forEachRemaining(Consumer<? super B> action) {
		listIterator.forEachRemaining(action);
	}

	@Override
	public boolean hasMoreElements() {
		return listIterator.hasNext();
	}

	@Override
	public B nextElement() {
		return listIterator.next();
	}

}