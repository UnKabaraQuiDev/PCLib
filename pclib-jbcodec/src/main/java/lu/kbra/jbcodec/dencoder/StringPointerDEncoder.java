package lu.kbra.jbcodec.dencoder;

import java.nio.ByteBuffer;

import lu.kbra.pclib.pointer.StringPointer;

public class StringPointerDEncoder extends DefaultObjectDEncoder<StringPointer> {

	@Override
	public ByteBuffer encode(final boolean head, final StringPointer obj) {
		final ByteBuffer bb = ByteBuffer.allocate(this.estimateSize(head, obj));

		super.putHeader(head, bb);

		bb.put(this.cm.encode(true, obj.get()));

		bb.flip();

		return bb;
	}

	@Override
	public StringPointer decode(final boolean head, final ByteBuffer bb) {
		super.verifyHeader(head, bb);

		return new StringPointer((String) this.cm.decode(bb));
	}

	@Override
	public int estimateSize(final boolean head, final StringPointer obj) {
		return super.estimateHeaderSize(head) + this.cm.estimateSize(true, obj.get());
	}

}
