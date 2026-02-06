package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;

public class IntegerEncoder extends DefaultObjectEncoder<Integer> {

	@Override
	public ByteBuffer encode(boolean head, Integer obj) {
		ByteBuffer bb = ByteBuffer.allocate(4 + (head ? 2 : 0));
		if (head)
			bb.putShort(header);
		bb.putInt(obj);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(boolean head, Integer obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Integer.BYTES;
	}

}
