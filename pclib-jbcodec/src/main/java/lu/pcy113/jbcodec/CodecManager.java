package lu.pcy113.jbcodec;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

import lu.pcy113.jbcodec.decoder.BooleanDecoder;
import lu.pcy113.jbcodec.decoder.ByteDecoder;
import lu.pcy113.jbcodec.decoder.CharacterDecoder;
import lu.pcy113.jbcodec.decoder.Decoder;
import lu.pcy113.jbcodec.decoder.DecoderNotFoundException;
import lu.pcy113.jbcodec.decoder.DoubleDecoder;
import lu.pcy113.jbcodec.decoder.FloatDecoder;
import lu.pcy113.jbcodec.decoder.IntegerDecoder;
import lu.pcy113.jbcodec.decoder.LongDecoder;
import lu.pcy113.jbcodec.decoder.NullDecoder;
import lu.pcy113.jbcodec.decoder.PlatformStringDecoder;
import lu.pcy113.jbcodec.decoder.ShortDecoder;
import lu.pcy113.jbcodec.decoder.VoidDecoder;
import lu.pcy113.jbcodec.encoder.BooleanEncoder;
import lu.pcy113.jbcodec.encoder.ByteEncoder;
import lu.pcy113.jbcodec.encoder.CharacterEncoder;
import lu.pcy113.jbcodec.encoder.DoubleEncoder;
import lu.pcy113.jbcodec.encoder.Encoder;
import lu.pcy113.jbcodec.encoder.EncoderNotFoundException;
import lu.pcy113.jbcodec.encoder.FloatEncoder;
import lu.pcy113.jbcodec.encoder.IntegerEncoder;
import lu.pcy113.jbcodec.encoder.LongEncoder;
import lu.pcy113.jbcodec.encoder.NullEncoder;
import lu.pcy113.jbcodec.encoder.PlatformStringEncoder;
import lu.pcy113.jbcodec.encoder.ShortEncoder;
import lu.pcy113.jbcodec.encoder.VoidEncoder;
import lu.pcy113.pclib.datastructure.pair.Pair;

public class CodecManager {

	public static final int HEAD_SIZE = Short.BYTES;

	private HashMap<Short, Pair<Decoder, String>> registeredDecoders = new HashMap<>();
	private HashMap<String, Pair<Encoder, Short>> registeredEncoders = new HashMap<>();

	public void register(Decoder<?> d, short header) {
		registeredDecoders.put(header, new Pair<>(d, d.register(this, header)));
	}

	public void register(Encoder<?> e, short header) {
		registeredEncoders.put(e.register(this, header), new Pair<>(e, header));
	}

	public void register(Encoder<?> e, Decoder<?> d, short header) {
		register(d, header);
		register(e, header);
	}
	
	public void registerBoth(Encoder<?> e, short header) {
		register(e, header);
		if(e instanceof Decoder) {
			register((Decoder<?>) e, header);
		}
	}

	public <T> int estimateSize(boolean head, T obj) {
		@SuppressWarnings("unchecked")
		Encoder<T> encoder = ((Encoder<T>) getEncoderByObject(obj));

		return encoder.estimateSize(head, obj);
	}
	
	public <T> int estimateSize(T obj) {
		return this.estimateSize(true, obj);
	}

	public Decoder getDecoder(short header) {
		Pair<Decoder, String> dec = registeredDecoders.get(header);
		return (dec == null ? null : dec.getKey());
	}

	public <T> Decoder<T> getDecoderByClass(Class<T> clazz) {
		return registeredDecoders
				.values()
				.stream()
				.filter(e -> Objects.equals(e.getValue(), clazz.getName()))
				.findFirst()
				.map(e -> e == null ? null : e.getKey())
				.get();
	}
	
	public Encoder getEncoder(short header) {
		return registeredEncoders
				.values()
				.stream()
				.filter(e -> e.getValue() == header)
				.findFirst()
				.map(e -> e == null ? null : e.getKey())
				.get();
	}

