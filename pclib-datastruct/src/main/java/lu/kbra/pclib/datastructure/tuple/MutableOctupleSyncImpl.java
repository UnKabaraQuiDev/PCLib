package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.OctFunction;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public class MutableOctupleSyncImpl<A, B, C, D, E, F, G, H> extends MutableOctupleImpl<A, B, C, D, E, F, G, H> {

	public MutableOctupleSyncImpl() {
	}

	public MutableOctupleSyncImpl(
			final A first,
			final B second,
			final C third,
			final D fourth,
			final E fifth,
			final F sixth,
			final G seventh,
			final H eighth) {
		super(first, second, third, fourth, fifth, sixth, seventh, eighth);
	}

	@Override
	public synchronized Object[] asArray() {
		return super.asArray();
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> clone() {
		return super.clone();
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> cloneMutable() {
		return super.cloneMutable();
	}

	@Override
	public synchronized ReadOnlyOctuple<A, B, C, D, E, F, G, H> cloneReadOnly() {
		return super.cloneReadOnly();
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> deepClone() {
		return super.deepClone();
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> deepCloneMutable() {
		return super.deepCloneMutable();
	}

	@Override
	public synchronized ReadOnlyOctuple<A, B, C, D, E, F, G, H> deepCloneReadOnly() {
		return super.deepCloneReadOnly();
	}

	@Override
	public synchronized int elementCount() {
		return super.elementCount();
	}

	@Override
	public synchronized boolean equals(final Object obj) {
		return super.equals(obj);
	}

	@Override
	public synchronized <T> T get(final int i) {
		return super.get(i);
	}

	@Override
	public synchronized int hashCode() {
		return super.hashCode();
	}

	@Override
	public synchronized boolean hasFirst() {
		return super.hasFirst();
	}

	@Override
	public synchronized boolean hasSecond() {
		return super.hasSecond();
	}

	@Override
	public synchronized boolean hasThird() {
		return super.hasThird();
	}

	@Override
	public synchronized boolean hasFourth() {
		return super.hasFourth();
	}

	@Override
	public synchronized boolean hasFifth() {
		return super.hasFifth();
	}

	@Override
	public synchronized boolean hasSixth() {
		return super.hasSixth();
	}

	@Override
	public synchronized boolean hasSeventh() {
		return super.hasSeventh();
	}

	@Override
	public synchronized boolean hasEighth() {
		return super.hasEighth();
	}

	@Override
	public synchronized <T> T map(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return super.map(func);
	}

	@Override
	public synchronized <T, N, O, P, Q, R, S, U> MutableOctuple<T, N, O, P, Q, R, S, U> map(
			final OctFunction<A, B, C, D, E, F, G, H, T> funcFirst,
			final OctFunction<A, B, C, D, E, F, G, H, N> funcSecond,
			final OctFunction<A, B, C, D, E, F, G, H, O> funcThird,
			final OctFunction<A, B, C, D, E, F, G, H, P> funcFourth,
			final OctFunction<A, B, C, D, E, F, G, H, Q> funcFifth,
			final OctFunction<A, B, C, D, E, F, G, H, R> funcSixth,
			final OctFunction<A, B, C, D, E, F, G, H, S> funcSeventh,
			final OctFunction<A, B, C, D, E, F, G, H, U> funcEighth) {
		return super.map(funcFirst, funcSecond, funcThird, funcFourth, funcFifth, funcSixth, funcSeventh, funcEighth);
	}

	@Override
	public synchronized <T> MutableOctuple<T, B, C, D, E, F, G, H> mapFirst(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return super.mapFirst(func);
	}

	@Override
	public synchronized <T> MutableOctuple<A, T, C, D, E, F, G, H> mapSecond(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return super.mapSecond(func);
	}

	@Override
	public synchronized <T> MutableOctuple<A, B, T, D, E, F, G, H> mapThird(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return super.mapThird(func);
	}

	@Override
	public synchronized <T> MutableOctuple<A, B, C, T, E, F, G, H> mapFourth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return super.mapFourth(func);
	}

	@Override
	public synchronized <T> MutableOctuple<A, B, C, D, T, F, G, H> mapFifth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return super.mapFifth(func);
	}

	@Override
	public synchronized <T> MutableOctuple<A, B, C, D, E, T, G, H> mapSixth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return super.mapSixth(func);
	}

	@Override
	public synchronized <T> MutableOctuple<A, B, C, D, E, F, T, H> mapSeventh(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return super.mapSeventh(func);
	}

	@Override
	public synchronized <T> MutableOctuple<A, B, C, D, E, F, G, T> mapEighth(final OctFunction<A, B, C, D, E, F, G, H, T> func) {
		return super.mapEighth(func);
	}

	@Override
	public synchronized <T> MutableOctuple<?, ?, ?, ?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		return super.map(index, func);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> mut(
			final OctFunction<A, B, C, D, E, F, G, H, A> funcFirst,
			final OctFunction<A, B, C, D, E, F, G, H, B> funcSecond,
			final OctFunction<A, B, C, D, E, F, G, H, C> funcThird,
			final OctFunction<A, B, C, D, E, F, G, H, D> funcFourth,
			final OctFunction<A, B, C, D, E, F, G, H, E> funcFifth,
			final OctFunction<A, B, C, D, E, F, G, H, F> funcSixth,
			final OctFunction<A, B, C, D, E, F, G, H, G> funcSeventh,
			final OctFunction<A, B, C, D, E, F, G, H, H> funcEighth) {
		return super.mut(funcFirst, funcSecond, funcThird, funcFourth, funcFifth, funcSixth, funcSeventh, funcEighth);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> mutFirst(final OctFunction<A, B, C, D, E, F, G, H, A> func) {
		return super.mutFirst(func);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> mutSecond(final OctFunction<A, B, C, D, E, F, G, H, B> func) {
		return super.mutSecond(func);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> mutThird(final OctFunction<A, B, C, D, E, F, G, H, C> func) {
		return super.mutThird(func);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> mutFourth(final OctFunction<A, B, C, D, E, F, G, H, D> func) {
		return super.mutFourth(func);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> mutFifth(final OctFunction<A, B, C, D, E, F, G, H, E> func) {
		return super.mutFifth(func);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> mutSixth(final OctFunction<A, B, C, D, E, F, G, H, F> func) {
		return super.mutSixth(func);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> mutSeventh(final OctFunction<A, B, C, D, E, F, G, H, G> func) {
		return super.mutSeventh(func);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> mutEighth(final OctFunction<A, B, C, D, E, F, G, H, H> func) {
		return super.mutEighth(func);
	}

	@Override
	public synchronized <T> MutableOctuple<A, B, C, D, E, F, G, H> mut(final int index, final Function<Object, T> func) {
		return super.mut(index, func);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> setFirst(final A first) {
		return super.setFirst(first);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> setSecond(final B second) {
		return super.setSecond(second);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> setThird(final C third) {
		return super.setThird(third);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> setFourth(final D fourth) {
		return super.setFourth(fourth);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> setFifth(final E fifth) {
		return super.setFifth(fifth);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> setSixth(final F sixth) {
		return super.setSixth(sixth);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> setSeventh(final G seventh) {
		return super.setSeventh(seventh);
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> setEighth(final H eighth) {
		return super.setEighth(eighth);
	}

	@Override
	public synchronized <T> MutableOctuple<A, B, C, D, E, F, G, H> set(final int i, final T value) {
		return super.set(i, value);
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}

	@Override
	public synchronized A getFirst() {
		return super.getFirst();
	}

	@Override
	public synchronized B getSecond() {
		return super.getSecond();
	}

	@Override
	public synchronized C getThird() {
		return super.getThird();
	}

	@Override
	public synchronized D getFourth() {
		return super.getFourth();
	}

	@Override
	public synchronized E getFifth() {
		return super.getFifth();
	}

	@Override
	public synchronized F getSixth() {
		return super.getSixth();
	}

	@Override
	public synchronized G getSeventh() {
		return super.getSeventh();
	}

	@Override
	public synchronized H getEighth() {
		return super.getEighth();
	}

	@Override
	public synchronized <T, P extends Throwable> T[] asArray(final ThrowingFunction<Integer, T[], P> factory) throws P {
		return super.asArray(factory);
	}

	@Override
	public synchronized <T> T[] asArray(final Function<Integer, T[]> factory) {
		return super.asArray(factory);
	}

	@Override
	public synchronized <T> T[] asArray(final IntFunction<T[]> factory) {
		return super.asArray(factory);
	}

	@Override
	public synchronized <T, P extends Throwable> T[] asArray(final ThrowingSupplier<T[], P> factory) throws P {
		return super.asArray(factory);
	}

	@Override
	public synchronized <T> T[] asArray(final Supplier<T[]> factory) {
		return super.asArray(factory);
	}

	@Override
	public synchronized <T> T[] asArray(final T[] destination) {
		return super.asArray(destination);
	}

	@Override
	public synchronized boolean isMutable() {
		return true;
	}

	@Override
	public synchronized boolean isView() {
		return false;
	}

	@Override
	public synchronized Octuple<A, B, C, D, E, F, G, H> asViewReadOnly() {
		return super.asViewReadOnly();
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> asViewMutable() {
		return super.asViewMutable();
	}

	@Override
	public synchronized MutableOctuple<A, B, C, D, E, F, G, H> asSynchronized() {
		return this;
	}

	@Override
	public synchronized boolean isSynchronized() {
		return true;
	}
}
