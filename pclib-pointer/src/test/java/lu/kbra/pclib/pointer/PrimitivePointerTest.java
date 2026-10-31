package lu.kbra.pclib.pointer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.pointer.prim.BooleanPointer;
import lu.kbra.pclib.pointer.prim.BytePointer;
import lu.kbra.pclib.pointer.prim.CharPointer;
import lu.kbra.pclib.pointer.prim.DoublePointer;
import lu.kbra.pclib.pointer.prim.IntPointer;
import lu.kbra.pclib.pointer.prim.LongPointer;
import lu.kbra.pclib.pointer.prim.ShortPointer;

class PrimitivePointerTest {

	@Test
	void integerPointerSupportsArithmeticAndSnapshotConversion() {
		final IntPointer pointer = new IntPointer(10);
		final ObjectPointer<Integer> snapshot = pointer.toObjectPointer();

		assertEquals(13, pointer.add(3));
		assertEquals(11, pointer.sub(2));
		assertEquals(22, pointer.mul(2));
		assertEquals(11, pointer.div(2));
		assertEquals(1, pointer.mod(5));
		assertEquals(2, pointer.increment());
		assertEquals(1, pointer.decrement());
		assertEquals(Integer.valueOf(10), snapshot.get());

		pointer.setValue(42);
		assertEquals(42, pointer.getValue());
		assertEquals("IntPointer [value=42]", pointer.toSafeString());
	}

	@Test
	void longAndDoublePointersSupportArithmetic() {
		final LongPointer longs = new LongPointer(10L);
		assertEquals(15L, longs.add(5L));
		assertEquals(30L, longs.mul(2L));
		assertEquals(6L, longs.div(5L));
		assertEquals(1L, longs.mod(5L));
		assertEquals(0L, longs.sub(1L));

		final DoublePointer doubles = new DoublePointer(2.5);
		assertEquals(4.0, doubles.add(1.5), 0.00001);
		assertEquals(8.0, doubles.mul(2), 0.00001);
		assertEquals(4.0, doubles.div(2), 0.00001);
		assertEquals(3.5, doubles.sub(0.5), 0.00001);
	}

	@Test
	void byteShortAndCharPointersUsePrimitiveArithmetic() {
		final BytePointer bytes = new BytePointer((byte) 8);
		assertEquals((byte) 9, bytes.increment());
		assertEquals((byte) 18, bytes.mul((byte) 2));
		assertEquals((byte) 16, bytes.sub((byte) 2));

		final ShortPointer shorts = new ShortPointer((short) 12);
		assertEquals((short) 15, shorts.add((short) 3));
		assertEquals((short) 5, shorts.div((short) 3));

		final CharPointer chars = new CharPointer('A');
		assertEquals('B', chars.increment());
		assertEquals('A', chars.decrement());
		assertEquals(Character.valueOf('A'), chars.toObjectPointer().get());
	}

	@Test
	void booleanPointerFlipsAndSignalsExistingValue() {
		final BooleanPointer pointer = new BooleanPointer(false);
		assertTrue(pointer.flip());
		assertTrue(pointer.getValue());
		assertTrue(pointer.waitForTrue(100));
		assertFalse(pointer.flip());
		assertTrue(pointer.waitForFalse(100));
		assertEquals(Boolean.FALSE, pointer.toObjectPointer().get());
	}

	@Test
	void primitivePointersRejectUnsetAndNullValues() {
		final IntPointer pointer = new IntPointer();
		assertThrows(UnsupportedOperationException.class, pointer::isSet);
		assertThrows(UnsupportedOperationException.class, pointer::waitForIsset);
		assertThrows(NullPointerException.class, () -> pointer.set((Integer) null));
	}

}
