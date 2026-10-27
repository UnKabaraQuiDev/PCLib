package lu.kbra.pclib.datastructure.tuple;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lu.kbra.pclib.impl.function.SextFunction;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public class MutableSextupleSyncImpl<A, B, C, D, E, F> extends MutableSextupleImpl<A, B, C, D, E, F> {

	public MutableSextupleSyncImpl() {
	}

	public MutableSextupleSyncImpl(final A first, final B second, final C third, final D fourth, final E fifth, final F sixth) {
		super(first, second, third, fourth, fifth, sixth);
	}

	@Override
	public synchronized Object[] asArray() {
		return super.asArray();
	}

	@Override
	public synchronized Sextuple<A, B, C, D, E, F> clone() {
		return super.clone();
	}

	@Override
	public synchronized MutableSextuple<A, B, C, D, E, F> cloneMutable() {
		return super.cloneMutable();
	}

	@Override
	public synchronized ReadOnlySextuple<A, B, C, D, E, F> cloneReadOnly() {
		return super.cloneReadOnly();
	}

	@Override
	public synchronized MutableSextuple<A, B, C, D, E, F> deepClone() {
		return super.deepClone();
	}

	@Override
	public synchronized MutableSextuple<A, B, C, D, E, F> deepCloneMutable() {
		return super.deepCloneMutable();
	}

	@Override
	public synchronized ReadOnlySextuple<A, B, C, D, E, F> deepCloneReadOnly() {
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
	public synchronized boolean hasSixth() {
		return super.hasSixth();
	}

	@Override
	public synchronized <T> T map(final SextFunction<A, B, C, D, E, F, T> func) {
		return super.map(func);
	}

	@Override
	public synchronized <T, U, V, W, X, Y> MutableSextuple<T, U, V, W, X, Y> map(
			final SextFunction<A, B, C, D, E, F, T> funcFirst,
			final SextFunction<A, B, C, D, E, F, U> funcSecond,
			final SextFunction<A, B, C, D, E, F, V> funcThird,
			final SextFunction<A, B, C, D, E, F, W> funcFourth,
			final SextFunction<A, B, C, D, E, F, X> funcFifth,
			final SextFunction<A, B, C, D, E, F, Y> funcSixth) {
		return super.map(funcFirst, funcSecond, funcThird, funcFourth, funcFifth, funcSixth);
	}

	@Override
	public synchronized <T> MutableSextuple<T, B, C, D, E, F> mapFirst(final SextFunction<A, B, C, D, E, F, T> func) {
		return super.mapFirst(func);
	}

	@Override
	public synchronized <T> MutableSextuple<A, T, C, D, E, F> mapSecond(final SextFunction<A, B, C, D, E, F, T> func) {
		return super.mapSecond(func);
	}

	@Override
	public synchronized <T> MutableSextuple<A, B, T, D, E, F> mapThird(final SextFunction<A, B, C, D, E, F, T> func) {
		return super.mapThird(func);
	}

	@Override
	public synchronized <T> MutableSextuple<A, B, C, T, E, F> mapFourth(final SextFunction<A, B, C, D, E, F, T> func) {
		return super.mapFourth(func);
	}

	@Override
	public synchronized <T> MutableSextuple<A, B, C, D, T, F> mapFifth(final SextFunction<A, B, C, D, E, F, T> func) {
		return super.mapFifth(func);
	}

	@Override
	public synchronized <T> MutableSextuple<A, B, C, D, E, T> mapSixth(final SextFunction<A, B, C, D, E, F, T> func) {
		return super.mapSixth(func);
	}

	@Override
	public synchronized <T> MutableSextuple<?, ?, ?, ?, ?, ?> map(final int index, final Function<Object, T> func) {
		return super.map(index, func);
	}

	@Override
	public synchronized MutableSextuple<A, B, C, D, E, F> mut(
			final SextFunction<A, B, C, D, E, F, A> funcFirst,
			final SextFunction<A, B, C, D, E, F, B> funcSecond,
			final SextFunction<A, B, C, D, E, F, C> funcThird,
			final SextFunction<A, B, C, D, E, F, D> funcFourth,
			final SextFunction<A, B, C, D, E, F, E> funcFifth,
			final SextFunction<A, B, C, D, E, F, F> funcSixth) {
		return super.mut(funcFirst, funcSecond, funcThird, funcFourth, funcFifth, funcSixth);
	}

	@Override
	public synchronized MutableSextuple<A, B, C, D, E, F> mutFirst(final SextFunction<A, B, C, D, E, F, A> func) {
		return super.mutFirst(func);
	}

	@Override
	public synchronized MutableSextuple<A, B, C, D, E, F> mutSecond(final SextFunction<A, B, C, D, E, F, B> func) {
		return super.mutSecond(func);
	}

	@Override
	public synchronized MutableSextuple<A, B, C, D, E, F> mutThird(final SextFunction<A, B, C, D, E, F, C> func) {
		return super.mutThird(func);
	}

	@Override
	public synchronized MutableSextuple<A, B, C, D, E, F> mutFourth(final SextFunction<A, B, C, D, E, F, D> func) {
		return super.mutFourth(func);
	}

	@Override
	public synchronized MutableSextuple<A, B, C, D, E, F> mutFifth(final SextFunction<A, B, C, D, E, F, E> func) {
		return super.mutFifth(func);
	}

	@Override
	public synchronized MutableSextuple<A, B, C, D, E, F> mutSixth(final SextFunction<A, B, C, D, E, F, F> func) {
		return super.mutSixth(func);
	}

	@Override
	public synchronized <T> MutableSextuple<A, B, C, D, E, F> mut(final int index, final Function<Object, T> func) {
		return super.mut(index, func);
	}

	@Override
	public synchronized Sextuple<A, B, C, D, E, F> setFirst(final A first) {
		return super.setFirst(first);
	}

	@Override
	public synchronized Sextuple<A, B, C, D, E, F> setSecond(final B second) {
		return super.setSecond(second);
	}

	@Override
	public synchronized Sextuple<A, B, C, D, E, F> setThird(final C third) {
		return super.setThird(third);
	}

	@Override
	public synchronized Sextuple<A, B, C, D, E, F> setFourth(final D fourth) {
		return super.setFourth(fourth);
	}

	@Override
	public synchronized Sextuple<A, B, C, D, E, F> setFifth(final E fifth) {
		return super.setFifth(fifth);
	}

	@Override
	public synchronized Sextuple<A, B, C, D, E, F> setSixth(final F sixth) {
		return super.setSixth(sixth);
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}

	@Override
	public synchronized <T> MutableSextuple<A, B, C, D, E, F> set(final int index, final T element) {
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
	public synchronized F getSixth() {
		return super.getSixth();
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
	public synchronized Sextuple<A, B, C, D, E, F> asViewReadOnly() {
		return super.asViewReadOnly();
	}

	@Override
	public synchronized MutableSextuple<A, B, C, D, E, F> asViewMutable() {
		return super.asViewMutable();
	}

	@Override
	public synchronized MutableSextuple<A, B, C, D, E, F> asSynchronized() {
		return this;
	}

	@Override
	public synchronized boolean isSynchronized() {
		return true;
	}
}
