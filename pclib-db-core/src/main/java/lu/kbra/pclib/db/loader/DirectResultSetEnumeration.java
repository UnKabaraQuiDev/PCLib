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

	public DirectResultSetEnumeration(final ResultSet rs, final SQLThrowingFunction<B> mapper) throws SQLException {
		this.rs = rs;
		this.mapper = mapper;
		this.hasNext = rs.next();
	}

	public void forEachRemaining(final Consumer<? super B> action) {
		while (this.hasMoreElements()) {
			action.accept(this.nextElement());
		}
	}

	@Override
	public boolean hasMoreElements() {
		return this.hasNext;
	}

	@Override
	public B nextElement() {
		if (!this.hasNext) {
			throw new NoSuchElementException();
		}
		try {
			final B value = this.mapper.apply(this.rs);
			this.hasNext = this.rs.next();
			if (!this.hasNext) {
				this.rs.close();
			}
			return value;
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
