package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;

public class ByteEncoder extends DefaultObjectEncoder<Byte> {

	@Override
	public ByteBuffer encode(final boolean head, final Byte obj) {
		final ByteBuffer bb = ByteBuffer.allocate(1 + (head ? 2 : 0));
		if (head) {
			bb.putShort(this.header);
		}
		bb.put(obj);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final Byte obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Byte.BYTES;
	}

}
