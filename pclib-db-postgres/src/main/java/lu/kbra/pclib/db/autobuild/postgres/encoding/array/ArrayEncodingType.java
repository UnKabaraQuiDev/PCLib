package lu.kbra.pclib.db.autobuild.postgres.encoding.array;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

public interface ArrayEncodingType<Tjdbc> extends FixedEncodingType<Tjdbc> {

	String getRawTypeName();

	default int dimensionCount() {
		return 1;
	}

	@Override
	default String getTypeName() {
		return this.getRawTypeName() + PCUtils.repeatString("[]", this.dimensionCount());
	}

}
