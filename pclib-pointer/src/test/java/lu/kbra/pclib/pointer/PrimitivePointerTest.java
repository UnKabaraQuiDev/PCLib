package lu.kbra.pclib.pointer;

import org.junit.jupiter.api.Assertions;
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

		Assertions.assertEquals(13, pointer.add(3));
		Assertions.assertEquals(11, pointer.sub(2));
		Assertions.assertEquals(22, pointer.mul(2));
		Assertions.assertEquals(11, pointer.div(2));
		Assertions.assertEquals(1, pointer.mod(5));
		Assertions.assertEquals(2, pointer.increment());
		Assertions.assertEquals(1, pointer.decrement());
		Assertions.assertEquals(Integer.valueOf(10), snapshot.get());

		pointer.setValue(42);
		Assertions.assertEquals(42, pointer.getValue());
		Assertions.assertEquals("IntPointer [value=42]", pointer.toSafeString());
	}

	@Test
	void longAndDoublePointersSupportArithmetic() {
		final LongPointer longs = new LongPointer(10L);
		Assertions.assertEquals(15L, longs.add(5L));
		Assertions.assertEquals(30L, longs.mul(2L));
		Assertions.assertEquals(6L, longs.div(5L));
		Assertions.assertEquals(1L, longs.mod(5L));
		Assertions.assertEquals(0L, longs.sub(1L));

		final DoublePointer doubles = new DoublePointer(2.5);
		Assertions.assertEquals(4.0, doubles.add(1.5), 0.00001);
		Assertions.assertEquals(8.0, doubles.mul(2), 0.00001);
		Assertions.assertEquals(4.0, doubles.div(2), 0.00001);
		Assertions.assertEquals(3.5, doubles.sub(0.5), 0.00001);
	}

	@Test
	void byteShortAndCharPointersUsePrimitiveArithmetic() {
		final BytePointer bytes = new BytePointer((byte) 8);
		Assertions.assertEquals((byte) 9, bytes.increment());
		Assertions.assertEquals((byte) 18, bytes.mul((byte) 2));
		Assertions.assertEquals((byte) 16, bytes.sub((byte) 2));

		final ShortPointer shorts = new ShortPointer((short) 12);
		Assertions.assertEquals((short) 15, shorts.add((short) 3));
		Assertions.assertEquals((short) 5, shorts.div((short) 3));

		final CharPointer chars = new CharPointer('A');
		Assertions.assertEquals('B', chars.increment());
		Assertions.assertEquals('A', chars.decrement());
		Assertions.assertEquals(Character.valueOf('A'), chars.toObjectPointer().get());
	}

	@Test
	void booleanPointerFlipsAndSignalsExistingValue() {
		final BooleanPointer pointer = new BooleanPointer(false);
		Assertions.assertTrue(pointer.flip());
		Assertions.assertTrue(pointer.getValue());
		Assertions.assertTrue(pointer.waitForTrue(100));
		Assertions.assertFalse(pointer.flip());
		Assertions.assertTrue(pointer.waitForFalse(100));
		Assertions.assertEquals(Boolean.FALSE, pointer.toObjectPointer().get());
	}

	@Test
	void primitivePointersRejectUnsetAndNullValues() {
		final IntPointer pointer = new IntPointer();
		Assertions.assertThrows(UnsupportedOperationException.class, pointer::isSet);
		Assertions.assertThrows(UnsupportedOperationException.class, pointer::waitForIsset);
		Assertions.assertThrows(NullPointerException.class, () -> pointer.set((Integer) null));
	}

}
