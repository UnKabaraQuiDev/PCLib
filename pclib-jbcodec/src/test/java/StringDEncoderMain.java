import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.jbcodec.decoder.StringDecoder;
import lu.pcy113.jbcodec.encoder.StringEncoder;
import lu.pcy113.pclib.PCUtils;

public class StringDEncoderMain {

	@Test
	public void platformTest() {
		System.out.println("PLATFORM --");
		System.out.println("Charsets: " + Charset.availableCharsets());
		System.out.println("Default Charset: " + Charset.defaultCharset());

		// assert Charset.isSupported("UTF-8") : "UTF-8 not supported";

		CodecManager cm = CodecManager.base();

		String input = "teeeeeest";

		ByteBuffer bb0 = cm.encode(input);

		assert bb0.remaining() == cm.estimateSize(input) : "Unexpected size: " + bb0.remaining() + " vs estimated: " + cm.estimateSize(input);
		assert bb0.capacity() == bb0.remaining() : "Unexpected capacity: " + bb0.capacity() + " vs remaining: " + bb0.remaining() + " (buffer not filled)";

		System.out.println("Content (" + bb0.capacity() + "): " + PCUtils.byteBufferToHexString(bb0));

		String output = (String) cm.decode(bb0);

		System.out.println(input + " = " + output);

		assert Objects.equals(input, output);
	}

	@Test
	public void asciiTest() {
		System.out.println("ASCII --");
		System.out.println("Charsets: " + Charset.availableCharsets());
		System.out.println("Default Charset: " + Charset.defaultCharset());

		assert Charset.isSupported("US-ASCII") : "ASCII not supported";

		CodecManager cm = CodecManager.base();
		cm.register(new StringEncoder("US-ASCII"), new StringDecoder("US-ASCII"), (short) 20);

		System.out.println("Using Charset: US-ASCII");

		String input = "teeeeeest";

		ByteBuffer bb0 = cm.encode(input);

		assert bb0.remaining() == cm.estimateSize(input) : "Unexpected size: " + bb0.remaining() + " vs estimated: " + cm.estimateSize(input);
		assert bb0.capacity() == bb0.remaining() : "Unexpected capacity: " + bb0.capacity() + " vs remaining: " + bb0.remaining() + " (buffer not filled)";

		System.out.println("Content (" + bb0.capacity() + "): " + PCUtils.byteBufferToHexString(bb0));

		String output = (String) cm.decode(bb0);

		System.out.println(input + " = " + output);

		assert Objects.equals(input, output);
	}

}
