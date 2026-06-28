package lu.kbra.pclib.db.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class ConditionBuilder {

	@Getter
	@ToString
	@EqualsAndHashCode(callSuper = true)
	public class InConditionNode extends ConditionNode {

		public InConditionNode(final String column, final int size) {
			super(column, " IN ", size);
		}

		@Override
		public <B extends SQLQueryable<T>, T extends DataBaseEntry> String build(final SQLStructureVisitor visitor, final B instance) {
			return visitor.qualifiedName(this.column) + this.op + " ("
					+ IntStream.range(0, (int) this.value).mapToObj(i -> "?").collect(Collectors.joining(", ")) + ")";
		}

	}

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode
	protected static class BinaryOpNode implements Node {

		final String op;
		final Node left, right;

		BinaryOpNode(final String op, final Node single) {
			this(op, null, single);
		}

		@Override
		public <B extends SQLQueryable<T>, T extends DataBaseEntry> String build(final SQLStructureVisitor visitor, final B instance) {
			if (this.left == null) {
				return "(" + this.right.build(visitor, instance) + ")";
			}
			return "(" + this.left.build(visitor, instance) + " " + this.op + " " + this.right.build(visitor, instance) + ")";
		}

	}

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode
	protected static class ConditionNode implements Node {
		final String column, op;
		final Object value;

		@Override
		public <B extends SQLQueryable<T>, T extends DataBaseEntry> String build(final SQLStructureVisitor visitor, final B instance) {
			return visitor.qualifiedName(this.column) + " " + this.op + " ?";
		}

	}

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode
	protected static class InlineConditionNode implements Node {

		final String line;

		@Override
		public <B extends SQLQueryable<T>, T extends DataBaseEntry> String build(final SQLStructureVisitor visitor, final B instance) {
			return this.line;
		}

	}

	protected interface Node {

		<B extends SQLQueryable<T>, T extends DataBaseEntry> String build(SQLStructureVisitor visitor, B instance);

		@Override
		int hashCode();

		@Override
		String toString();

	}

	private Node root;
	private final List<Object> params = new ArrayList<>();
	private final List<String> columns = new ArrayList<>();

	ConditionBuilder() {
	}

	public ConditionBuilder and(final Function<ConditionBuilder, ConditionBuilder> sub) {
		final ConditionBuilder nested = sub.apply(new ConditionBuilder());
		this.attach("AND", nested.root);
		this.params.addAll(nested.getParams());
		this.columns.addAll(nested.getColumns());
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

	public ConditionBuilder match(final String column, final String op, final Object value) {
		final ConditionNode c = new ConditionNode(column, op, value);
		this.attach("AND", c);
		this.params.add(value);
		this.columns.add(column);
		return this;
	}

	public ConditionBuilder or(final Function<ConditionBuilder, ConditionBuilder> sub) {
		final ConditionBuilder nested = sub.apply(new ConditionBuilder());
		this.attach("OR", nested.root);
		this.params.addAll(nested.getParams());
		this.columns.addAll(nested.getColumns());
		return this;
	}

	private void attach(final String op, final Node newNode) {
		if (this.root == null) {
			this.root = newNode;
		} else {
			this.root = new BinaryOpNode(op, this.root, newNode);
		}
	}

}
