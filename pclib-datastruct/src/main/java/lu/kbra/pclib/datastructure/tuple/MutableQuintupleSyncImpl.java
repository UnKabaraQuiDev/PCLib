package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.QuintFunction;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public class MutableQuintupleSyncImpl<A, B, C, D, E> extends MutableQuintupleImpl<A, B, C, D, E> {

	public MutableQuintupleSyncImpl() {
	}

	public MutableQuintupleSyncImpl(final A first, final B second, final C third, final D fourth, final E fifth) {
		super(first, second, third, fourth, fifth);
	}

	@Override
	public synchronized Object[] asArray() {
		return super.asArray();
	}

	@Override
	public synchronized Quintuple<A, B, C, D, E> clone() {
		return super.clone();
	}

	@Override
	public synchronized MutableQuintuple<A, B, C, D, E> cloneMutable() {
		return super.cloneMutable();
	}

	@Override
	public synchronized ReadOnlyQuintuple<A, B, C, D, E> cloneReadOnly() {
		return super.cloneReadOnly();
	}

	@Override
	public synchronized MutableQuintuple<A, B, C, D, E> deepClone() {
		return super.deepClone();
	}

	@Override
	public synchronized MutableQuintuple<A, B, C, D, E> deepCloneMutable() {
		return super.deepCloneMutable();
	}

	@Override
	public synchronized ReadOnlyQuintuple<A, B, C, D, E> deepCloneReadOnly() {
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
	public synchronized <T> T get(final int index) {
		return super.get(index);
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
	public synchronized <T> T map(final QuintFunction<A, B, C, D, E, T> func) {
		return super.map(func);
	}

	@Override
	public synchronized <T, U, V, W, X> MutableQuintuple<T, U, V, W, X> map(
			final QuintFunction<A, B, C, D, E, T> funcFirst,
			final QuintFunction<A, B, C, D, E, U> funcSecond,
			final QuintFunction<A, B, C, D, E, V> funcThird,
			final QuintFunction<A, B, C, D, E, W> funcFourth,
			final QuintFunction<A, B, C, D, E, X> funcFifth) {
		return super.map(funcFirst, funcSecond, funcThird, funcFourth, funcFifth);
	}

	@Override
	public synchronized <T> MutableQuintuple<T, B, C, D, E> mapFirst(final QuintFunction<A, B, C, D, E, T> func) {
		return super.mapFirst(func);
	}

	@Override
	public synchronized <T> MutableQuintuple<A, T, C, D, E> mapSecond(final QuintFunction<A, B, C, D, E, T> func) {
		return super.mapSecond(func);
	}

	@Override
	public synchronized <T> MutableQuintuple<A, B, T, D, E> mapThird(final QuintFunction<A, B, C, D, E, T> func) {
		return super.mapThird(func);
	}

	@Override
	public synchronized <T> MutableQuintuple<A, B, C, T, E> mapFourth(final QuintFunction<A, B, C, D, E, T> func) {
		return super.mapFourth(func);
	}

	@Override
	public synchronized <T> MutableQuintuple<A, B, C, D, T> mapFifth(final QuintFunction<A, B, C, D, E, T> func) {
		return super.mapFifth(func);
	}

	@Override
	public synchronized <T> MutableQuintuple<?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		return super.map(index, func);
	}

	@Override
	public synchronized MutableQuintuple<A, B, C, D, E> mut(
			final QuintFunction<A, B, C, D, E, A> funcFirst,
			final QuintFunction<A, B, C, D, E, B> funcSecond,
			final QuintFunction<A, B, C, D, E, C> funcThird,
			final QuintFunction<A, B, C, D, E, D> funcFourth,
			final QuintFunction<A, B, C, D, E, E> funcFifth) {
		return super.mut(funcFirst, funcSecond, funcThird, funcFourth, funcFifth);
	}

	@Override
	public synchronized MutableQuintuple<A, B, C, D, E> mutFirst(final QuintFunction<A, B, C, D, E, A> func) {
		return super.mutFirst(func);
	}

	@Override
	public synchronized MutableQuintuple<A, B, C, D, E> mutSecond(final QuintFunction<A, B, C, D, E, B> func) {
		return super.mutSecond(func);
	}

	@Override
	public synchronized MutableQuintuple<A, B, C, D, E> mutThird(final QuintFunction<A, B, C, D, E, C> func) {
		return super.mutThird(func);
	}

	@Override
	public synchronized MutableQuintuple<A, B, C, D, E> mutFourth(final QuintFunction<A, B, C, D, E, D> func) {
		return super.mutFourth(func);
	}

	@Override
	public synchronized MutableQuintuple<A, B, C, D, E> mutFifth(final QuintFunction<A, B, C, D, E, E> func) {
		return super.mutFifth(func);
	}

	@Override
	public synchronized <T> MutableQuintuple<A, B, C, D, E> mut(final int index, final Function<Object, T> func) {
		return super.mut(index, func);
	}

	@Override
	public synchronized Quintuple<A, B, C, D, E> setFirst(final A first) {
		return super.setFirst(first);
	}

	@Override
	public synchronized Quintuple<A, B, C, D, E> setSecond(final B second) {
		return super.setSecond(second);
	}

	@Override
	public synchronized Quintuple<A, B, C, D, E> setThird(final C third) {
		return super.setThird(third);
	}

	@Override
	public synchronized Quintuple<A, B, C, D, E> setFourth(final D fourth) {
		return super.setFourth(fourth);
	}

	@Override
	public synchronized Quintuple<A, B, C, D, E> setFifth(final E fifth) {
		return super.setFifth(fifth);
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}

	@Override
	public synchronized <T> MutableQuintuple<A, B, C, D, E> set(final int index, final T element) {
		return super.set(index, element);
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
	public synchronized Quintuple<A, B, C, D, E> viewReadOnly() {
		return super.viewReadOnly();
	}

	@Override
	public synchronized MutableQuintuple<A, B, C, D, E> viewMutable() {
		return super.viewMutable();
	}

	@Override
	public synchronized MutableQuintuple<A, B, C, D, E> asSynchronized() {
		return this;
	}

	@Override
	public synchronized boolean isSynchronized() {
		return true;
	}
}
