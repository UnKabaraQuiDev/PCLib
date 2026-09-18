package lu.kbra.pclib.datastructure.tuple;

import java.util.Objects;
import java.util.function.Function;

import lu.kbra.pclib.impl.function.SextFunction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditableSextuple<A, B, C, D, E, F> implements Sextuple<A, B, C, D, E, F>, EditableTuple {

	protected A first;
	protected B second;
	protected C third;
	protected D fourth;
	protected E fifth;
	protected F sixth;

	@Override
	public Object[] asArray() {
		return new Object[] { this.first, this.second, this.third, this.fourth, this.fifth, this.sixth };
	}

	@Override
	public Sextuple<A, B, C, D, E, F> clone() {
		return new EditableSextuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	public EditableSextuple<A, B, C, D, E, F> cloneEditable() {
		return new EditableSextuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	public ReadOnlySextuple<A, B, C, D, E, F> cloneReadOnly() {
		return new ReadOnlySextuple<>(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	public final int elementCount() {
		return 6;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}

		final Sextuple<?, ?, ?, ?, ?, ?> other = (Sextuple<?, ?, ?, ?, ?, ?>) obj;

		return Objects.equals(this.first, other.getFirst()) && Objects.equals(this.second, other.getSecond())
				&& Objects.equals(this.third, other.getThird()) && Objects.equals(this.fourth, other.getFourth())
				&& Objects.equals(this.fifth, other.getFifth()) && Objects.equals(this.sixth, other.getSixth());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(final int index) {
		switch (index) {
		case 0:
			return (T) this.first;
		case 1:
			return (T) this.second;
		case 2:
			return (T) this.third;
		case 3:
			return (T) this.fourth;
		case 4:
			return (T) this.fifth;
		case 5:
			return (T) this.sixth;
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 5]");
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	public boolean hasFirst() {
		return this.first != null;
	}

	@Override
	public boolean hasSecond() {
		return this.second != null;
	}

	@Override
	public boolean hasThird() {
		return this.third != null;
	}

	@Override
	public boolean hasFourth() {
		return this.fourth != null;
	}

	@Override
	public boolean hasFifth() {
		return this.fifth != null;
	}

	@Override
	public boolean hasSixth() {
		return this.sixth != null;
	}

	@Override
	public <T> T map(final SextFunction<A, B, C, D, E, F, T> func) {
		return func.apply(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	public <T, U, V, W, X, Y> EditableSextuple<T, U, V, W, X, Y> map(
			final SextFunction<A, B, C, D, E, F, T> funcFirst,
			final SextFunction<A, B, C, D, E, F, U> funcSecond,
			final SextFunction<A, B, C, D, E, F, V> funcThird,
			final SextFunction<A, B, C, D, E, F, W> funcFourth,
			final SextFunction<A, B, C, D, E, F, X> funcFifth,
			final SextFunction<A, B, C, D, E, F, Y> funcSixth) {

		return this.map((a, b, c, d, e, f) -> new EditableSextuple<>(funcFirst.apply(a, b, c, d, e, f),
				funcSecond.apply(a, b, c, d, e, f),
				funcThird.apply(a, b, c, d, e, f),
				funcFourth.apply(a, b, c, d, e, f),
				funcFifth.apply(a, b, c, d, e, f),
				funcSixth.apply(a, b, c, d, e, f)));
	}

	@Override
	public <T> EditableSextuple<T, B, C, D, E, F> mapFirst(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new EditableSextuple<>(func.apply(a, b, c, d, e, f), b, c, d, e, f));
	}

	@Override
	public <T> EditableSextuple<A, T, C, D, E, F> mapSecond(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new EditableSextuple<>(a, func.apply(a, b, c, d, e, f), c, d, e, f));
	}

	@Override
	public <T> EditableSextuple<A, B, T, D, E, F> mapThird(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new EditableSextuple<>(a, b, func.apply(a, b, c, d, e, f), d, e, f));
	}

	@Override
	public <T> EditableSextuple<A, B, C, T, E, F> mapFourth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new EditableSextuple<>(a, b, c, func.apply(a, b, c, d, e, f), e, f));
	}

	@Override
	public <T> EditableSextuple<A, B, C, D, T, F> mapFifth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new EditableSextuple<>(a, b, c, d, func.apply(a, b, c, d, e, f), f));
	}

	@Override
	public <T> EditableSextuple<A, B, C, D, E, T> mapSixth(final SextFunction<A, B, C, D, E, F, T> func) {
		return this.map((a, b, c, d, e, f) -> new EditableSextuple<>(a, b, c, d, e, func.apply(a, b, c, d, e, f)));
	}

	@Override
	public <T> EditableSextuple<?, ?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		final EditableSextuple<?, ?, ?, ?, ?, ?> ret = new EditableSextuple<>(this.first,
				this.second,
				this.third,
				this.fourth,
				this.fifth,
				this.sixth);
		ret.set(index, func.apply(ret.get(index)));
		return ret;
	}

	public Sextuple<A, B, C, D, E, F> setFirst(final A first) {
		this.first = first;
		return this;
	}

	public Sextuple<A, B, C, D, E, F> setSecond(final B second) {
		this.second = second;
		return this;
	}

	public Sextuple<A, B, C, D, E, F> setThird(final C third) {
		this.third = third;
		return this;
	}

	public Sextuple<A, B, C, D, E, F> setFourth(final D fourth) {
		this.fourth = fourth;
		return this;
	}

	public Sextuple<A, B, C, D, E, F> setFifth(final E fifth) {
		this.fifth = fifth;
		return this;
	}

	public Sextuple<A, B, C, D, E, F> setSixth(final F sixth) {
		this.sixth = sixth;
		return this;
	}

	@Override
	public String toString() {
		return String.format("{%s, %s, %s, %s, %s, %s}", this.first, this.second, this.third, this.fourth, this.fifth, this.sixth);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> EditableSextuple<A, B, C, D, E, F> set(final int index, final T element) {
		switch (index) {
		case 0:
			this.first = (A) element;
			break;
		case 1:
			this.second = (B) element;
			break;
		case 2:
			this.third = (C) element;
			break;
		case 3:
			this.fourth = (D) element;
			break;
		case 4:
			this.fifth = (E) element;
			break;
		case 5:
			this.sixth = (F) element;
			break;
		default:
			throw new IndexOutOfBoundsException("Index: " + index + " out of range: [0, 5]");
		}

		return this;
	}

}
