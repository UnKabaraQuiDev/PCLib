package lu.kbra.pclib.geo.result;

import java.util.Objects;
import java.util.function.Supplier;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyOctupleImpl;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

/** Exactly one of 8 alternatives is selected; the others are null. */
public final class Result8<A, B, C, D, E, F, G, H> extends ReadOnlyOctupleImpl<A, B, C, D, E, F, G, H> implements Cloneable {
	private final int selectedIndex;

	private Result8(final A a, final B b, final C c, final D d, final E e, final F f, final G g, final H h, final int selectedIndex) {
		super(a, b, c, d, e, f, g, h);
		this.selectedIndex = selectedIndex;
	}

	public static <A, B, C, D, E, F, G, H> Result8<A, B, C, D, E, F, G, H> first(final A value) {
		return new Result8<>(Objects.requireNonNull(value), null, null, null, null, null, null, null, 0);
	}

	public static <A, B, C, D, E, F, G, H> Result8<A, B, C, D, E, F, G, H> second(final B value) {
		return new Result8<>(null, Objects.requireNonNull(value), null, null, null, null, null, null, 1);
	}

	public static <A, B, C, D, E, F, G, H> Result8<A, B, C, D, E, F, G, H> third(final C value) {
		return new Result8<>(null, null, Objects.requireNonNull(value), null, null, null, null, null, 2);
	}

	public static <A, B, C, D, E, F, G, H> Result8<A, B, C, D, E, F, G, H> fourth(final D value) {
		return new Result8<>(null, null, null, Objects.requireNonNull(value), null, null, null, null, 3);
	}

	public static <A, B, C, D, E, F, G, H> Result8<A, B, C, D, E, F, G, H> fifth(final E value) {
		return new Result8<>(null, null, null, null, Objects.requireNonNull(value), null, null, null, 4);
	}

	public static <A, B, C, D, E, F, G, H> Result8<A, B, C, D, E, F, G, H> sixth(final F value) {
		return new Result8<>(null, null, null, null, null, Objects.requireNonNull(value), null, null, 5);
	}

	public static <A, B, C, D, E, F, G, H> Result8<A, B, C, D, E, F, G, H> seventh(final G value) {
		return new Result8<>(null, null, null, null, null, null, Objects.requireNonNull(value), null, 6);
	}

	public static <A, B, C, D, E, F, G, H> Result8<A, B, C, D, E, F, G, H> eighth(final H value) {
		return new Result8<>(null, null, null, null, null, null, null, Objects.requireNonNull(value), 7);
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
			final ThrowingFunction<G, T, Z> seventh,
			final ThrowingFunction<H, T, Z> eighth)
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
		case 7 -> eighth.apply(this.getEighth());
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
			final ThrowingFunction<H, T, Z> eighth,
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
		case 7 -> eighth.apply(this.getEighth());
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
			final ThrowingFunction<H, T, Z> eighth,
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
		case 7 -> eighth.apply(this.getEighth());
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
			final ThrowingFunction<H, T, Z> eighth,
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
		case 7 -> eighth.apply(this.getEighth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public Object selectedValue() {
		return super.get(this.selectedIndex);
	}

	@Override
	public Result8<A, B, C, D, E, F, G, H> clone() {
		return this;
	}

	@Override
	public Result8<A, B, C, D, E, F, G, H> cloneReadOnly() {
		return this;
	}

	@Override
	public Result8<A, B, C, D, E, F, G, H> deepClone() {
		return this.deepCloneReadOnly();
	}

	@Override
	public Result8<A, B, C, D, E, F, G, H> deepCloneReadOnly() {
		return switch (this.selectedIndex) {
		case 0 -> Result8.first(DeepCloneable.deepClone(this.getFirst()));
		case 1 -> Result8.second(DeepCloneable.deepClone(this.getSecond()));
		case 2 -> Result8.third(DeepCloneable.deepClone(this.getThird()));
		case 3 -> Result8.fourth(DeepCloneable.deepClone(this.getFourth()));
		case 4 -> Result8.fifth(DeepCloneable.deepClone(this.getFifth()));
		case 5 -> Result8.sixth(DeepCloneable.deepClone(this.getSixth()));
		case 6 -> Result8.seventh(DeepCloneable.deepClone(this.getSeventh()));
		case 7 -> Result8.eighth(DeepCloneable.deepClone(this.getEighth()));
		default -> throw new AssertionError(this.selectedIndex);
		};
	}
}
