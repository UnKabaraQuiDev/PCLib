package lu.kbra.pclib.geo.result;

import java.util.Objects;
import java.util.function.Supplier;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.datastructure.tuple.ReadOnlySextupleImpl;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

/** Exactly one of 6 alternatives is selected; the others are null. */
public final class Result6<A, B, C, D, E, F> extends ReadOnlySextupleImpl<A, B, C, D, E, F> implements Cloneable {
	private final int selectedIndex;

	private Result6(final A a, final B b, final C c, final D d, final E e, final F f, final int selectedIndex) {
		super(a, b, c, d, e, f);
		this.selectedIndex = selectedIndex;
	}

	public static <A, B, C, D, E, F> Result6<A, B, C, D, E, F> first(final A value) {
		return new Result6<>(Objects.requireNonNull(value), null, null, null, null, null, 0);
	}

	public static <A, B, C, D, E, F> Result6<A, B, C, D, E, F> second(final B value) {
		return new Result6<>(null, Objects.requireNonNull(value), null, null, null, null, 1);
	}

	public static <A, B, C, D, E, F> Result6<A, B, C, D, E, F> third(final C value) {
		return new Result6<>(null, null, Objects.requireNonNull(value), null, null, null, 2);
	}

	public static <A, B, C, D, E, F> Result6<A, B, C, D, E, F> fourth(final D value) {
		return new Result6<>(null, null, null, Objects.requireNonNull(value), null, null, 3);
	}

	public static <A, B, C, D, E, F> Result6<A, B, C, D, E, F> fifth(final E value) {
		return new Result6<>(null, null, null, null, Objects.requireNonNull(value), null, 4);
	}

	public static <A, B, C, D, E, F> Result6<A, B, C, D, E, F> sixth(final F value) {
		return new Result6<>(null, null, null, null, null, Objects.requireNonNull(value), 5);
	}

	public int selectedIndex() {
		return this.selectedIndex;
	}

	public <T, Z extends Throwable> T get(
			final ThrowingFunction<A, T, Z> first,
			final ThrowingFunction<B, T, Z> second,
			final ThrowingFunction<C, T, Z> third,
			final ThrowingFunction<D, T, Z> fourth,
			final ThrowingFunction<E, T, Z> fifth,
			final ThrowingFunction<F, T, Z> sixth)
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
		case 5 -> sixth.apply(this.getSixth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, Z extends Throwable> T getOrThrow(
			final ThrowingFunction<A, T, Z> first,
			final ThrowingFunction<B, T, Z> second,
			final ThrowingFunction<C, T, Z> third,
			final ThrowingFunction<D, T, Z> fourth,
			final ThrowingFunction<E, T, Z> fifth,
			final ThrowingFunction<F, T, Z> sixth,
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
		case 5 -> sixth.apply(this.getSixth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, Z extends Throwable> T getOrElse(
			final ThrowingFunction<A, T, Z> first,
			final ThrowingFunction<B, T, Z> second,
			final ThrowingFunction<C, T, Z> third,
			final ThrowingFunction<D, T, Z> fourth,
			final ThrowingFunction<E, T, Z> fifth,
			final ThrowingFunction<F, T, Z> sixth,
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
		case 5 -> sixth.apply(this.getSixth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, Z extends Throwable> T getOrElse(
			final ThrowingFunction<A, T, Z> first,
			final ThrowingFunction<B, T, Z> second,
			final ThrowingFunction<C, T, Z> third,
			final ThrowingFunction<D, T, Z> fourth,
			final ThrowingFunction<E, T, Z> fifth,
			final ThrowingFunction<F, T, Z> sixth,
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
		case 5 -> sixth.apply(this.getSixth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public Object selectedValue() {
		return super.get(this.selectedIndex);
	}

	@Override
	public Result6<A, B, C, D, E, F> clone() {
		return this;
	}

	@Override
	public Result6<A, B, C, D, E, F> cloneReadOnly() {
		return this;
	}

	@Override
	public Result6<A, B, C, D, E, F> deepClone() {
		return this.deepCloneReadOnly();
	}

	@Override
	public Result6<A, B, C, D, E, F> deepCloneReadOnly() {
		return switch (this.selectedIndex) {
		case 0 -> Result6.first(DeepCloneable.deepClone(this.getFirst()));
		case 1 -> Result6.second(DeepCloneable.deepClone(this.getSecond()));
		case 2 -> Result6.third(DeepCloneable.deepClone(this.getThird()));
		case 3 -> Result6.fourth(DeepCloneable.deepClone(this.getFourth()));
		case 4 -> Result6.fifth(DeepCloneable.deepClone(this.getFifth()));
		case 5 -> Result6.sixth(DeepCloneable.deepClone(this.getSixth()));
		default -> throw new AssertionError(this.selectedIndex);
		};
	}
}
