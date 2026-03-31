package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.pclib.datastructure.triplet.ReadOnlyTriplet;
import lu.kbra.pclib.datastructure.triplet.Triplet;

public class TripletEncoder extends DefaultObjectEncoder<Triplet<?, ?, ?>> {

	public TripletEncoder() {
		super(Triplet.class);
	}

	@Override
	public ByteBuffer encode(final boolean head, final Triplet<?, ?, ?> obj) {
		final int length = this.estimateSize(head, obj);
		final ByteBuffer bb = ByteBuffer.allocate(length);
		super.putHeader(head, bb);

		bb.put(this.cm.encode(false, obj instanceof ReadOnlyTriplet));

		bb.put(this.cm.encode(true, obj.getFirst()));
		bb.put(this.cm.encode(true, obj.getSecond()));
		bb.put(this.cm.encode(true, obj.getThird()));

		bb.flip();

		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final Triplet<?, ?, ?> obj) {
		return super.estimateHeaderSize(head) + this.cm.estimateSize(false, obj instanceof ReadOnlyTriplet)
				+ this.cm.estimateSize(true, obj.getFirst()) + this.cm.estimateSize(true, obj.getSecond())
				+ this.cm.estimateSize(true, obj.getThird());
	}

	@Override
	public boolean confirmClassType(final Class<?> clazz) {
		return Triplet.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean confirmType(final Object obj) {
		return obj instanceof Triplet;
	}
}
