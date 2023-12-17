package lu.pcy113.pclib;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class ByteArrayUtils {

	public static final ArrayList<Byte> byteArrayToList(byte[] b) {
		ArrayList<Byte> al = new ArrayList<>(b.length);
		for (int i = 0; i < b.length; i++)
			al.add(b[i]);
		return al;
	}

	public static final byte[] byteListToPrimitive(List<Byte> bytes) {
		byte[] b = new byte[bytes.size()];
		for (int i = 0; i < b.length; i++)
			b[i] = bytes.get(i);
		return b;
	}

	public static String byteArrayToHexString(byte[] byteArray) {
		StringBuilder sb = new StringBuilder();
		for (byte b : byteArray) {
			sb.append(String.format("%02X ", b));
		}
		return sb.toString();
	}

	/**
	 * does not change the reader's position
	 */
	public static String byteBufferToHexString(ByteBuffer bb) {
		int x = bb.position();
		StringBuilder sb = new StringBuilder();
		while (bb.hasRemaining()) {
			sb.append(String.format("%02X ", bb.get()));
		}
		bb.position(x);
		return sb.toString();
	}

}
