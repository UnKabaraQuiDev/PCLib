package lu.kbra.p4j.crypto.encryptor;

import java.nio.ByteBuffer;

public interface Encryptor {

	ByteBuffer encrypt(ByteBuffer input) throws Exception;

}