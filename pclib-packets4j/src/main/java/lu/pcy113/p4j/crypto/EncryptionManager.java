package lu.pcy113.p4j.crypto;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;

import javax.crypto.NoSuchPaddingException;

import lu.pcy113.p4j.crypto.decryptor.AESDecryptor;
import lu.pcy113.p4j.crypto.decryptor.Decryptor;
import lu.pcy113.p4j.crypto.decryptor.RawDecryptor;
import lu.pcy113.p4j.crypto.encryptor.AESEncryptor;
import lu.pcy113.p4j.crypto.encryptor.Encryptor;
import lu.pcy113.p4j.crypto.encryptor.RawEncryptor;

public class EncryptionManager {

	private Encryptor encryptor;
	private Decryptor decryptor;

	public EncryptionManager(Encryptor e, Decryptor d) {
		this.encryptor = e;
		this.decryptor = d;
	}

	public static final EncryptionManager raw() {
		return new EncryptionManager(new RawEncryptor(), new RawDecryptor());
	}

	public static final EncryptionManager aes(byte[] key) throws InvalidKeyException, NoSuchPaddingException {
		return new EncryptionManager(new AESEncryptor(key), new AESDecryptor(key));
	}

	public ByteBuffer decrypt(ByteBuffer b) throws Exception {
		return decryptor.decrypt(b);
	}

	public ByteBuffer encrypt(ByteBuffer b) throws Exception {
		return encryptor.encrypt(b);
	}

	public Encryptor getEncryptor() {
		return encryptor;
	}

	public Decryptor getDecryptor() {
		return decryptor;
	}

	public void setEncryptor(Encryptor encryptor) {
		this.encryptor = encryptor;
	}

	public void setDecryptor(Decryptor decryptor) {
		this.decryptor = decryptor;
	}

}
