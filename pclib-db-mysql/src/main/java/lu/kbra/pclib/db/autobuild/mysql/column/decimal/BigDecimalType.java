package lu.kbra.pclib.db.autobuild.mysql.column.decimal;

import java.math.BigDecimal;

import lu.kbra.pclib.db.autobuild.mysql.encoding.decimal.DecimalEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BigDecimalType implements IdentityColumnType<BigDecimal> {

	private final EncodingType<BigDecimal> encodingType;

	public BigDecimalType(final int precision, final int scale) {
		this.encodingType = new DecimalEncodingType(precision, scale);
	}

}
