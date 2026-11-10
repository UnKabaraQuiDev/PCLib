package lu.kbra.pclib.geo.result;

import java.util.Objects;
import java.util.function.Supplier;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyQuintupleImpl;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

/** Exactly one of 5 alternatives is selected; the others are null. */
public final class Result5<A, B, C, D, E> extends ReadOnlyQuintupleImpl<A, B, C, D, E> implements Cloneable {
	private final int selectedIndex;

	private Result5(final A a, final B b, final C c, final D d, final E e, final int selectedIndex) {
		super(a, b, c, d, e);
		this.selectedIndex = selectedIndex;
	}

	public static <A, B, C, D, E> Result5<A, B, C, D, E> first(final A value) {
		return new Result5<>(Objects.requireNonNull(value), null, null, null, null, 0);
	}

	public static <A, B, C, D, E> Result5<A, B, C, D, E> second(final B value) {
		return new Result5<>(null, Objects.requireNonNull(value), null, null, null, 1);
	}

	public static <A, B, C, D, E> Result5<A, B, C, D, E> third(final C value) {
		return new Result5<>(null, null, Objects.requireNonNull(value), null, null, 2);
	}

	public static <A, B, C, D, E> Result5<A, B, C, D, E> fourth(final D value) {
		return new Result5<>(null, null, null, Objects.requireNonNull(value), null, 3);
	}

	public static <A, B, C, D, E> Result5<A, B, C, D, E> fifth(final E value) {
		return new Result5<>(null, null, null, null, Objects.requireNonNull(value), 4);
	}

	public int selectedIndex() {
		return this.selectedIndex;
	}

	public <T, Z extends Throwable> T get(
			final ThrowingFunction<A, T, Z> first,
			final ThrowingFunction<B, T, Z> second,
			final ThrowingFunction<C, T, Z> third,
			final ThrowingFunction<D, T, Z> fourth,
			final ThrowingFunction<E, T, Z> fifth)
			throws Z {
		if (this.selectedValue() instanceof Empty) {
			throw new NullPointerException("Empty value.");
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getFirst());
		case 1 -> second.apply(this.getSecond());
		case 2 -> third.apply(this.getThird());
		case 3 -> fourth.apply(this.getFourth());
		case 4 -> fifth.apply(this.getFifth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, Z extends Throwable> T getOrThrow(
			final ThrowingFunction<A, T, Z> first,
			final ThrowingFunction<B, T, Z> second,
			final ThrowingFunction<C, T, Z> third,
			final ThrowingFunction<D, T, Z> fourth,
			final ThrowingFunction<E, T, Z> fifth,
			final Supplier<Z> exceptionSupplier)
			throws Z {
		if (this.selectedValue() instanceof Empty) {
			throw exceptionSupplier.get();
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getFirst());
		case 1 -> second.apply(this.getSecond());
		case 2 -> third.apply(this.getThird());
		case 3 -> fourth.apply(this.getFourth());
		case 4 -> fifth.apply(this.getFifth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, Z extends Throwable> T getOrElse(
			final ThrowingFunction<A, T, Z> first,
			final ThrowingFunction<B, T, Z> second,
			final ThrowingFunction<C, T, Z> third,
			final ThrowingFunction<D, T, Z> fourth,
			final ThrowingFunction<E, T, Z> fifth,
			final ThrowingSupplier<T, Z> supplier)
			throws Z {
		if (this.selectedValue() instanceof Empty) {
			return supplier.get();
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getFirst());
		case 1 -> second.apply(this.getSecond());
		case 2 -> third.apply(this.getThird());
		case 3 -> fourth.apply(this.getFourth());
		case 4 -> fifth.apply(this.getFifth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, Z extends Throwable> T getOrElse(
			final ThrowingFunction<A, T, Z> first,
			final ThrowingFunction<B, T, Z> second,
			final ThrowingFunction<C, T, Z> third,
			final ThrowingFunction<D, T, Z> fourth,
			final ThrowingFunction<E, T, Z> fifth,
			final T default_)
			throws Z {
		if (this.selectedValue() instanceof Empty) {
			return default_;
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getFirst());
		case 1 -> second.apply(this.getSecond());
		case 2 -> third.apply(this.getThird());
		case 3 -> fourth.apply(this.getFourth());
		case 4 -> fifth.apply(this.getFifth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public Object selectedValue() {
		return super.get(this.selectedIndex);
	}

	@Override
	public Result5<A, B, C, D, E> clone() {
		return this;
	}

	@Override
	public Result5<A, B, C, D, E> cloneReadOnly() {
		return this;
	}

	@Override
	public Result5<A, B, C, D, E> deepClone() {
		return this.deepCloneReadOnly();
	}

	@Override
	public Result5<A, B, C, D, E> deepCloneReadOnly() {
		return switch (this.selectedIndex) {
		case 0 -> Result5.first(DeepCloneable.deepClone(this.getFirst()));
		case 1 -> Result5.second(DeepCloneable.deepClone(this.getSecond()));
		case 2 -> Result5.third(DeepCloneable.deepClone(this.getThird()));
		case 3 -> Result5.fourth(DeepCloneable.deepClone(this.getFourth()));
		case 4 -> Result5.fifth(DeepCloneable.deepClone(this.getFifth()));
		default -> throw new AssertionError(this.selectedIndex);
		};
	}
}
