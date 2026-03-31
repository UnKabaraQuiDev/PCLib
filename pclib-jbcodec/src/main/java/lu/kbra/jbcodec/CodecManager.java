package lu.kbra.jbcodec;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

import lu.kbra.jbcodec.decoder.BooleanDecoder;
import lu.kbra.jbcodec.decoder.ByteDecoder;
import lu.kbra.jbcodec.decoder.CharacterDecoder;
import lu.kbra.jbcodec.decoder.Decoder;
import lu.kbra.jbcodec.decoder.DecoderNotFoundException;
import lu.kbra.jbcodec.decoder.DoubleDecoder;
import lu.kbra.jbcodec.decoder.FloatDecoder;
import lu.kbra.jbcodec.decoder.IntegerDecoder;
import lu.kbra.jbcodec.decoder.LongDecoder;
import lu.kbra.jbcodec.decoder.NullDecoder;
import lu.kbra.jbcodec.decoder.PlatformStringDecoder;
import lu.kbra.jbcodec.decoder.ShortDecoder;
import lu.kbra.jbcodec.decoder.VoidDecoder;
import lu.kbra.jbcodec.encoder.BooleanEncoder;
import lu.kbra.jbcodec.encoder.ByteEncoder;
import lu.kbra.jbcodec.encoder.CharacterEncoder;
import lu.kbra.jbcodec.encoder.DoubleEncoder;
import lu.kbra.jbcodec.encoder.Encoder;
import lu.kbra.jbcodec.encoder.EncoderNotFoundException;
import lu.kbra.jbcodec.encoder.FloatEncoder;
import lu.kbra.jbcodec.encoder.IntegerEncoder;
import lu.kbra.jbcodec.encoder.LongEncoder;
import lu.kbra.jbcodec.encoder.NullEncoder;
import lu.kbra.jbcodec.encoder.PlatformStringEncoder;
import lu.kbra.jbcodec.encoder.ShortEncoder;
import lu.kbra.jbcodec.encoder.VoidEncoder;
import lu.kbra.pclib.datastructure.pair.Pair;

public class CodecManager {

	public static final int HEAD_SIZE = Short.BYTES;

	private final HashMap<Short, Pair<Decoder, String>> registeredDecoders = new HashMap<>();
	private final HashMap<String, Pair<Encoder, Short>> registeredEncoders = new HashMap<>();

	public void register(final Decoder<?> d, final short header) {
		this.registeredDecoders.put(header, new Pair<>(d, d.register(this, header)));
	}

	public void register(final Encoder<?> e, final short header) {
		this.registeredEncoders.put(e.register(this, header), new Pair<>(e, header));
	}

	public void register(final Encoder<?> e, final Decoder<?> d, final short header) {
		this.register(d, header);
		this.register(e, header);
	}

	public void registerBoth(final Encoder<?> e, final short header) {
		this.register(e, header);
		if (e instanceof Decoder) {
			this.register((Decoder<?>) e, header);
		}
	}

	public <T> int estimateSize(final boolean head, final T obj) {
		@SuppressWarnings("unchecked") final Encoder<T> encoder = this.getEncoderByObject(obj);

		return encoder.estimateSize(head, obj);
	}

	public <T> int estimateSize(final T obj) {
		return this.estimateSize(true, obj);
	}

	public Decoder getDecoder(final short header) {
		final Pair<Decoder, String> dec = this.registeredDecoders.get(header);
		return dec == null ? null : dec.getKey();
	}

	public <T> Decoder<T> getDecoderByClass(final Class<T> clazz) {
		return this.registeredDecoders.values()
				.stream()
				.filter(e -> Objects.equals(e.getValue(), clazz.getName()))
				.findFirst()
				.map(e -> e == null ? null : e.getKey())
				.get();
	}

	public Encoder getEncoder(final short header) {
		return this.registeredEncoders.values()
				.stream()
				.filter(e -> e.getValue() == header)
				.findFirst()
				.map(e -> e == null ? null : e.getKey())
				.get();
	}

	public Encoder getEncoderByClassName(final String name) {
		if (!this.registeredEncoders.containsKey(name)) {
			throw new EncoderNotFoundException("Encoder for class: " + name + " not registered in CodecManager.");
		}

		return this.registeredEncoders.get(name).getKey();
	}

	public Encoder getEncoderByObject(final Object obj) {
		if (obj != null) {
			try {
				final String name = obj.getClass().getName().replace("^class\\s", "");
				if (this.registeredEncoders.containsKey(name)) {
					return this.registeredEncoders.get(name).getKey();
				}
			} catch (final Exception e) {
				throw new EncoderNotFoundException(e, "Error while getting encoder for object: " + obj);
			}
		}

		for (final Entry<String, Pair<Encoder, Short>> e : this.registeredEncoders.entrySet()) {
			if (e.getValue().getKey().confirmType(obj)) {
				return e.getValue().getKey();
			}
		}

		throw new EncoderNotFoundException(
				"Encoder for: " + (obj != null ? obj.getClass() : "NullType") + "; not registered in CodecManager.");
	}

	public Encoder getEncoderByClass(final Class<?> clazz) {
		final String name = clazz.getName();
		if (this.registeredEncoders.containsKey(name)) {
			return this.registeredEncoders.get(name).getKey();
		}

		for (final Entry<String, Pair<Encoder, Short>> e : this.registeredEncoders.entrySet()) {
			if (e.getValue().getKey().confirmClassType(clazz)) {
				return e.getValue().getKey();
			}
		}

		throw new EncoderNotFoundException("Encoder for: " + (clazz != null ? clazz : "NullType") + "; not registered in CodecManager.");
	}

	public ByteBuffer encode(final Object o) {
		final Encoder e = this.getEncoderByObject(o);
		if (e == null) {
			throw new EncoderNotFoundException(
					"Encoder for: " + (o != null ? o.getClass() : "NullType") + "; not registered in CodecManager.");
		}
		return e.encode(true, o);
	}

	public ByteBuffer encode(final boolean b, final Object o) {
		final Encoder e = this.getEncoderByObject(o);
		if (e == null) {
			throw new EncoderNotFoundException(
					"Encoder for: " + (o != null ? o.getClass() : "NullType") + "; not registered in CodecManager.");
		}
		return e.encode(b, o);
	}

	public Object decode(final ByteBuffer bb) {
		final short header = bb.getShort();
		final Decoder dec = this.getDecoder(header);
		if (dec == null) {
			throw new DecoderNotFoundException(header);
		}
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
		final CodecManager cm = new CodecManager();

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
