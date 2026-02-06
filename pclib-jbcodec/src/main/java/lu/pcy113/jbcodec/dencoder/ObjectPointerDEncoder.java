package lu.pcy113.jbcodec.dencoder;

import java.nio.ByteBuffer;

import lu.pcy113.pclib.pointer.ObjectPointer;

public class ObjectPointerDEncoder extends DefaultObjectDEncoder<ObjectPointer> {

	public ObjectPointerDEncoder() {
		super(ObjectPointer.class);
	}

	@Override
	public ByteBuffer encode(boolean head, ObjectPointer obj) {
		final ByteBuffer bb = ByteBuffer.allocate(this.estimateSize(head, obj));

		super.putHeader(head, bb);

		bb.put(cm.encode(true, obj.get()));

		bb.flip();

		return bb;
	}

	@Override
	public ObjectPointer decode(boolean head, ByteBuffer bb) {
		super.verifyHeader(head, bb);

		return new ObjectPointer(cm.decode(bb));
	}

	@Override
	public int estimateSize(boolean head, ObjectPointer obj) {
		return super.estimateHeaderSize(head) + cm.estimateSize(true, obj.get());
	}

}
