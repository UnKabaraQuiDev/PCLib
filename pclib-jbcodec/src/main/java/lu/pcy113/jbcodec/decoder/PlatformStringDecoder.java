package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;

public class PlatformStringDecoder extends DefaultObjectDecoder<String> {

	@Override
	public String decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		final int length = bb.getInt();
		
		final byte[] arr = new byte[length];
		bb.get(arr);
		
		return new String(arr);
	}

}
