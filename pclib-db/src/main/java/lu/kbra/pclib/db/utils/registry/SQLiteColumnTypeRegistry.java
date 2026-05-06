package lu.kbra.pclib.db.utils.registry;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
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
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.BigIntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.BitType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.IntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.SmallIntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.TinyIntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.CharType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.JsonType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.TextType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.VarcharType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.DateType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.TimestampType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.BooleanType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.IntegerType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.RealType;

public class SQLiteColumnTypeRegistry implements ColumnTypeRegistry {

	@Override
	public void registerClassTypes(final Map<Predicate<Class<?>>, Function<Column, ColumnType>> classTypeMap) {
		classTypeMap.put(Class::isEnum, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.TextType());
	}

	@Override
	public void registerTypes(final Map<Class<?>, Function<Column, ColumnType>> typeMap) {
		// Java types use SQLite storage classes.
		typeMap.put(String.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.TextType());
		typeMap.put(CharSequence.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.TextType());
		typeMap.put(char[].class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.TextType());

		typeMap.put(byte[].class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.BlobType());
		typeMap.put(ByteBuffer.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.BlobType());

		typeMap.put(Byte.class, col -> new IntegerType());
		typeMap.put(byte.class, col -> new IntegerType());
		typeMap.put(Short.class, col -> new IntegerType());
		typeMap.put(short.class, col -> new IntegerType());
		typeMap.put(Integer.class, col -> new IntegerType());
		typeMap.put(int.class, col -> new IntegerType());
		typeMap.put(Long.class, col -> new IntegerType());
		typeMap.put(long.class, col -> new IntegerType());
		typeMap.put(BigInteger.class, col -> new IntegerType());

		typeMap.put(Double.class, col -> new RealType());
		typeMap.put(double.class, col -> new RealType());
		typeMap.put(Float.class, col -> new RealType());
		typeMap.put(float.class, col -> new RealType());

		typeMap.put(Boolean.class, col -> new BooleanType());
		typeMap.put(boolean.class, col -> new BooleanType());

		typeMap.put(Timestamp.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.TimestampType());
		typeMap.put(LocalDateTime.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.TimestampType());
		typeMap.put(Date.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.DateType());
		typeMap.put(LocalDate.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.DateType());

		typeMap.put(JSONObject.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.JsonType());
		typeMap.put(JSONArray.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.JsonType());

		// MySQL native type classes are mapped to equivalent SQLite storage classes when users annotate
		// with them.
		typeMap.put(TextType.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.TextType());
		typeMap.put(CharType.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.TextType());
		typeMap.put(VarcharType.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.TextType());

		typeMap.put(BinaryType.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.BlobType());
		typeMap.put(VarbinaryType.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.BlobType());
		typeMap.put(BlobType.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.BlobType());

		typeMap.put(BitType.class, col -> new IntegerType());
		typeMap.put(TinyIntType.class, col -> new IntegerType());
		typeMap.put(SmallIntType.class, col -> new IntegerType());
		typeMap.put(IntType.class, col -> new IntegerType());
		typeMap.put(BigIntType.class, col -> new IntegerType());

		typeMap.put(DoubleType.class, col -> new RealType());
		typeMap.put(FloatType.class, col -> new RealType());
		typeMap.put(DecimalType.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.NumericType());

		typeMap.put(TimestampType.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.TimestampType());
		typeMap.put(DateType.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.DateType());

		typeMap.put(JsonType.class, col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.JsonType());

		// Native SQLite type classes.
		typeMap.put(IntegerType.class, col -> new IntegerType());
		typeMap.put(RealType.class, col -> new RealType());
		typeMap.put(BooleanType.class, col -> new BooleanType());
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.sqlite.TextType.class,
				col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.TextType());
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.sqlite.BlobType.class,
				col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.BlobType());
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.sqlite.NumericType.class,
				col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.NumericType());
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.sqlite.DateType.class,
				col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.DateType());
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.sqlite.TimestampType.class,
				col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.TimestampType());
		typeMap.put(lu.kbra.pclib.db.autobuild.column.type.sqlite.JsonType.class,
				col -> new lu.kbra.pclib.db.autobuild.column.type.sqlite.JsonType());
	}

}
