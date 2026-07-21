package lu.kbra.pclib.db.autobuild.mysql.decimal;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.mysql.encoding.decimal.DecimalEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

@Getter
@RequiredArgsConstructor
public class BigDecimalType implements IdentityColumnType<BigDecimal> {

	private final EncodingType<BigDecimal> encodingType;

	public BigDecimalType(final int precision, final int scale) {
		this.encodingType = new DecimalEncodingType(precision, scale);
	}

}