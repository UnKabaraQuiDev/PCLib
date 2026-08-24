package lu.kbra.pclib.db.autobuild.postgres.encoding.array;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface FixedArrayEncodingType<Tjdbc> extends ArrayEncodingType<Tjdbc> {

	int[] getDimensions();

	@Override
	default int getDimensionCount() {
		return getDimensions().length;
	}

	@Override
	default String getTypeName() {
		return this.getRawTypeName() + Arrays.stream(getDimensions()).mapToObj(i -> "[" + i + "]").collect(Collectors.joining());
	}

}
