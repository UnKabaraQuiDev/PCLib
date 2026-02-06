package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import lu.pcy113.jbcodec.CodecManager;

public class SingleArrayListEncoder extends DefaultObjectEncoder<ArrayList<?>> {

	public SingleArrayListEncoder() {
		super(ArrayList.class);
	}

	/**
	 * ( HEAD 2b - SIZE 4b - SUB HEAD 2b - DATA xb
	 */
	@Override
	public ByteBuffer encode(boolean head, ArrayList<?> obj) {
		ByteBuffer bb = ByteBuffer.allocate(estimateSize(head, obj));

		super.putHeader(head, bb);

		bb.putInt(obj.size());

		if (obj.size() != 0) {
			Encoder<?> encoder = cm.getEncoderByObject(obj.get(0));

			bb.putShort(encoder.header());

			for (Object o : obj) {
				bb.put(cm.encode(false, o));
			}
		}

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(boolean head, ArrayList<?> obj) {
		return super.estimateHeaderSize(head) + 4 + (obj.size() > 0 ? CodecManager.HEAD_SIZE : 0) + obj.stream().mapToInt(c -> cm.estimateSize(false, c)).sum();
	}

	@Override
	public boolean confirmClassType(Class<?> clazz) {
		return ArrayList.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean confirmType(Object obj) {
		return obj instanceof ArrayList;
	}

}
