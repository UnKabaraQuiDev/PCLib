package examples.car;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.decoder.DefaultObjectDecoder;

public class CarDecoder extends DefaultObjectDecoder<Car> {

	public CarDecoder() {
		super(Car.class);
	}

	@Override
	public Car decode(final boolean head, final ByteBuffer bb) {
		super.verifyHeader(head, bb);

		final Car car = new Car();
		car.amountOfWheels = bb.getInt();
		car.capacity = bb.getLong();
		car.full = (boolean) this.cm.decode(bb);
		car.name = (String) this.cm.decode(bb);

		return car;
	}

}
