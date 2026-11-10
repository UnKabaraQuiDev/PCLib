package lu.kbra.pclib.geo.result;

import java.util.Objects;
import java.util.function.Supplier;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyTripletImpl;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

/** A tagged alternative; inactive tuple slots are null. */
public final class Result3<A, B, C> extends ReadOnlyTripletImpl<A, B, C> implements Cloneable {
	private final int selectedIndex;

	private Result3(final A a, final B b, final C c, final int index) {
		super(a, b, c);
		this.selectedIndex = index;
	}

	public static <A, B, C> Result3<A, B, C> first(final A a) {
		return new Result3<>(Objects.requireNonNull(a), null, null, 0);
	}

	public static <A, B, C> Result3<A, B, C> second(final B b) {
		return new Result3<>(null, Objects.requireNonNull(b), null, 1);
	}

	public static <A, B, C> Result3<A, B, C> third(final C c) {
		return new Result3<>(null, null, Objects.requireNonNull(c), 2);
	}

	public int selectedIndex() {
		return this.selectedIndex;
	}

	public <T, E extends Throwable> T
			get(final ThrowingFunction<A, T, E> first, final ThrowingFunction<B, T, E> second, final ThrowingFunction<C, T, E> third)
					throws E {
		if (this.selectedValue() instanceof Empty) {
			throw new NullPointerException("Empty value.");
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getFirst());
		case 1 -> second.apply(this.getSecond());
		case 2 -> third.apply(this.getThird());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, E extends Throwable> T getOrThrow(
			final ThrowingFunction<A, T, E> first,
			final ThrowingFunction<B, T, E> second,
			final ThrowingFunction<C, T, E> third,
			final Supplier<E> exceptionSupplier)
			throws E {
		if (this.selectedValue() instanceof Empty) {
			throw exceptionSupplier.get();
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getFirst());
		case 1 -> second.apply(this.getSecond());
		case 2 -> third.apply(this.getThird());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, E extends Throwable> T getOrElse(
			final ThrowingFunction<A, T, E> first,
			final ThrowingFunction<B, T, E> second,
			final ThrowingFunction<C, T, E> third,
			final ThrowingSupplier<T, E> supplier)
			throws E {
		if (this.selectedValue() instanceof Empty) {
			return supplier.get();
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getFirst());
		case 1 -> second.apply(this.getSecond());
		case 2 -> third.apply(this.getThird());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, E extends Throwable> T getOrElse(
			final ThrowingFunction<A, T, E> first,
			final ThrowingFunction<B, T, E> second,
			final ThrowingFunction<C, T, E> third,
			final T default_)
			throws E {
		if (this.selectedValue() instanceof Empty) {
			return default_;
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getFirst());
		case 1 -> second.apply(this.getSecond());
		case 2 -> third.apply(this.getThird());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public Object selectedValue() {
		return super.get(this.selectedIndex);
	}

	public boolean isFirst() {
		return this.selectedIndex == 0;
	}

	public boolean isSecond() {
		return this.selectedIndex == 1;
	}

	public boolean isThird() {
		return this.selectedIndex == 2;
	}

	@Override
	public Result3<A, B, C> clone() {
		return this;
	}

	@Override
	public Result3<A, B, C> cloneReadOnly() {
		return this;
	}

	@Override
	public Result3<A, B, C> deepClone() {
		return this.deepCloneReadOnly();
	}

	@Override
	public Result3<A, B, C> deepCloneReadOnly() {
		return switch (this.selectedIndex) {
		case 0 -> Result3.first(DeepCloneable.deepClone(this.getFirst()));
		case 1 -> Result3.second(DeepCloneable.deepClone(this.getSecond()));
		default -> Result3.third(DeepCloneable.deepClone(this.getThird()));
		};
	}
}
