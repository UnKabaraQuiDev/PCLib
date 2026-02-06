package lu.pcy113.p4j.crypto.decryptor;

import java.nio.ByteBuffer;

public interface Decryptor {

	ByteBuffer decrypt(ByteBuffer input) throws Exception;

}
