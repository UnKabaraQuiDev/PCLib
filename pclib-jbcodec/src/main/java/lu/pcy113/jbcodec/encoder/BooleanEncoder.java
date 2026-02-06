package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;

public class BooleanEncoder extends DefaultObjectEncoder<Boolean> {

	@Override
	public ByteBuffer encode(boolean head, Boolean obj) {
		ByteBuffer bb = ByteBuffer.allocate(1 + (head ? 2 : 0));
		if (head)
			bb.putShort(header);
		bb.put((byte) (obj ? 1 : 0));

		bb.flip();
		return bb;
	}
	
	@Override
	public int estimateSize(boolean head, Boolean obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Byte.BYTES;
	}
	
}
