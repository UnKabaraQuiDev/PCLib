package lu.kbra.pclib.db.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConditionBuilder {

	public class InConditionNode extends ConditionNode {

		public InConditionNode(final String column, final int size) {
			super(column, " IN ", size);
		}

		@Override
		public String toSQL() {
			return this.column + this.op + " (" + IntStream.range(0, (int) this.value).mapToObj(i -> "?").collect(Collectors.joining(", "))
					+ ")";
		}

		@Override
		public String toString() {
			return "InConditionNode [column=" + this.column + ", op=" + this.op + ", value=" + this.value + "]";
		}

	}

	private Node node;
	private final List<Object> params = new ArrayList<>();
	private final List<String> columns = new ArrayList<>();

	ConditionBuilder() {
	}

	public ConditionBuilder match(final String column, final String op, final Object value) {
		final ConditionNode c = new ConditionNode(column, op, value);
		this.attach("AND", c);
		this.params.add(value);
		this.columns.add(column);
		return this;
	}

	public <T> ConditionBuilder in(final String column, final Collection<T> value) {
		final ConditionNode c = new InConditionNode(column, value.size());
		this.attach("AND", c);
		value.forEach(this.params::add);
		this.columns.add(column);
		return this;
	}

	public ConditionBuilder match(final String line) {
		final InlineConditionNode c = new InlineConditionNode(line);
		this.attach("AND", c);
		return this;
	}

	public ConditionBuilder and(final Function<ConditionBuilder, ConditionBuilder> sub) {
		final ConditionBuilder nested = sub.apply(new ConditionBuilder());
		this.attach("AND", nested.build());
		this.params.addAll(nested.getParams());
		this.columns.addAll(nested.getColumns());
		return this;
	}

	public ConditionBuilder or(final Function<ConditionBuilder, ConditionBuilder> sub) {
		final ConditionBuilder nested = sub.apply(new ConditionBuilder());
		this.attach("OR", nested.build());
		this.params.addAll(nested.getParams());
		this.columns.addAll(nested.getColumns());
		return this;
	}

	private void attach(final String op, final Node newNode) {
		if (this.node == null) {
			this.node = newNode;
		} else {
			this.node = new BinaryOpNode(op, this.node, newNode);
		}
	}

	Node build() {
		return this.node;
	}

	List<Object> getParams() {
		return this.params;
	}

	List<String> getColumns() {
		return this.columns;
	}

	protected interface Node {
		String toSQL();
	}

	protected static class ConditionNode implements Node {
		String column, op;
		Object value;

		ConditionNode(final String column, final String op, final Object value) {
			this.column = column;
			this.op = op;
			this.value = value;
		}

		@Override
		public String toSQL() {
			return this.column + " " + this.op + " ?";
		}

		@Override
		public String toString() {
			return "ConditionNode [column=" + this.column + ", op=" + this.op + ", value=" + this.value + "]";
		}

	}

	protected static class InlineConditionNode implements Node {
		String line;

		public InlineConditionNode(final String line) {
			this.line = line;
		}

		@Override
		public String toSQL() {
			return this.line;
		}

		@Override
		public String toString() {
			return "InlineConditionNode [line=" + this.line + "]";
		}

	}

	protected static class BinaryOpNode implements Node {
		String op;
		Node left, right;

		BinaryOpNode(final String op, final Node single) {
			this(op, null, single);
		}

		BinaryOpNode(final String op, final Node left, final Node right) {
			this.op = op;
			this.left = left;
			this.right = right;
		}

		@Override
		public String toSQL() {
			if (this.left == null) {
				return "(" + this.right.toSQL() + ")";
			}
			return "(" + this.left.toSQL() + " " + this.op + " " + this.right.toSQL() + ")";
		}

		@Override
		public String toString() {
			return "BinaryOpNode [op=" + this.op + ", left=" + this.left + ", right=" + this.right + "]";
		}

	}

}
