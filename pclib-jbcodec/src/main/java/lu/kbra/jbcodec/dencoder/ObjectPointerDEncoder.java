package lu.kbra.jbcodec.dencoder;

import java.nio.ByteBuffer;

import lu.kbra.pclib.pointer.ObjectPointer;

public class ObjectPointerDEncoder extends DefaultObjectDEncoder<ObjectPointer> {

	public ObjectPointerDEncoder() {
		super(ObjectPointer.class);
	}

	@Override
	public ByteBuffer encode(final boolean head, final ObjectPointer obj) {
		final ByteBuffer bb = ByteBuffer.allocate(this.estimateSize(head, obj));

		super.putHeader(head, bb);

		bb.put(this.cm.encode(true, obj.get()));

		bb.flip();

		return bb;
	}

	@Override
	public ObjectPointer decode(final boolean head, final ByteBuffer bb) {
		super.verifyHeader(head, bb);

		return new ObjectPointer(this.cm.decode(bb));
	}

	@Override
	public int estimateSize(final boolean head, final ObjectPointer obj) {
		return super.estimateHeaderSize(head) + this.cm.estimateSize(true, obj.get());
	}

}
