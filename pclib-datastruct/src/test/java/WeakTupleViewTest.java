
import java.lang.ref.WeakReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.datastructure.tuple.MutableOctuple;
import lu.kbra.pclib.datastructure.tuple.MutableOctupleImpl;
import lu.kbra.pclib.datastructure.tuple.MutablePair;
import lu.kbra.pclib.datastructure.tuple.MutablePairImpl;
import lu.kbra.pclib.datastructure.tuple.MutableTriplet;
import lu.kbra.pclib.datastructure.tuple.MutableTripletImpl;
import lu.kbra.pclib.datastructure.tuple.Triplet;
import lu.kbra.pclib.datastructure.tuple.Tuple;
import lu.kbra.pclib.datastructure.tuple.WeakTupleView;

class WeakTupleViewTest {

	@Test
	void mutablePairDelegatesReadsWritesAndCloning() {
		final MutablePair<String, Integer> original = new MutablePairImpl<>("a", 1);
		final MutablePair<String, Integer> weak = original.asWeakViewMutable();

		Assertions.assertTrue(weak instanceof WeakTupleView);
		Assertions.assertSame(original, ((WeakTupleView) weak).getParent());
		Assertions.assertTrue(((WeakTupleView) weak).isParentAvailable());
		Assertions.assertEquals("a", weak.getKey());

		weak.setValue(2);
		Assertions.assertEquals(2, original.getValue());
		original.setKey("b");
		Assertions.assertEquals("b", weak.getKey());
		Assertions.assertNotSame(original, weak.cloneMutable());
		Assertions.assertEquals(original.asArray().length, weak.asArray().length);
	}

	@Test
	void readOnlyViewOfMutableParentReflectsChanges() {
		final MutableTriplet<Integer, Integer, Integer> original = new MutableTripletImpl<>(1, 2, 3);
		final Triplet<Integer, Integer, Integer> weak = original.asWeakViewReadOnly();

		Assertions.assertFalse(weak.isMutable());
		Assertions.assertEquals(2, weak.getSecond());
		original.setSecond(4);
		Assertions.assertEquals(4, weak.getSecond());
	}

	@Test
	void clearedReferenceFailsWithDescriptiveException() {
		final MutablePair<String, String> original = new MutablePairImpl<>("k", "v");
		final MutablePair<String, String> weak = original.asWeakViewMutable();
		final WeakReference<? extends Tuple> reference = ((WeakTupleView) weak).getParentReference();
		reference.clear(); // deterministic simulation of garbage collection

		Assertions.assertFalse(((WeakTupleView) weak).isParentAvailable());
		Assertions.assertThrows(IllegalStateException.class, weak::getKey);
		Assertions.assertThrows(IllegalStateException.class, weak::hashCode);
		Assertions.assertThrows(IllegalStateException.class, () -> weak.setValue("x"));
	}

	@Test
	void octupleWeakViewDelegates() {
		final MutableOctuple<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> original = new MutableOctupleImpl<>(1,
				2,
				3,
				4,
				5,
				6,
				7,
				8);
		final MutableOctuple<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> weak = original.asWeakViewMutable();

		Assertions.assertEquals(8, weak.getEighth());
		weak.setEighth(9);
		Assertions.assertEquals(9, original.getEighth());
	}
}
