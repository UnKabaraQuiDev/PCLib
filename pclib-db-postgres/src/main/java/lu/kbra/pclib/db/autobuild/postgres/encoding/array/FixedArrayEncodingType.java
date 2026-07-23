package lu.kbra.pclib.db.autobuild.postgres.encoding.array;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface FixedArrayEncodingType<Tjdbc> extends ArrayEncodingType<Tjdbc> {

	int[] dimensions();

	@Override
	default int dimensionCount() {
		return dimensions().length;
	}

	@Override
	default String getTypeName() {
		return this.getRawTypeName() + Arrays.stream(dimensions()).mapToObj(i -> "[" + i + "]").collect(Collectors.joining());
	}

}
