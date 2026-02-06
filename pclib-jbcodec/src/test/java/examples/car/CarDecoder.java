package examples.car;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.decoder.DefaultObjectDecoder;

public class CarDecoder extends DefaultObjectDecoder<Car> {

	public CarDecoder() {
		super(Car.class);
	}

	@Override
	public Car decode(boolean head, ByteBuffer bb) {
		super.verifyHeader(head, bb);

		Car car = new Car();
		car.amountOfWheels = bb.getInt();
		car.capacity = bb.getLong();
		car.full = (boolean) cm.decode(bb);
		car.name = (String) cm.decode(bb);

		return car;
	}

}