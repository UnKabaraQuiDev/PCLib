package lu.kbra.pclib.geo.result;

import java.util.Objects;
import java.util.function.Supplier;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.datastructure.tuple.ReadOnlySeptupleImpl;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

/** Exactly one of 7 alternatives is selected; the others are null. */
public final class Result7<A, B, C, D, E, F, G> extends ReadOnlySeptupleImpl<A, B, C, D, E, F, G> implements Cloneable {
	private final int selectedIndex;

	private Result7(final A a, final B b, final C c, final D d, final E e, final F f, final G g, final int selectedIndex) {
		super(a, b, c, d, e, f, g);
		this.selectedIndex = selectedIndex;
	}

	public static <A, B, C, D, E, F, G> Result7<A, B, C, D, E, F, G> first(final A value) {
		return new Result7<>(Objects.requireNonNull(value), null, null, null, null, null, null, 0);
	}

	public static <A, B, C, D, E, F, G> Result7<A, B, C, D, E, F, G> second(final B value) {
		return new Result7<>(null, Objects.requireNonNull(value), null, null, null, null, null, 1);
	}

	public static <A, B, C, D, E, F, G> Result7<A, B, C, D, E, F, G> third(final C value) {
		return new Result7<>(null, null, Objects.requireNonNull(value), null, null, null, null, 2);
	}

	public static <A, B, C, D, E, F, G> Result7<A, B, C, D, E, F, G> fourth(final D value) {
		return new Result7<>(null, null, null, Objects.requireNonNull(value), null, null, null, 3);
	}

	public static <A, B, C, D, E, F, G> Result7<A, B, C, D, E, F, G> fifth(final E value) {
		return new Result7<>(null, null, null, null, Objects.requireNonNull(value), null, null, 4);
	}

	public static <A, B, C, D, E, F, G> Result7<A, B, C, D, E, F, G> sixth(final F value) {
		return new Result7<>(null, null, null, null, null, Objects.requireNonNull(value), null, 5);
	}

	public static <A, B, C, D, E, F, G> Result7<A, B, C, D, E, F, G> seventh(final G value) {
		return new Result7<>(null, null, null, null, null, null, Objects.requireNonNull(value), 6);
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
			final ThrowingFunction<F, T, Z> sixth,
			final ThrowingFunction<G, T, Z> seventh)
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
		case 6 -> seventh.apply(this.getSeventh());
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
			final ThrowingFunction<G, T, Z> seventh,
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
		case 6 -> seventh.apply(this.getSeventh());
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
			final ThrowingFunction<G, T, Z> seventh,
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
		case 6 -> seventh.apply(this.getSeventh());
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
			final ThrowingFunction<G, T, Z> seventh,
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
		case 6 -> seventh.apply(this.getSeventh());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public Object selectedValue() {
		return super.get(this.selectedIndex);
	}

	@Override
	public Result7<A, B, C, D, E, F, G> clone() {
		return this;
	}

	@Override
	public Result7<A, B, C, D, E, F, G> cloneReadOnly() {
		return this;
	}

	@Override
	public Result7<A, B, C, D, E, F, G> deepClone() {
		return this.deepCloneReadOnly();
	}

	@Override
	public Result7<A, B, C, D, E, F, G> deepCloneReadOnly() {
		return switch (this.selectedIndex) {
		case 0 -> Result7.first(DeepCloneable.deepClone(this.getFirst()));
		case 1 -> Result7.second(DeepCloneable.deepClone(this.getSecond()));
		case 2 -> Result7.third(DeepCloneable.deepClone(this.getThird()));
		case 3 -> Result7.fourth(DeepCloneable.deepClone(this.getFourth()));
		case 4 -> Result7.fifth(DeepCloneable.deepClone(this.getFifth()));
		case 5 -> Result7.sixth(DeepCloneable.deepClone(this.getSixth()));
		case 6 -> Result7.seventh(DeepCloneable.deepClone(this.getSeventh()));
		default -> throw new AssertionError(this.selectedIndex);
		};
	}
}
