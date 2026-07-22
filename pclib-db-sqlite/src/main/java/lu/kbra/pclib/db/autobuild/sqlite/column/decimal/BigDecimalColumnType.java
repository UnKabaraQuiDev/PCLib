package lu.kbra.pclib.db.autobuild.sqlite.column.decimal;

import java.math.BigDecimal;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.decimal.NumericEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BigDecimalColumnType implements IdentityColumnType<BigDecimal> {

	private final EncodingType<BigDecimal> encodingType;

	public BigDecimalColumnType(final int precision, final int scale) {
		this.encodingType = new NumericEncodingType(precision, scale);
	}

}
