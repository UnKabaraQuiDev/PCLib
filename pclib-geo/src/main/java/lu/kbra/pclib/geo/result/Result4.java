package lu.kbra.pclib.geo.result;

import java.util.Objects;
import java.util.function.Supplier;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyQuadrupleImpl;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

/** Exactly one of 4 alternatives is selected; the others are null. */
public final class Result4<A, B, C, D> extends ReadOnlyQuadrupleImpl<A, B, C, D> implements Cloneable {
	private final int selectedIndex;

	private Result4(final A a, final B b, final C c, final D d, final int selectedIndex) {
		super(a, b, c, d);
		this.selectedIndex = selectedIndex;
	}

	public static <A, B, C, D> Result4<A, B, C, D> first(final A value) {
		return new Result4<>(Objects.requireNonNull(value), null, null, null, 0);
	}

	public static <A, B, C, D> Result4<A, B, C, D> second(final B value) {
		return new Result4<>(null, Objects.requireNonNull(value), null, null, 1);
	}

	public static <A, B, C, D> Result4<A, B, C, D> third(final C value) {
		return new Result4<>(null, null, Objects.requireNonNull(value), null, 2);
	}

	public static <A, B, C, D> Result4<A, B, C, D> fourth(final D value) {
		return new Result4<>(null, null, null, Objects.requireNonNull(value), 3);
	}

	public int selectedIndex() {
		return this.selectedIndex;
	}

	public <T, E extends Throwable> T get(
			final ThrowingFunction<A, T, E> first,
			final ThrowingFunction<B, T, E> second,
			final ThrowingFunction<C, T, E> third,
			final ThrowingFunction<D, T, E> fourth)
			throws E {
		if (this.selectedValue() instanceof Empty) {
			throw new NullPointerException("Empty value.");
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getFirst());
		case 1 -> second.apply(this.getSecond());
		case 2 -> third.apply(this.getThird());
		case 3 -> fourth.apply(this.getFourth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, E extends Throwable> T getOrThrow(
			final ThrowingFunction<A, T, E> first,
			final ThrowingFunction<B, T, E> second,
			final ThrowingFunction<C, T, E> third,
			final ThrowingFunction<D, T, E> fourth,
			final Supplier<E> exceptionSupplier)
			throws E {
		if (this.selectedValue() instanceof Empty) {
			throw exceptionSupplier.get();
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getFirst());
		case 1 -> second.apply(this.getSecond());
		case 2 -> third.apply(this.getThird());
		case 3 -> fourth.apply(this.getFourth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, E extends Throwable> T getOrElse(
			final ThrowingFunction<A, T, E> first,
			final ThrowingFunction<B, T, E> second,
			final ThrowingFunction<C, T, E> third,
			final ThrowingFunction<D, T, E> fourth,
			final ThrowingSupplier<T, E> supplier)
			throws E {
		if (this.selectedValue() instanceof Empty) {
			return supplier.get();
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getFirst());
		case 1 -> second.apply(this.getSecond());
		case 2 -> third.apply(this.getThird());
		case 3 -> fourth.apply(this.getFourth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, E extends Throwable> T getOrElse(
			final ThrowingFunction<A, T, E> first,
			final ThrowingFunction<B, T, E> second,
			final ThrowingFunction<C, T, E> third,
			final ThrowingFunction<D, T, E> fourth,
			final T default_)
			throws E {
		if (this.selectedValue() instanceof Empty) {
			return default_;
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getFirst());
		case 1 -> second.apply(this.getSecond());
		case 2 -> third.apply(this.getThird());
		case 3 -> fourth.apply(this.getFourth());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public Object selectedValue() {
		return super.get(this.selectedIndex);
	}

	@Override
	public Result4<A, B, C, D> clone() {
		return this;
	}

	@Override
	public Result4<A, B, C, D> cloneReadOnly() {
		return this;
	}

	@Override
	public Result4<A, B, C, D> deepClone() {
		return this.deepCloneReadOnly();
	}

	@Override
	public Result4<A, B, C, D> deepCloneReadOnly() {
		return switch (this.selectedIndex) {
		case 0 -> Result4.first(DeepCloneable.deepClone(this.getFirst()));
		case 1 -> Result4.second(DeepCloneable.deepClone(this.getSecond()));
		case 2 -> Result4.third(DeepCloneable.deepClone(this.getThird()));
		case 3 -> Result4.fourth(DeepCloneable.deepClone(this.getFourth()));
		default -> throw new AssertionError(this.selectedIndex);
		};
	}
}
