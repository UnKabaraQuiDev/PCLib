package lu.kbra.pclib.db.loader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import lu.kbra.pclib.db.impl.SQLThrowingFunction;

public class DirectResultSetEnumeration<B> implements Enumeration<B> {

	private final ResultSet rs;
	private final SQLThrowingFunction<B> mapper;
	private boolean hasNext;

	public DirectResultSetEnumeration(ResultSet rs, SQLThrowingFunction<B> mapper) throws SQLException {
		this.rs = rs;
		this.mapper = mapper;
		this.hasNext = rs.next();
	}

	public void forEachRemaining(Consumer<? super B> action) {
		while (hasMoreElements()) {
			action.accept(nextElement());
		}
	}

	@Override
	public boolean hasMoreElements() {
		return hasNext;
	}

	@Override
	public B nextElement() {
		if (!hasNext) {
			throw new NoSuchElementException();
		}
		try {
			final B value = mapper.apply(rs);
			hasNext = rs.next();
			if (!hasNext) {
				rs.close();
			}
			return value;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}