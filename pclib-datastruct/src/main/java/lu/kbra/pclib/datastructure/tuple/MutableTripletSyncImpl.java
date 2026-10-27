package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.function.TriFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public class MutableTripletSyncImpl<A, B, C> extends MutableTripletImpl<A, B, C> {

	public MutableTripletSyncImpl() {
	}

	public MutableTripletSyncImpl(final A first, final B second, final C third) {
		super(first, second, third);
	}

	@Override
	public synchronized Object[] asArray() {
		return super.asArray();
	}

	@Override
	public synchronized Triplet<A, B, C> clone() {
		return super.clone();
	}

	@Override
	public synchronized MutableTriplet<A, B, C> cloneMutable() {
		return super.cloneMutable();
	}

	@Override
	public synchronized ReadOnlyTriplet<A, B, C> cloneReadOnly() {
		return super.cloneReadOnly();
	}

	@Override
	public synchronized MutableTriplet<A, B, C> deepClone() {
		return super.deepClone();
	}

	@Override
	public synchronized MutableTriplet<A, B, C> deepCloneMutable() {
		return super.deepCloneMutable();
	}

	@Override
	public synchronized ReadOnlyTriplet<A, B, C> deepCloneReadOnly() {
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
	public synchronized <T> T map(final TriFunction<A, B, C, T> func) {
		return super.map(func);
	}

	@Override
	public synchronized <T, U, V> MutableTriplet<T, U, V> map(
			final TriFunction<A, B, C, T> funcFirst,
			final TriFunction<A, B, C, U> funcSecond,
			final TriFunction<A, B, C, V> funcThird) {
		return super.map(funcFirst, funcSecond, funcThird);
	}

	@Override
	public synchronized <T> MutableTriplet<T, B, C> mapFirst(final TriFunction<A, B, C, T> func) {
		return super.mapFirst(func);
	}

	@Override
	public synchronized <T> MutableTriplet<A, T, C> mapSecond(final TriFunction<A, B, C, T> func) {
		return super.mapSecond(func);
	}

	@Override
	public synchronized <T> MutableTriplet<A, B, T> mapThird(final TriFunction<A, B, C, T> func) {
		return super.mapThird(func);
	}

	@Override
	public synchronized <T> MutableTriplet<?, ?, ?> map(final int index, final Function<Object, T> func) {
		return super.map(index, func);
	}

	@Override
	public synchronized MutableTriplet<A, B, C> mut(
			final TriFunction<A, B, C, A> funcFirst,
			final TriFunction<A, B, C, B> funcSecond,
			final TriFunction<A, B, C, C> funcThird) {
		return super.mut(funcFirst, funcSecond, funcThird);
	}

	@Override
	public synchronized MutableTriplet<A, B, C> mutFirst(final TriFunction<A, B, C, A> func) {
		return super.mutFirst(func);
	}

	@Override
	public synchronized MutableTriplet<A, B, C> mutSecond(final TriFunction<A, B, C, B> func) {
		return super.mutSecond(func);
	}

	@Override
	public synchronized MutableTriplet<A, B, C> mutThird(final TriFunction<A, B, C, C> func) {
		return super.mutThird(func);
	}

	@Override
	public synchronized <T> MutableTriplet<A, B, C> mut(final int index, final Function<Object, T> func) {
		return super.mut(index, func);
	}

	@Override
	public synchronized Triplet<A, B, C> setFirst(final A first) {
		return super.setFirst(first);
	}

	@Override
	public synchronized Triplet<A, B, C> setSecond(final B second) {
		return super.setSecond(second);
	}

	@Override
	public synchronized Triplet<A, B, C> setThird(final C third) {
		return super.setThird(third);
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}

	@Override
	public synchronized <T> MutableTriplet<A, B, C> set(final int index, final T element) {
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
	public synchronized Triplet<A, B, C> viewReadOnly() {
		return super.viewReadOnly();
	}

	@Override
	public synchronized MutableTriplet<A, B, C> viewMutable() {
		return super.viewMutable();
	}

	@Override
	public synchronized MutableTriplet<A, B, C> asSynchronized() {
		return this;
	}

	@Override
	public synchronized boolean isSynchronized() {
		return true;
	}

}
