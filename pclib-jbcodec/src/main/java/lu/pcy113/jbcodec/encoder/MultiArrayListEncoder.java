package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MultiArrayListEncoder extends DefaultObjectEncoder<ArrayList<?>> {

	public MultiArrayListEncoder() {
		super(ArrayList.class);
	}

	/**
	 * ( HEAD 2b - SIZE 4b - SUB HEAD 2b - DATA xb
	 */
	@Override
	public ByteBuffer encode(boolean head, ArrayList<?> obj) {
		List<ByteBuffer> elements = new ArrayList<>();
		for (Object o : obj) {
			elements.add(cm.encode(true, o));
		}

		ByteBuffer bb = ByteBuffer.allocate(estimateSize(head, obj));

		super.putHeader(head, bb);

		bb.putInt(obj.size());

		for (ByteBuffer bbb : elements) {
			bb.put(bbb);
		}

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(boolean head, ArrayList<?> obj) {
		return super.estimateHeaderSize(head) + 4 + obj.stream().mapToInt(c -> cm.estimateSize(true, c)).sum();
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
