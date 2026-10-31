package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.SeptFunction;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public class MutableSeptupleSyncImpl<A, B, C, D, E, F, G> extends MutableSeptupleImpl<A, B, C, D, E, F, G> {

	public MutableSeptupleSyncImpl() {
	}

	public MutableSeptupleSyncImpl(
			final A first,
			final B second,
			final C third,
			final D fourth,
			final E fifth,
			final F sixth,
			final G seventh) {
		super(first, second, third, fourth, fifth, sixth, seventh);
	}

	@Override
	public synchronized Object[] asArray() {
		return super.asArray();
	}

	@Override
	public synchronized Septuple<A, B, C, D, E, F, G> clone() {
		return super.clone();
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> cloneMutable() {
		return super.cloneMutable();
	}

	@Override
	public synchronized ReadOnlySeptuple<A, B, C, D, E, F, G> cloneReadOnly() {
		return super.cloneReadOnly();
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> deepClone() {
		return super.deepClone();
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> deepCloneMutable() {
		return super.deepCloneMutable();
	}

	@Override
	public synchronized ReadOnlySeptuple<A, B, C, D, E, F, G> deepCloneReadOnly() {
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
	public synchronized <T> T map(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return super.map(func);
	}

	@Override
	public synchronized <T, N, O, P, Q, R, S> MutableSeptuple<T, N, O, P, Q, R, S> map(
			final SeptFunction<A, B, C, D, E, F, G, T> funcFirst,
			final SeptFunction<A, B, C, D, E, F, G, N> funcSecond,
			final SeptFunction<A, B, C, D, E, F, G, O> funcThird,
			final SeptFunction<A, B, C, D, E, F, G, P> funcFourth,
			final SeptFunction<A, B, C, D, E, F, G, Q> funcFifth,
			final SeptFunction<A, B, C, D, E, F, G, R> funcSixth,
			final SeptFunction<A, B, C, D, E, F, G, S> funcSeventh) {
		return super.map(funcFirst, funcSecond, funcThird, funcFourth, funcFifth, funcSixth, funcSeventh);
	}

	@Override
	public synchronized <T> MutableSeptuple<T, B, C, D, E, F, G> mapFirst(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return super.mapFirst(func);
	}

	@Override
	public synchronized <T> MutableSeptuple<A, T, C, D, E, F, G> mapSecond(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return super.mapSecond(func);
	}

	@Override
	public synchronized <T> MutableSeptuple<A, B, T, D, E, F, G> mapThird(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return super.mapThird(func);
	}

	@Override
	public synchronized <T> MutableSeptuple<A, B, C, T, E, F, G> mapFourth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return super.mapFourth(func);
	}

	@Override
	public synchronized <T> MutableSeptuple<A, B, C, D, T, F, G> mapFifth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return super.mapFifth(func);
	}

	@Override
	public synchronized <T> MutableSeptuple<A, B, C, D, E, T, G> mapSixth(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return super.mapSixth(func);
	}

	@Override
	public synchronized <T> MutableSeptuple<A, B, C, D, E, F, T> mapSeventh(final SeptFunction<A, B, C, D, E, F, G, T> func) {
		return super.mapSeventh(func);
	}

	@Override
	public synchronized <T> MutableSeptuple<?, ?, ?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		return super.map(index, func);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> mut(
			final SeptFunction<A, B, C, D, E, F, G, A> funcFirst,
			final SeptFunction<A, B, C, D, E, F, G, B> funcSecond,
			final SeptFunction<A, B, C, D, E, F, G, C> funcThird,
			final SeptFunction<A, B, C, D, E, F, G, D> funcFourth,
			final SeptFunction<A, B, C, D, E, F, G, E> funcFifth,
			final SeptFunction<A, B, C, D, E, F, G, F> funcSixth,
			final SeptFunction<A, B, C, D, E, F, G, G> funcSeventh) {
		return super.mut(funcFirst, funcSecond, funcThird, funcFourth, funcFifth, funcSixth, funcSeventh);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> mutFirst(final SeptFunction<A, B, C, D, E, F, G, A> func) {
		return super.mutFirst(func);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> mutSecond(final SeptFunction<A, B, C, D, E, F, G, B> func) {
		return super.mutSecond(func);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> mutThird(final SeptFunction<A, B, C, D, E, F, G, C> func) {
		return super.mutThird(func);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> mutFourth(final SeptFunction<A, B, C, D, E, F, G, D> func) {
		return super.mutFourth(func);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> mutFifth(final SeptFunction<A, B, C, D, E, F, G, E> func) {
		return super.mutFifth(func);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> mutSixth(final SeptFunction<A, B, C, D, E, F, G, F> func) {
		return super.mutSixth(func);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> mutSeventh(final SeptFunction<A, B, C, D, E, F, G, G> func) {
		return super.mutSeventh(func);
	}

	@Override
	public synchronized <T> MutableSeptuple<A, B, C, D, E, F, G> mut(final int index, final Function<Object, T> func) {
		return super.mut(index, func);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> setFirst(final A first) {
		return super.setFirst(first);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> setSecond(final B second) {
		return super.setSecond(second);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> setThird(final C third) {
		return super.setThird(third);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> setFourth(final D fourth) {
		return super.setFourth(fourth);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> setFifth(final E fifth) {
		return super.setFifth(fifth);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> setSixth(final F sixth) {
		return super.setSixth(sixth);
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> setSeventh(final G seventh) {
		return super.setSeventh(seventh);
	}

	@Override
	public synchronized <T> MutableSeptuple<A, B, C, D, E, F, G> set(final int i, final T value) {
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
	public synchronized Septuple<A, B, C, D, E, F, G> asViewReadOnly() {
		return super.asViewReadOnly();
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> asViewMutable() {
		return super.asViewMutable();
	}

	@Override
	public synchronized MutableSeptuple<A, B, C, D, E, F, G> asSynchronized() {
		return this;
	}

	@Override
	public synchronized boolean isSynchronized() {
		return true;
	}
}
