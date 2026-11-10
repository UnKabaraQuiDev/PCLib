package lu.kbra.pclib.geo.result;

import java.util.Objects;
import java.util.function.Supplier;

import lu.kbra.pclib.datastructure.DeepCloneable;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyPairImpl;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

/** A tagged alternative; the inactive tuple slot is null. */
public final class Result2<A, B> extends ReadOnlyPairImpl<A, B> implements Cloneable {
	private final int selectedIndex;

	private Result2(final A a, final B b, final int index) {
		super(a, b);
		this.selectedIndex = index;
	}

	public static <A, B> Result2<A, B> first(final A a) {
		return new Result2<>(Objects.requireNonNull(a), null, 0);
	}

	public static <A, B> Result2<A, B> second(final B b) {
		return new Result2<>(null, Objects.requireNonNull(b), 1);
	}

	public <T, E extends Throwable> T get(final ThrowingFunction<A, T, E> first, final ThrowingFunction<B, T, E> second) throws E {
		if (this.selectedValue() instanceof Empty) {
			throw new NullPointerException("Empty value.");
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getKey());
		case 1 -> second.apply(this.getValue());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, E extends Throwable> T
			getOrThrow(final ThrowingFunction<A, T, E> first, final ThrowingFunction<B, T, E> second, final Supplier<E> exceptionSupplier)
					throws E {
		if (this.selectedValue() instanceof Empty) {
			throw exceptionSupplier.get();
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getKey());
		case 1 -> second.apply(this.getValue());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, E extends Throwable> T
			getOrElse(final ThrowingFunction<A, T, E> first, final ThrowingFunction<B, T, E> second, final ThrowingSupplier<T, E> supplier)
					throws E {
		if (this.selectedValue() instanceof Empty) {
			return supplier.get();
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getKey());
		case 1 -> second.apply(this.getValue());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public <T, E extends Throwable> T
			getOrElse(final ThrowingFunction<A, T, E> first, final ThrowingFunction<B, T, E> second, final T default_) throws E {
		if (this.selectedValue() instanceof Empty) {
			return default_;
		}
		return switch (this.selectedIndex) {
		case 0 -> first.apply(this.getKey());
		case 1 -> second.apply(this.getValue());
		default -> throw new IndexOutOfBoundsException(this.selectedIndex);
		};
	}

	public Object selectedValue() {
		return super.get(this.selectedIndex);
	}

	public int selectedIndex() {
		return this.selectedIndex;
	}

	public boolean isFirst() {
		return this.selectedIndex == 0;
	}

	public boolean isSecond() {
		return this.selectedIndex == 1;
	}

	@Override
	public Result2<A, B> clone() {
		return new Result2<>(this.getKey(), this.getValue(), this.selectedIndex);
	}

	@Override
	public Result2<A, B> cloneReadOnly() {
		return this.clone();
	}

	@Override
	public Result2<A, B> deepClone() {
		return this.deepCloneReadOnly();
	}

	@Override
	public Result2<A, B> deepCloneReadOnly() {
		return this.isFirst() ? Result2.first(DeepCloneable.deepClone(this.getKey()))
				: Result2.second(DeepCloneable.deepClone(this.getValue()));
	}
}
