package examples.car;

public class Car {

	int amountOfWheels;
	long capacity;
	boolean full;
	String name;

	@Override
	public String toString() {
		return this.amountOfWheels + ", " + this.capacity + ", " + this.full + ", " + this.name;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}

		return this.toString().equals(obj.toString());
	}
}
