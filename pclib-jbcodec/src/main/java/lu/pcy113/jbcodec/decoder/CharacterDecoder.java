package lu.pcy113.jbcodec.decoder;

import java.nio.ByteBuffer;

public class CharacterDecoder extends DefaultObjectDecoder<Character> {

	@Override
	public Character decode(boolean head, ByteBuffer bb) {
		verifyHeader(head, bb);

		return bb.getChar();
	}

}
