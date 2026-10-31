package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.QuadFunction;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public class MutableQuadrupleSyncImpl<A, B, C, D> extends MutableQuadrupleImpl<A, B, C, D> {

	public MutableQuadrupleSyncImpl() {
	}

	public MutableQuadrupleSyncImpl(final A first, final B second, final C third, final D fourth) {
		super(first, second, third, fourth);
	}

	@Override
	public synchronized Object[] asArray() {
		return super.asArray();
	}

	@Override
	public synchronized Quadruple<A, B, C, D> clone() {
		return super.clone();
	}

	@Override
	public synchronized MutableQuadruple<A, B, C, D> cloneMutable() {
		return super.cloneMutable();
	}

	@Override
	public synchronized ReadOnlyQuadruple<A, B, C, D> cloneReadOnly() {
		return super.cloneReadOnly();
	}

	@Override
	public synchronized MutableQuadruple<A, B, C, D> deepClone() {
		return super.deepClone();
	}

	@Override
	public synchronized MutableQuadruple<A, B, C, D> deepCloneMutable() {
		return super.deepCloneMutable();
	}

	@Override
	public synchronized ReadOnlyQuadruple<A, B, C, D> deepCloneReadOnly() {
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
	public synchronized <T> T map(final QuadFunction<A, B, C, D, T> func) {
		return super.map(func);
	}

	@Override
	public synchronized <T, U, V, W> MutableQuadruple<T, U, V, W> map(
			final QuadFunction<A, B, C, D, T> funcFirst,
			final QuadFunction<A, B, C, D, U> funcSecond,
			final QuadFunction<A, B, C, D, V> funcThird,
			final QuadFunction<A, B, C, D, W> funcFourth) {
		return super.map(funcFirst, funcSecond, funcThird, funcFourth);
	}

	@Override
	public synchronized <T> MutableQuadruple<T, B, C, D> mapFirst(final QuadFunction<A, B, C, D, T> func) {
		return super.mapFirst(func);
	}

	@Override
	public synchronized <T> MutableQuadruple<A, T, C, D> mapSecond(final QuadFunction<A, B, C, D, T> func) {
		return super.mapSecond(func);
	}

	@Override
	public synchronized <T> MutableQuadruple<A, B, T, D> mapThird(final QuadFunction<A, B, C, D, T> func) {
		return super.mapThird(func);
	}

	@Override
	public synchronized <T> MutableQuadruple<A, B, C, T> mapFourth(final QuadFunction<A, B, C, D, T> func) {
		return super.mapFourth(func);
	}

	@Override
	public synchronized <T> MutableQuadruple<?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		return super.map(index, func);
	}

	@Override
	public synchronized MutableQuadruple<A, B, C, D> mut(
			final QuadFunction<A, B, C, D, A> funcFirst,
			final QuadFunction<A, B, C, D, B> funcSecond,
			final QuadFunction<A, B, C, D, C> funcThird,
			final QuadFunction<A, B, C, D, D> funcFourth) {
		return super.mut(funcFirst, funcSecond, funcThird, funcFourth);
	}

	@Override
	public synchronized MutableQuadruple<A, B, C, D> mutFirst(final QuadFunction<A, B, C, D, A> func) {
		return super.mutFirst(func);
	}

	@Override
	public synchronized MutableQuadruple<A, B, C, D> mutSecond(final QuadFunction<A, B, C, D, B> func) {
		return super.mutSecond(func);
	}

	@Override
	public synchronized MutableQuadruple<A, B, C, D> mutThird(final QuadFunction<A, B, C, D, C> func) {
		return super.mutThird(func);
	}

	@Override
	public synchronized MutableQuadruple<A, B, C, D> mutFourth(final QuadFunction<A, B, C, D, D> func) {
		return super.mutFourth(func);
	}

	@Override
	public synchronized <T> MutableQuadruple<A, B, C, D> mut(final int index, final Function<Object, T> func) {
		return super.mut(index, func);
	}

	@Override
	public synchronized Quadruple<A, B, C, D> setFirst(final A first) {
		return super.setFirst(first);
	}

	@Override
	public synchronized Quadruple<A, B, C, D> setSecond(final B second) {
		return super.setSecond(second);
	}

	@Override
	public synchronized Quadruple<A, B, C, D> setThird(final C third) {
		return super.setThird(third);
	}

	@Override
	public synchronized Quadruple<A, B, C, D> setFourth(final D fourth) {
		return super.setFourth(fourth);
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}

	@Override
	public synchronized <T> MutableQuadruple<A, B, C, D> set(final int index, final T element) {
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
	public synchronized Quadruple<A, B, C, D> asViewReadOnly() {
		return super.asViewReadOnly();
	}

	@Override
	public synchronized MutableQuadruple<A, B, C, D> asViewMutable() {
		return super.asViewMutable();
	}

	@Override
	public synchronized MutableQuadruple<A, B, C, D> asSynchronized() {
		return this;
	}

	@Override
	public synchronized boolean isSynchronized() {
		return true;
	}
}
