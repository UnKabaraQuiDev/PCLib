package examples.car;

import java.util.Objects;

public class Car {

	int amountOfWheels;
	long capacity;
	boolean full;
	String name;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final Car other = (Car) obj;
		return this.amountOfWheels == other.amountOfWheels && this.capacity == other.capacity && this.full == other.full
				&& Objects.equals(this.name, other.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(Integer.valueOf(this.amountOfWheels), Long.valueOf(this.capacity), Boolean.valueOf(this.full), this.name);
	}

	@Override
	public String toString() {
		return this.amountOfWheels + ", " + this.capacity + ", " + this.full + ", " + this.name;
	}

}
