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

	public BufferedResultSetEnumeration(final ResultSet rs, final SQLThrowingFunction<B> mapper) throws SQLException {
		this.list = new ArrayList<>();
		while (rs.next()) {
			this.list.add(mapper.apply(rs));
		}
		this.listIterator = this.list.iterator();
	}

	public Stream<B> stream() {
		return this.list.stream();
	}

	public Stream<B> parallelStream() {
		return this.list.parallelStream();
	}

	public List<B> asList() {
		return Collections.unmodifiableList(this.list);
	}

	public void forEach(final Consumer<? super B> action) {
		this.list.forEach(action);
	}

	public void forEachRemaining(final Consumer<? super B> action) {
		this.listIterator.forEachRemaining(action);
	}

	@Override
	public boolean hasMoreElements() {
		return this.listIterator.hasNext();
	}

	@Override
	public B nextElement() {
		return this.listIterator.next();
	}

}
