package lu.kbra.jbcodec.dencoder;

import lu.kbra.jbcodec.CodecManager;

public interface DEncoder {

	CodecManager codecManager();

	short header();

	String register(CodecManager cm, short header);

	Class<?> type();

	default void verifyRegister() {
		if (this.codecManager() != null) {
			throw new IllegalArgumentException("Cannot register D/Encoder to more than one CodecManager.");
		}
	}

}