	public Encoder getEncoderByClassName(String name) {
		if (!registeredEncoders.containsKey(name)) {
			throw new EncoderNotFoundException("Encoder for class: " + name + " not registered in CodecManager.");
		}

		return registeredEncoders.get(name).getKey();
	}

	public Encoder getEncoderByObject(Object obj) {
		if (obj != null) {
			try {
				String name = obj.getClass().getName().replace("^class\\s", "");
				if (registeredEncoders.containsKey(name)) {
					return registeredEncoders.get(name).getKey();
				}
			} catch (Exception e) {
				throw new EncoderNotFoundException(e, "Error while getting encoder for object: " + obj);
			}
		}

		for (Entry<String, Pair<Encoder, Short>> e : registeredEncoders.entrySet()) {
			if (e.getValue().getKey().confirmType(obj)) {
				return e.getValue().getKey();
			}
		}

		throw new EncoderNotFoundException("Encoder for: " + (obj != null ? obj.getClass() : "NullType") + "; not registered in CodecManager.");
	}

	public Encoder getEncoderByClass(Class<?> clazz) {
		String name = clazz.getName();
		if (registeredEncoders.containsKey(name)) {
			return registeredEncoders.get(name).getKey();
		}

		for (Entry<String, Pair<Encoder, Short>> e : registeredEncoders.entrySet()) {
			if (e.getValue().getKey().confirmClassType(clazz)) {
				return e.getValue().getKey();
			}
		}

		throw new EncoderNotFoundException("Encoder for: " + (clazz != null ? clazz : "NullType") + "; not registered in CodecManager.");
	}

	public ByteBuffer encode(Object o) {
		Encoder e = getEncoderByObject(o);
		if (e == null) {
			throw new EncoderNotFoundException("Encoder for: " + (o != null ? o.getClass() : "NullType") + "; not registered in CodecManager.");
		}
		return e.encode(true, o);
	}

	public ByteBuffer encode(boolean b, Object o) {
		Encoder e = getEncoderByObject(o);
		if (e == null) {
			throw new EncoderNotFoundException("Encoder for: " + (o != null ? o.getClass() : "NullType") + "; not registered in CodecManager.");
		}
		return e.encode(b, o);
	}

	public Object decode(ByteBuffer bb) {
		short header = bb.getShort();
		Decoder dec = getDecoder(header);
		if (dec == null)
			throw new DecoderNotFoundException(header);
		return dec.decode(false, bb);
	}

	/**
	 * Registers the following D/Encoders:<br>
	 * 0. Null<br>
	 * 1. Byte<br>
	 * 2. Short<br>
	 * 3. Integer<br>
	 * 4. Double<br>
	 * 5. Float<br>
	 * 6. Long<br>
	 * 7. Character<br>
	 * 8. String<br>
	 * 9. Void<br>
	 * 10. Boolean<br>
	 */
	public static final CodecManager base() {
		CodecManager cm = new CodecManager();

		cm.register(new NullEncoder(), new NullDecoder(), (short) 0);
		cm.register(new ByteEncoder(), new ByteDecoder(), (short) 1);
		cm.register(new ShortEncoder(), new ShortDecoder(), (short) 2);
		cm.register(new IntegerEncoder(), new IntegerDecoder(), (short) 3);
		cm.register(new DoubleEncoder(), new DoubleDecoder(), (short) 4);
		cm.register(new FloatEncoder(), new FloatDecoder(), (short) 5);
		cm.register(new LongEncoder(), new LongDecoder(), (short) 6);
		cm.register(new CharacterEncoder(), new CharacterDecoder(), (short) 7);
		cm.register(new PlatformStringEncoder(), new PlatformStringDecoder(), (short) 8);
		cm.register(new VoidEncoder(), new VoidDecoder(), (short) 9);
		cm.register(new BooleanEncoder(), new BooleanDecoder(), (short) 10);

		return cm;
	}

}