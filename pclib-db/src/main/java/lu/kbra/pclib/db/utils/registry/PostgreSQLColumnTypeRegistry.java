package lu.kbra.pclib.db.utils.registry;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BinaryTypes.BinaryType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BinaryTypes.BlobType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BinaryTypes.VarbinaryType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.DecimalType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.DoubleType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.FloatType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.BitType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.TinyIntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.CharType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes;

public class PostgreSQLColumnTypeRegistry implements ColumnTypeRegistry {

	@Override
	public void registerClassTypes(final Map<Predicate<Class<?>>, Function<Column, ColumnType>> classTypeMap) {
		classTypeMap.put(Class::isEnum,
				col -> col.length() != -1 ? new PostgreSQLTypes.VarcharType(col.length()) : new PostgreSQLTypes.TextType());
	}

	@Override
	public void registerTypes(final Map<Class<?>, Function<Column, ColumnType>> typeMap) {
		typeMap.put(String.class,
				col -> col.length() != -1 ? new PostgreSQLTypes.VarcharType(col.length()) : new PostgreSQLTypes.TextType());
		typeMap.put(CharSequence.class,
				col -> col.length() != -1 ? new PostgreSQLTypes.VarcharType(col.length()) : new PostgreSQLTypes.TextType());
		typeMap.put(char[].class,
				col -> col.length() != -1 ? new PostgreSQLTypes.VarcharType(col.length()) : new PostgreSQLTypes.TextType());
		typeMap.put(UUID.class, col -> new PostgreSQLTypes.UUIDType());

		typeMap.put(byte[].class, col -> new PostgreSQLTypes.ByteAType());
		typeMap.put(ByteBuffer.class, col -> new PostgreSQLTypes.ByteAType());

		typeMap.put(Byte.class, col -> new PostgreSQLTypes.SmallIntType());
		typeMap.put(byte.class, col -> new PostgreSQLTypes.SmallIntType());
		typeMap.put(Short.class, col -> new PostgreSQLTypes.SmallIntType());
		typeMap.put(short.class, col -> new PostgreSQLTypes.SmallIntType());
		typeMap.put(Integer.class, col -> new PostgreSQLTypes.IntegerType());
		typeMap.put(int.class, col -> new PostgreSQLTypes.IntegerType());
		typeMap.put(Long.class, col -> new PostgreSQLTypes.BigIntType());
		typeMap.put(long.class, col -> new PostgreSQLTypes.BigIntType());
		typeMap.put(BigInteger.class, col -> new PostgreSQLTypes.BigIntType());

		typeMap.put(Double.class, col -> new PostgreSQLTypes.DoublePrecisionType());
		typeMap.put(double.class, col -> new PostgreSQLTypes.DoublePrecisionType());
		typeMap.put(Float.class, col -> new PostgreSQLTypes.RealType());
		typeMap.put(float.class, col -> new PostgreSQLTypes.RealType());

		typeMap.put(Boolean.class, col -> new PostgreSQLTypes.BooleanType());
		typeMap.put(boolean.class, col -> new PostgreSQLTypes.BooleanType());

		typeMap.put(Timestamp.class, col -> new PostgreSQLTypes.TimestampType());
		typeMap.put(LocalDateTime.class, col -> new PostgreSQLTypes.TimestampType());
		typeMap.put(Date.class, col -> new PostgreSQLTypes.DateType());
		typeMap.put(LocalDate.class, col -> new PostgreSQLTypes.DateType());

		typeMap.put(JSONObject.class, col -> new PostgreSQLTypes.JsonType());
		typeMap.put(JSONArray.class, col -> new PostgreSQLTypes.JsonType());

		// Native PostgreSQL types.
		typeMap.put(PostgreSQLTypes.TextType.class, col -> new PostgreSQLTypes.TextType());
		typeMap.put(PostgreSQLTypes.VarcharType.class, col -> new PostgreSQLTypes.VarcharType(col.length()));
		typeMap.put(PostgreSQLTypes.UUIDType.class, col -> new PostgreSQLTypes.UUIDType());
		typeMap.put(PostgreSQLTypes.ByteAType.class, col -> new PostgreSQLTypes.ByteAType());
		typeMap.put(PostgreSQLTypes.SmallIntType.class, col -> new PostgreSQLTypes.SmallIntType());
		typeMap.put(PostgreSQLTypes.IntegerType.class, col -> new PostgreSQLTypes.IntegerType());
		typeMap.put(PostgreSQLTypes.BigIntType.class, col -> new PostgreSQLTypes.BigIntType());
		typeMap.put(PostgreSQLTypes.RealType.class, col -> new PostgreSQLTypes.RealType());
		typeMap.put(PostgreSQLTypes.DoublePrecisionType.class, col -> new PostgreSQLTypes.DoublePrecisionType());
		typeMap.put(PostgreSQLTypes.NumericType.class, col -> new PostgreSQLTypes.NumericType());
		typeMap.put(PostgreSQLTypes.BooleanType.class, col -> new PostgreSQLTypes.BooleanType());
		typeMap.put(PostgreSQLTypes.DateType.class, col -> new PostgreSQLTypes.DateType());
		typeMap.put(PostgreSQLTypes.TimestampType.class, col -> new PostgreSQLTypes.TimestampType());
		typeMap.put(PostgreSQLTypes.JsonType.class, col -> new PostgreSQLTypes.JsonType());

		// Existing MySQL native annotations are mapped to PostgreSQL equivalents.
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.TextType.class, col -> new PostgreSQLTypes.TextType());
		typeMap.put(CharType.class, col -> new PostgreSQLTypes.VarcharType(col.length()));
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.VarcharType.class,
				col -> new PostgreSQLTypes.VarcharType(col.length()));
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.UUIDType.class, col -> new PostgreSQLTypes.UUIDType());
		typeMap.put(BinaryType.class, col -> new PostgreSQLTypes.ByteAType());
		typeMap.put(VarbinaryType.class, col -> new PostgreSQLTypes.ByteAType());
		typeMap.put(BlobType.class, col -> new PostgreSQLTypes.ByteAType());
		typeMap.put(BitType.class, col -> new PostgreSQLTypes.BooleanType());
		typeMap.put(TinyIntType.class, col -> new PostgreSQLTypes.SmallIntType());
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.SmallIntType.class, col -> new PostgreSQLTypes.SmallIntType());
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.IntType.class, col -> new PostgreSQLTypes.IntegerType());
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.BigIntType.class, col -> new PostgreSQLTypes.BigIntType());
		typeMap.put(DoubleType.class, col -> new PostgreSQLTypes.DoublePrecisionType());
		typeMap.put(FloatType.class, col -> new PostgreSQLTypes.RealType());
		typeMap.put(DecimalType.class, col -> new PostgreSQLTypes.NumericType());
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.TimestampType.class, col -> new PostgreSQLTypes.TimestampType());
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.DateType.class, col -> new PostgreSQLTypes.DateType());
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.JsonType.class, col -> new PostgreSQLTypes.JsonType());
	}

}
