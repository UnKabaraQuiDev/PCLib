package lu.pcy113.jbcodec.dencoder;

import java.nio.ByteBuffer;

import lu.pcy113.pclib.pointer.StringPointer;

public class StringPointerDEncoder extends DefaultObjectDEncoder<StringPointer> {

	@Override
	public ByteBuffer encode(boolean head, StringPointer obj) {
		final ByteBuffer bb = ByteBuffer.allocate(this.estimateSize(head, obj));

		super.putHeader(head, bb);
		
		bb.put(cm.encode(true, obj.get()));

		bb.flip();

		return bb;
	}

	@Override
	public StringPointer decode(boolean head, ByteBuffer bb) {
		super.verifyHeader(head, bb);
		
		return new StringPointer((String) cm.decode(bb));
	}

	@Override
	public int estimateSize(boolean head, StringPointer obj) {
		return super.estimateHeaderSize(head) + cm.estimateSize(true, obj.get());
	}

}
