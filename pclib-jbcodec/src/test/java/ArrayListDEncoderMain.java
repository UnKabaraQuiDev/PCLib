import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.jbcodec.decoder.MultiArrayListDecoder;
import lu.pcy113.jbcodec.decoder.SingleArrayListDecoder;
import lu.pcy113.jbcodec.encoder.MultiArrayListEncoder;
import lu.pcy113.jbcodec.encoder.SingleArrayListEncoder;

public class ArrayListDEncoderMain {

	@Test
	public void singleTest() {
		System.out.println("SINGLE --");

		final ArrayList<String> originalMap = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			originalMap.add(Integer.toString(Integer.hashCode(i * (Integer.MAX_VALUE - 2000000))));
		}

		final CodecManager cm = CodecManager.base();

		cm.register(new SingleArrayListEncoder(), new SingleArrayListDecoder(), (short) 25);

		final ByteBuffer bb = cm.encode(originalMap);

		@SuppressWarnings("unchecked")
		ArrayList<String> backMap = (ArrayList<String>) cm.decode(bb);

		final Iterator<String> original = originalMap.iterator();
		final Iterator<String> back = backMap.iterator();

		while (original.hasNext() && back.hasNext()) {
			String orig = original.next();
			String bac = back.next();

			System.out.println(orig + " = " + bac);

			assert orig.equals(bac);
		}

		assert (original.hasNext() || back.hasNext()) == false : "Missing/too many elements: " + originalMap.size() + " vs" + backMap.size();
	}

	@Test
	public void multiTest() {
		System.out.println("MULTI --");

		final ArrayList<String> originalMap = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			originalMap.add(Integer.toString(Integer.hashCode(i * (Integer.MAX_VALUE - 2000000))));
		}

		final CodecManager cm = CodecManager.base();

		cm.register(new SingleArrayListEncoder(), new SingleArrayListDecoder(), (short) 25);

		final ByteBuffer bb = cm.encode(originalMap);

		@SuppressWarnings("unchecked")
		ArrayList<String> backMap = (ArrayList<String>) cm.decode(bb);

		final Iterator<String> original = originalMap.iterator();
		final Iterator<String> back = backMap.iterator();

		while (original.hasNext() && back.hasNext()) {
			String orig = original.next();
			String bac = back.next();

			System.out.println(orig + " = " + bac);

			assert orig.equals(bac);
		}

		assert (original.hasNext() || back.hasNext()) == false : "Missing/too many elements: " + originalMap.size() + " vs" + backMap.size();
	}

	@Test
	public void sizeComparaison() {
		final ArrayList<String> originalMap = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			originalMap.add(Integer.toString(Integer.hashCode(i * (Integer.MAX_VALUE - 2000000))));
		}

		CodecManager cm = CodecManager.base();

		cm.register(new MultiArrayListEncoder(), new MultiArrayListDecoder(), (short) 24);
		final ByteBuffer bbMulti = cm.encode(false, originalMap);

		cm.register(new SingleArrayListEncoder(), new SingleArrayListDecoder(), (short) 24);
		final ByteBuffer bbSingle = cm.encode(false, originalMap);

		System.out.println("SIZES --");
		System.out.println("MULTI: " + bbMulti.capacity() + " & SINGLE: " + bbSingle.capacity());

		assert bbMulti.capacity() > bbSingle.capacity();

	}

}
