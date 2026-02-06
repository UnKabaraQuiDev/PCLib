package examples.class_object;

import java.nio.ByteBuffer;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.jbcodec.decoder.ClassDecoder;
import lu.pcy113.jbcodec.decoder.ObjectSerializableDecoder;
import lu.pcy113.jbcodec.encoder.ClassEncoder;
import lu.pcy113.jbcodec.encoder.ObjectSerializableEncoder;
import lu.pcy113.pclib.PCUtils;

public class ObjectSerializableMain {

	@Test
	public void objectSerializableEncoderTest() {
		CodecManager cm = CodecManager.base();

		cm.register(new ClassEncoder(), new ClassDecoder(), (short) 20);
		cm.register(new ObjectSerializableEncoder(), new ObjectSerializableDecoder(), (short) 21);

		Object input = new SerializableObject();
		
		ByteBuffer bb = cm.encode(true, input);
		
		assert bb.remaining() == cm.estimateSize(input) : "Unexpected size: " + bb.remaining() + " vs estimated: " + cm.estimateSize(input);
		assert bb.capacity() == bb.remaining() : "Unexpected capacity: " + bb.capacity() + " vs remaining: " + bb.remaining() + " (buffer not filled)";
		
		System.out.println("out: " +  PCUtils.byteBufferToHexString(bb));

		Object decoded = cm.decode(bb);
		
		System.out.println("decoded: " + decoded.getClass());
		
		assert Objects.equals(decoded.getClass(), input.getClass());
	}

}
