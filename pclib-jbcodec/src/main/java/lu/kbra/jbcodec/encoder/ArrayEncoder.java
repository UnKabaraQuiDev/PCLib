package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import lu.kbra.jbcodec.CodecManager;
import lu.kbra.pclib.PCUtils;

public class ArrayEncoder implements Encoder<Object[]> {

	public CodecManager cm;
	public short header;

	@Override
	public CodecManager codecManager() {
		return this.cm;
	}

	@Override
	public short header() {
		return this.header;
	}

	@Override
	public Class<?> type() {
		return null;
	}

	@Override
	public boolean confirmType(final Object o) {
		return o != null && o.getClass().isArray();
	}

	@Override
	public String register(final CodecManager cm, final short header) {
		this.verifyRegister();

		this.cm = cm;
		this.header = header;

		return "Array"; // type().getName();
	}

	/**
	 * ( HEAD 2b - SIZE 4b - DATA >=2b Data HEAD 2b DATA VALUE xb
	 */
	@Override
	public ByteBuffer encode(final boolean head, final Object[] obj) {
		final List<Byte> elements = new ArrayList<>();
		for (final Object o : obj) {
			elements.addAll(PCUtils.byteArrayToList(this.cm.encode(o).array()));
		}
		final ByteBuffer bb = ByteBuffer.allocate((head ? 2 : 0) + 4 + elements.size());
		if (head) {
			bb.putShort(this.header);
		}
		bb.putInt(obj.length);
		bb.put(PCUtils.byteListToPrimitive(elements));

		bb.flip();
		return bb;
	}

}
