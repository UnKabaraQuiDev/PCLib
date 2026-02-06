package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;

public class LongEncoder extends DefaultObjectEncoder<Long> {

	@Override
	public ByteBuffer encode(boolean head, Long obj) {
		ByteBuffer bb = ByteBuffer.allocate(8 + (head ? 2 : 0));
		if (head)
			bb.putShort(header);
		bb.putLong(obj);

		bb.flip();
		return bb;
	}
	
	@Override
	public int estimateSize(boolean head, Long obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Long.BYTES;
	}

}
