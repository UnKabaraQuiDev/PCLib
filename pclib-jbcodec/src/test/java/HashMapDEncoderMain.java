import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.jbcodec.decoder.MultiHashMapDecoder;
import lu.pcy113.jbcodec.decoder.SingleHashMapDecoder;
import lu.pcy113.jbcodec.encoder.MultiHashMapEncoder;
import lu.pcy113.jbcodec.encoder.SingleHashMapEncoder;

public class HashMapDEncoderMain {

	@Test
	public void singleTest() {
		System.out.println("SINGLE --");

		final HashMap<String, String> originalMap = new HashMap<>();
		for (int i = 0; i < 10; i++) {
			originalMap.put(Integer.toString(Integer.hashCode(i)), Integer.toString(Double.hashCode(Math.random())));
		}

		final CodecManager cm = CodecManager.base();

		cm.register(new SingleHashMapEncoder(), new SingleHashMapDecoder(), (short) 25);

		final ByteBuffer bb = cm.encode(originalMap);

		assert bb.remaining() == cm.estimateSize(originalMap) : "Unexpected size: " + bb.remaining() + " vs estimated: " + cm.estimateSize(originalMap);
		assert bb.capacity() == bb.remaining() : "Unexpected capacity: " + bb.capacity() + " vs remaining: " + bb.remaining() + " (buffer not filled)";

		
		@SuppressWarnings("unchecked")
		HashMap<String, String> backMap = (HashMap<String, String>) cm.decode(bb);

		final Iterator<Entry<String, String>> original = originalMap.entrySet().iterator();
		final Iterator<Entry<String, String>> back = backMap.entrySet().iterator();

		while (original.hasNext() && back.hasNext()) {
			Entry<String, String> orig = original.next();
			Entry<String, String> bac = back.next();

			System.out.println(orig + " = " + bac);

			assert orig.equals(bac);
		}

		assert (original.hasNext() || back.hasNext()) == false : "Missing/too many elements: " + originalMap.size() + " vs" + backMap.size();
	}

	@Test
	public void multiTest() {
		System.out.println("MULTI --");

		final HashMap<String, String> originalMap = new HashMap<>();
		for (int i = 0; i < 10; i++) {
			originalMap.put(Integer.toString(Integer.hashCode(i)), Integer.toString(Double.hashCode(Math.random())));
		}

		final CodecManager cm = CodecManager.base();

		cm.register(new MultiHashMapEncoder(), new MultiHashMapDecoder(), (short) 25);

		final ByteBuffer bb = cm.encode(originalMap);

		assert bb.remaining() == cm.estimateSize(originalMap) : "Unexpected size: " + bb.remaining() + " vs estimated: " + cm.estimateSize(originalMap);
		assert bb.capacity() == bb.remaining() : "Unexpected capacity: " + bb.capacity() + " vs remaining: " + bb.remaining() + " (buffer not filled)";
		
		@SuppressWarnings("unchecked")
		HashMap<String, String> backMap = (HashMap<String, String>) cm.decode(bb);

		final Iterator<Entry<String, String>> original = originalMap.entrySet().iterator();
		final Iterator<Entry<String, String>> back = backMap.entrySet().iterator();

		while (original.hasNext() && back.hasNext()) {
			Entry<String, String> orig = original.next();
			Entry<String, String> bac = back.next();

			System.out.println(orig + " = " + bac);

			assert orig.equals(bac);
		}

		assert (original.hasNext() || back.hasNext()) == false : "Missing/too many elements: " + originalMap.size() + " vs" + backMap.size();
	}

	@Test
	public void sizeComparaison() {
		final HashMap<String, String> originalMap = new HashMap<>();
		for (int i = 0; i < 10; i++) {
			originalMap.put(Integer.toString(Integer.hashCode(i)), Integer.toString(Double.hashCode(Math.random())));
		}

		CodecManager cm = CodecManager.base();

		cm.register(new MultiHashMapEncoder(), new MultiHashMapDecoder(), (short) 24);
		final ByteBuffer bbMulti = cm.encode(false, originalMap);
		
		cm.register(new SingleHashMapEncoder(), new SingleHashMapDecoder(), (short) 24);
		final ByteBuffer bbSingle = cm.encode(false, originalMap);

		System.out.println("MULTI: " + bbMulti.capacity() + " & SINGLE: " + bbSingle.capacity());
		
		assert bbMulti.capacity() > bbSingle.capacity();

	}

}
