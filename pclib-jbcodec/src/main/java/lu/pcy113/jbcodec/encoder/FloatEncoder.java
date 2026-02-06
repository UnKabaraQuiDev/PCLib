package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;

public class FloatEncoder extends DefaultObjectEncoder<Float> {

	@Override
	public ByteBuffer encode(boolean head, Float obj) {
		ByteBuffer bb = ByteBuffer.allocate(8 + (head ? 2 : 0));
		if (head)
			bb.putShort(header);
		bb.putDouble(obj);

		bb.flip();
		return bb;
	}
	
	@Override
	public int estimateSize(boolean head, Float obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Float.BYTES;
	}

}
