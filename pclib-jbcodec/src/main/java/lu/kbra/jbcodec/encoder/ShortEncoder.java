package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;

public class ShortEncoder extends DefaultObjectEncoder<Short> {

	@Override
	public ByteBuffer encode(boolean head, Short obj) {
		ByteBuffer bb = ByteBuffer.allocate(2 + (head ? 2 : 0));
		if (head)
			bb.putShort(header);
		bb.putShort(obj);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(boolean head, Short obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Short.BYTES;
	}

}
