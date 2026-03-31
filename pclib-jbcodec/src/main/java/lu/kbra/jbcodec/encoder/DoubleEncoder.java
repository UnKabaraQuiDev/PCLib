package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;

public class DoubleEncoder extends DefaultObjectEncoder<Double> {

	@Override
	public ByteBuffer encode(final boolean head, final Double obj) {
		final ByteBuffer bb = ByteBuffer.allocate(8 + (head ? 2 : 0));
		if (head) {
			bb.putShort(this.header);
		}
		bb.putDouble(obj);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final Double obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Double.BYTES;
	}

}
