package lu.kbra.pclib.db.autobuild.postgres.column.decimal;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.postgres.encoding.decimal.NumericEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

@Getter
@RequiredArgsConstructor
public class BigDecimalColumnType implements IdentityColumnType<BigDecimal> {

	private final EncodingType<BigDecimal> encodingType;

	public BigDecimalColumnType(final int precision, final int scale) {
		this.encodingType = new NumericEncodingType(precision, scale);
	}

}
