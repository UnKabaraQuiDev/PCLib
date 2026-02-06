package lu.kbra.jbcodec.decoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.dencoder.DEncoder;

public interface Decoder<T> extends DEncoder {

	T decode(boolean head, ByteBuffer bb);

	default void verifyHeader(boolean head, ByteBuffer bb) {
		if (head)
			Decoder.decoderNotCompatible(bb.getShort(), header());
	}

	static void decoderNotCompatible(short nheader, short header) throws DecoderNotCompatibleException {
		if (nheader != header)
			throw new DecoderNotCompatibleException(
					"Decoder not compatible with header: " + nheader + "; Header: " + header);
	}

}
