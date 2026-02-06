package lu.pcy113.p4j.crypto.decryptor;

import java.nio.ByteBuffer;

public class RawDecryptor implements Decryptor {

	@Override
	public ByteBuffer decrypt(ByteBuffer in) {
		return in;
	}

}