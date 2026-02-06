package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.pclib.PCUtils;

public class ArrayEncoder implements Encoder<Object[]> {

	public CodecManager cm;
	public short header;

	public CodecManager codecManager() {
		return cm;
	}

	public short header() {
		return header;
	}

	public Class<?> type() {
		return null;
	}

	public boolean confirmType(Object o) {
		return o != null && o.getClass().isArray();
	}

	public String register(CodecManager cm, short header) {
		verifyRegister();

		this.cm = cm;
		this.header = header;

		return "Array"; // type().getName();
	}

	/**
	 * ( HEAD 2b - SIZE 4b - DATA >=2b Data HEAD 2b DATA VALUE xb
	 */
	public ByteBuffer encode(boolean head, Object[] obj) {
		List<Byte> elements = new ArrayList<>();
		for (Object o : obj) {
			elements.addAll(PCUtils.byteArrayToList(cm.encode(o).array()));
		}
		ByteBuffer bb = ByteBuffer.allocate((head ? 2 : 0) + 4 + elements.size());
		if (head)
			bb.putShort(header);
		bb.putInt(obj.length);
		bb.put(PCUtils.byteListToPrimitive(elements));

		bb.flip();
		return bb;
	}

}
