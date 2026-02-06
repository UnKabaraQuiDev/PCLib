package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.pcy113.pclib.datastructure.triplet.ReadOnlyTriplet;
import lu.pcy113.pclib.datastructure.triplet.Triplet;

public class TripletEncoder extends DefaultObjectEncoder<Triplet<?, ?, ?>> {

	public TripletEncoder() {
		super(Triplet.class);
	}

	@Override
	public ByteBuffer encode(boolean head, Triplet<?, ?, ?> obj) {
		final int length = estimateSize(head, obj);
		ByteBuffer bb = ByteBuffer.allocate(length);
		super.putHeader(head, bb);

		bb.put(cm.encode(false, obj instanceof ReadOnlyTriplet));

		bb.put(cm.encode(true, obj.getFirst()));
		bb.put(cm.encode(true, obj.getSecond()));
		bb.put(cm.encode(true, obj.getThird()));

		bb.flip();

		return bb;
	}

	@Override
	public int estimateSize(boolean head, Triplet<?, ?, ?> obj) {
		return super.estimateHeaderSize(head) + cm.estimateSize(false, obj instanceof ReadOnlyTriplet) + cm.estimateSize(true, obj.getFirst()) + cm.estimateSize(true, obj.getSecond()) + cm.estimateSize(true, obj.getThird());
	}

	@Override
	public boolean confirmClassType(Class<?> clazz) {
		return Triplet.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean confirmType(Object obj) {
		return obj instanceof Triplet;
	}
}
