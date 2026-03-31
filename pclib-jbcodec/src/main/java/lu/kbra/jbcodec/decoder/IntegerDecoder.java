package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

public class IntegerDecoder extends DefaultObjectDecoder<Integer> {

	@Override
	public Integer decode(final boolean head, final ByteBuffer bb) {
		this.verifyHeader(head, bb);

		return bb.getInt();
	}

}
