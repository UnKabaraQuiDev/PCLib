package lu.kbra.p4j.crypto.decryptor;

import java.nio.ByteBuffer;

public class RawDecryptor implements Decryptor {

	@Override
	public ByteBuffer decrypt(final ByteBuffer in) {
		return in;
	}

}
