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
import lu.kbra.pclib.db.autobuild.column.type.mysql.BooleanType;
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

public class MySQLColumnTypeRegistry implements ColumnTypeRegistry {

	@Override
	public void registerClassTypes(final Map<Predicate<Class<?>>, Function<Column, ColumnType>> classTypeMap) {
		classTypeMap.put(Class::isEnum, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());
	}

	@Override
	public void registerTypes(final Map<Class<?>, Function<Column, ColumnType>> typeMap) {
		// Java types
		typeMap.put(String.class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());
		typeMap.put(CharSequence.class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());
		typeMap.put(char[].class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());

		typeMap.put(byte[].class, col -> col.length() != -1 ? new VarbinaryType(col.length()) : new BlobType());
		typeMap.put(ByteBuffer.class, col -> col.length() != -1 ? new VarbinaryType(col.length()) : new BlobType());

		typeMap.put(Byte.class, col -> new TinyIntType());
		typeMap.put(byte.class, col -> new TinyIntType());
		typeMap.put(Short.class, col -> new SmallIntType());
		typeMap.put(short.class, col -> new SmallIntType());
		typeMap.put(Integer.class, col -> new IntType());
		typeMap.put(int.class, col -> new IntType());
		typeMap.put(Long.class, col -> new BigIntType());
		typeMap.put(long.class, col -> new BigIntType());
		typeMap.put(BigInteger.class, col -> new BigIntType());

		typeMap.put(Double.class, col -> new DoubleType());
		typeMap.put(double.class, col -> new DoubleType());
		typeMap.put(Float.class, col -> new FloatType());
		typeMap.put(float.class, col -> new FloatType());

		typeMap.put(Boolean.class, col -> new BooleanType());
		typeMap.put(boolean.class, col -> new BooleanType());

		typeMap.put(Timestamp.class, col -> new TimestampType());
		typeMap.put(LocalDateTime.class, col -> new TimestampType());
		typeMap.put(Date.class, col -> new DateType());
		typeMap.put(LocalDate.class, col -> new DateType());

		typeMap.put(JSONObject.class, col -> new JsonType());
		typeMap.put(JSONArray.class, col -> new JsonType());

		// Native MySQL types
		typeMap.put(TextType.class, col -> new TextType());
		typeMap.put(CharType.class, col -> new CharType(col.length()));
		typeMap.put(VarcharType.class, col -> new VarcharType(col.length()));

		typeMap.put(BinaryType.class, col -> new BinaryType(col.length()));
		typeMap.put(VarbinaryType.class, col -> new VarbinaryType(col.length()));
		typeMap.put(BlobType.class, col -> new BlobType());

		typeMap.put(BitType.class, col -> new BitType());
		typeMap.put(TinyIntType.class, col -> new TinyIntType());
		typeMap.put(SmallIntType.class, col -> new SmallIntType());
		typeMap.put(IntType.class, col -> new IntType());
		typeMap.put(BigIntType.class, col -> new BigIntType());

		typeMap.put(DoubleType.class, col -> new DoubleType());
		typeMap.put(FloatType.class, col -> new FloatType());
		typeMap.put(DecimalType.class, col -> new DecimalType(col.length(), col.params()));

		typeMap.put(TimestampType.class, col -> new TimestampType());
		typeMap.put(DateType.class, col -> new DateType());

		typeMap.put(JsonType.class, col -> new JsonType());
	}

}
