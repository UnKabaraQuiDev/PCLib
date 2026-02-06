package lu.kbra.pclib.db.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConditionBuilder {

	public class InConditionNode extends ConditionNode {

		public InConditionNode(String column, int size) {
			super(column, " IN ", size);
		}

		@Override
		public String toSQL() {
			return column + op + " (" + IntStream.range(0, (int) value).mapToObj(i -> "?").collect(Collectors.joining(", ")) + ")";
		}

		@Override
		public String toString() {
			return "InConditionNode [column=" + column + ", op=" + op + ", value=" + value + "]";
		}

	}

	private Node node;
	private final List<Object> params = new ArrayList<>();
	private final List<String> columns = new ArrayList<>();

	ConditionBuilder() {
	}

	public ConditionBuilder match(String column, String op, Object value) {
		ConditionNode c = new ConditionNode(column, op, value);
		attach("AND", c);
		params.add(value);
		columns.add(column);
		return this;
	}

	public <T> ConditionBuilder in(String column, Collection<T> value) {
		ConditionNode c = new InConditionNode(column, value.size());
		attach("AND", c);
		value.forEach(params::add);
		columns.add(column);
		return this;
	}

	public ConditionBuilder match(String line) {
		InlineConditionNode c = new InlineConditionNode(line);
		attach("AND", c);
		return this;
	}

	public ConditionBuilder and(Function<ConditionBuilder, ConditionBuilder> sub) {
		ConditionBuilder nested = sub.apply(new ConditionBuilder());
		attach("AND", nested.build());
		params.addAll(nested.getParams());
		columns.addAll(nested.getColumns());
		return this;
	}

	public ConditionBuilder or(Function<ConditionBuilder, ConditionBuilder> sub) {
		ConditionBuilder nested = sub.apply(new ConditionBuilder());
		attach("OR", nested.build());
		params.addAll(nested.getParams());
		columns.addAll(nested.getColumns());
		return this;
	}

	private void attach(String op, Node newNode) {
		if (node == null) {
			node = newNode;
		} else {
			node = new BinaryOpNode(op, node, newNode);
		}
	}

	Node build() {
		return node;
	}

	List<Object> getParams() {
		return params;
	}

	List<String> getColumns() {
		return columns;
	}

	protected interface Node {
		String toSQL();
	}

	protected static class ConditionNode implements Node {
		String column, op;
		Object value;

		ConditionNode(String column, String op, Object value) {
			this.column = column;
			this.op = op;
			this.value = value;
		}

		@Override
		public String toSQL() {
			return column + " " + op + " ?";
		}

		@Override
		public String toString() {
			return "ConditionNode [column=" + column + ", op=" + op + ", value=" + value + "]";
		}

	}

	protected static class InlineConditionNode implements Node {
		String line;

		public InlineConditionNode(String line) {
			this.line = line;
		}

		@Override
		public String toSQL() {
			return line;
		}

		@Override
		public String toString() {
			return "InlineConditionNode [line=" + line + "]";
		}

	}

	protected static class BinaryOpNode implements Node {
		String op;
		Node left, right;

		BinaryOpNode(String op, Node single) {
			this(op, null, single);
		}

		BinaryOpNode(String op, Node left, Node right) {
			this.op = op;
			this.left = left;
			this.right = right;
		}

		@Override
		public String toSQL() {
			if (left == null)
				return "(" + right.toSQL() + ")";
			return "(" + left.toSQL() + " " + op + " " + right.toSQL() + ")";
		}

		@Override
		public String toString() {
			return "BinaryOpNode [op=" + op + ", left=" + left + ", right=" + right + "]";
		}

	}

}