package lu.pcy113.pclib;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.json.JSONObject;

import com.mysql.cj.jdbc.ClientPreparedStatement;

import lu.pcy113.pclib.datastructure.pair.Pair;
import lu.pcy113.pclib.datastructure.pair.Pairs;
import lu.pcy113.pclib.impl.DependsOn;
import lu.pcy113.pclib.impl.ThrowingFunction;
import lu.pcy113.pclib.impl.ThrowingSupplier;

public final class PCUtils {

	public static Throwable getCause(final Throwable e) {
		Throwable cause = null;
		Throwable result = e;

		while (null != (cause = result.getCause()) && (result != cause)) {
			result = cause;
		}
		return result;
	}

	public static String getRootCauseMessage(final Throwable th) {
		return getCause(th).getMessage();
	}

	public static String getStackTraceAsString(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		return sw.toString();
	}

	public static <T> T[] castArray(final Object[] arr, final Function<Object, T> transformer,
			final IntFunction<T[]> supplier) {
		return Arrays.stream(arr).map(transformer).toArray(supplier);
	}

	public static <T> T defaultIfNull(final T obj, final Supplier<T> orElse) {
		return obj == null ? orElse.get() : obj;
	}

	public static <T> T defaultIfNull(final T obj, final ThrowingSupplier<T, Throwable> orElse) throws Throwable {
		return obj == null ? orElse.get() : obj;
	}

	public static <T> T defaultIfNull(final T obj, final T orElse) {
		return obj == null ? orElse : obj;
	}

	public static boolean isInteger(String str) {
		Objects.requireNonNull(str);

		str = str.trim();
		return !str.isEmpty() && str.matches("[0-9]+");
	}

	public static byte randomShortRange(final byte min, final byte max) {
		return (byte) ((Math.random() * (max - min)) + min);
	}

	public static short randomShortRange(final short min, final short max) {
		return (short) ((Math.random() * (max - min)) + min);
	}

	public static char randomCharRange(final char min, final char max) {
		return (char) ((Math.random() * (max - min)) + min);
	}

	public static int randomIntRange(final int min, final int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

	public static long randomLongRange(final long min, final long max) {
		return (long) ((Math.random() * (max - min)) + min);
	}

	public static double randomDoubleRange(final double min, final double max) {
		return Math.random() * (max - min) + min;
	}

	public static float randomFloatRange(final float min, final float max) {
		return (float) (Math.random() * (max - min) + min);
	}

	public static int parseInteger(final String value, final int else_) {
		try {
			return Integer.parseInt(value);
		} catch (final NumberFormatException e) {
			return else_;
		}
	}

	public static boolean parseBoolean(final String value, final boolean else_) {
		if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
			return Boolean.parseBoolean(value);
		} else {
			return else_;
		}
	}

	public static final int SHA_256_CHAR_LENGTH = 64;

	public static String hashString(final String input, final String algorithm) {
		Objects.requireNonNull(input);

		try {
			final MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			final byte[] hashBytes = messageDigest.digest(input.getBytes());
			return bytesArrayToHexString(hashBytes);
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException("Hashing algorithm not found", e);
		}
	}

	public static String hashStringSha256(final String input) {
		return hashString(input, "SHA-256");
	}

	private static String bytesArrayToHexString(final byte[] bytes) {
		final StringBuilder hexString = new StringBuilder();
		for (final byte b : bytes) {
			final String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static boolean compare(final int x, final int target, final int delta) {
		return Math.abs(target - x) < delta;
	}

	public static boolean compare(final double x, final double target, final double delta) {
		return Math.abs(target - x) < delta;
	}

	public static boolean compare(final byte x, final byte target, final byte delta) {
		return Math.abs(target - x) < delta;
	}

	public static boolean compare(final float x, final float target, final float delta) {
		return Math.abs(target - x) < delta;
	}

	public static boolean compare(final char x, final char target, final char delta) {
		return Math.abs(target - x) < delta;
	}

	public static boolean compare(final short x, final short target, final short delta) {
		return Math.abs(target - x) < delta;
	}

	public static <T> T[] setArray(final T[] arr, final int index, final Function<Integer, T> sup) {
		arr[index] = sup.apply(index);
		return arr;
	}

	public static <T> T[] setArray(final T[] arr, final int index, final T val) {
		arr[index] = val;
		return arr;
	}

	public static <T> T[] shuffle(final T[] arr) {
		return shuffle(arr, 1);
	}

	public static <T> T[] shuffle(final T[] arr, final int fac) {
		for (int i = 0; i < arr.length * fac; i++) {
			swap(arr, i % arr.length, (int) (Math.random() * arr.length));
		}

		return arr;
	}

	public static <T> T[] swap(final T[] arr, final int i, final int j) {
		final T temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;

		return arr;
	}

	public static String capitalize(final String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public static final String getCallerClassName(final boolean parent) {
		return getCallerClassName(parent, false);
	}

	public static final String getCallerClassName(final boolean parent, final boolean simple) {
		final StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!PCUtils.class.getName().equals(ste.getClassName())) {
				if (!parent) {
					return (simple ? PCUtils.getFileExtension(ste.getClassName()) : ste.getClassName()) + "#"
							+ ste.getMethodName() + "@" + ste.getLineNumber();
				} else {
					ste = stElements[i + 1];
					return (simple ? PCUtils.getFileExtension(ste.getClassName()) : ste.getClassName()) + "#"
							+ ste.getMethodName() + "@" + ste.getLineNumber();
				}

			}
		}
		return null;
	}

	public static String getCallerClassName(final boolean parent, final boolean simple, final Class<?>... ignored) {
		final StackTraceElement[] stElements = Thread.currentThread().getStackTrace();

		final Set<String> ignoredClasses = Arrays.stream(ignored).map(Class::getName).collect(Collectors.toSet());

		for (int i = 1; i < stElements.length; i++) {
			final StackTraceElement ste = stElements[i];
			final String className = ste.getClassName();

			if (!PCUtils.class.getName().equals(className) && !ignoredClasses.contains(className)) {
				if (!parent) {
					return (simple ? PCUtils.getFileExtension(className) : className) + "#" + ste.getMethodName() + "@"
							+ ste.getLineNumber();
				} else if (i + 1 < stElements.length) {
					final StackTraceElement parentSte = stElements[i + 1];
					final String parentClassName = parentSte.getClassName();
					return (simple ? PCUtils.getFileExtension(parentClassName) : parentClassName) + "#"
							+ parentSte.getMethodName() + "@" + parentSte.getLineNumber();
				}
			}
		}
		return null;
	}

	public static String getCallerClassName(final boolean parent, final boolean simple,
			final String... ignorePatterns) {
		final StackTraceElement[] stElements = Thread.currentThread().getStackTrace();

		final List<Pattern> regexList = Arrays.stream(ignorePatterns).map(Pattern::compile)
				.collect(Collectors.toList());

		for (int i = 1; i < stElements.length; i++) {
			final StackTraceElement ste = stElements[i];
			final String className = ste.getClassName();

			if (!PCUtils.class.getName().equals(className)
					&& regexList.stream().noneMatch(p -> p.matcher(className).matches())) {
				if (!parent) {
					return (simple ? PCUtils.getFileExtension(className) : className) + "#" + ste.getMethodName() + "@"
							+ ste.getLineNumber();
				} else if (i + 1 < stElements.length) {
					final StackTraceElement parentSte = stElements[i + 1];
					final String parentClassName = parentSte.getClassName();

					return (simple ? PCUtils.getFileExtension(parentClassName) : parentClassName) + "#"
							+ parentSte.getMethodName() + "@" + parentSte.getLineNumber();
				}
			}
		}

		return null;
	}

	/**
	 * 0 -> first caller (before PCUtils)<br>
	 * 1 -> second caller<br>
	 * 3...
	 */
	public static final String getCallerClassName(final int offset, final boolean simple) {
		final StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!PCUtils.class.getName().equals(ste.getClassName())) {
				ste = stElements[i + offset];
				return (simple ? PCUtils.getFileExtension(ste.getClassName()) : ste.getClassName()) + "#"
						+ ste.getMethodName() + "@" + ste.getLineNumber();
			}
		}
		return null;
	}

	public static int byteToInt(final byte[] byteArray) {
		if (byteArray.length != 4) {
			throw new NumberFormatException("Array length should be 4.");
		}

		int result = 0;
		for (final byte element : byteArray) {
			result = (result << 8) | (element & 0xFF);
		}

		return result;
	}

	public static byte[] intToByteArray(final int val) {
		return new byte[] { (byte) ((val >> 24) & 0xFF), (byte) ((val >> 16) & 0xFF), (byte) ((val >> 8) & 0xFF),
				(byte) (val & 0xFF) };
	}

	public static byte[] remainingByteBufferToArray(final ByteBuffer bb) {
		final int length = bb.remaining();
		final byte[] cc = new byte[length];

		bb.get(cc);

		return cc;
	}

	public static final int[] intCountingUp(final int start, final int end, final int steps, final int count) {
		final int[] in = new int[end - start];
		for (int i = 0; i < in.length; i++)
			in[i] = start + steps * (i / count);
		return in;
	}

	public static final int[] intCountingUp(final int start, final int end) {
		final int[] in = new int[end - start];
		for (int i = 0; i < in.length; i++)
			in[i] = start + i;
		return in;
	}

	public static final int[] intCountingUpTriQuads(final int quadCount) {
		final int[] in = new int[quadCount * 6];
		for (int q = 0; q < quadCount; q++) {
			in[q * 6 + 0] = q * 4 + 0;
			in[q * 6 + 1] = q * 4 + 1;
			in[q * 6 + 2] = q * 4 + 2;
			in[q * 6 + 3] = q * 4 + 0;
			in[q * 6 + 4] = q * 4 + 2;
			in[q * 6 + 5] = q * 4 + 3;
		}
		return in;
	}

	public static float[] floatRepeating(final float[] is, final int count) {
		if (count <= 0) {
			throw new IllegalArgumentException("Size should be greater than 0.");
		}

		final int originalLength = is.length;
		final int repeatedLength = originalLength * count;
		final float[] result = new float[repeatedLength];

		for (int i = 0; i < count; i++) {
			System.arraycopy(is, 0, result, i * originalLength, originalLength);
		}

		return result;
	}

	public static final ArrayList<Byte> byteArrayToList(final byte[] b) {
		final ArrayList<Byte> al = new ArrayList<>(b.length);
		for (final byte element : b)
			al.add(element);
		return al;
	}

	public static final byte[] byteListToPrimitive(final List<Byte> bytes) {
		final byte[] b = new byte[bytes.size()];
		for (int i = 0; i < b.length; i++)
			b[i] = bytes.get(i);
		return b;
	}

	public static final String byteArrayToHexString(final byte[] byteArray) {
		final StringBuilder sb = new StringBuilder();
		for (final byte b : byteArray) {
			sb.append(String.format("%02X ", b));
		}
		return sb.toString();
	}

	/**
	 * Does not change the reader's position
	 */
	public static final String byteBufferToHexString(final ByteBuffer bb) {
		final int x = bb.position();
		final StringBuilder sb = new StringBuilder();
		while (bb.hasRemaining()) {
			sb.append(String.format("%02X ", bb.get()));
		}
		bb.position(x);
		return sb.toString();
	}

	public static String byteBufferToHexStringTable(final ByteBuffer bb, final int columnCount, final int columnWidth) {
		int currentColumn = 1;
		final int x = bb.position();

		final int indicatorLength = Integer.toString(bb.capacity()).length();

		final StringBuilder sb = new StringBuilder();

		sb.append(PCUtils.leftPadString(Integer.toString(bb.position()), " ", indicatorLength) + ": ");

		while (bb.hasRemaining()) {
			sb.append(String.format("%02X ", bb.get()));

			if (currentColumn == columnCount * columnWidth) {
				sb.append("\n");
				sb.append(PCUtils.leftPadString(Integer.toString(bb.position()), " ", indicatorLength) + ": ");
				currentColumn = 1;
			} else {
				if (currentColumn % columnWidth == 0) {
					sb.append(" ");
				}

				currentColumn++;
			}

		}

		sb.append(":" + PCUtils.leftPadString(Integer.toString(bb.position() - 1), " ", indicatorLength));

		bb.position(x);
		return sb.toString();
	}

	public static String byteBufferToHexString(final ByteBuffer bb, final int startingPos) {
		final int x = bb.position();
		bb.position(startingPos);
		final StringBuilder sb = new StringBuilder();
		while (bb.hasRemaining()) {
			sb.append(String.format("%02X ", bb.get()));
		}
		bb.position(x);
		return sb.toString();
	}

	public static final String repeatString(final String str, final int count) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++)
			sb.append(str);
		return sb.toString();
	}

	public static final String ipToString(final int ipv4) {
		return ipToString(intToByteArray(ipv4));
	}

	public static String ipToString(final byte[] ipv4) {
		return String.format("%d.%d.%d.%d", ipv4[0], ipv4[1], ipv4[2], ipv4[3]);
	}

	public static final int[] castInt(final Object[] arr) {
		return Arrays.stream((Object[]) arr).mapToInt(s -> (int) s).toArray();
	}

	public static final <T> Object[] castObject(final T[] arr) {
		return Arrays.stream(arr).map(s -> (Object) s).toArray();
	}

	public static final Object[] castObject(final long[] arr) {
		return Arrays.stream(arr).mapToObj(s -> (Object) s).toArray();
	}

	public static final Object[] castObject(final int[] arr) {
		return Arrays.stream(arr).mapToObj(s -> (Object) s).toArray();
	}

	public static final Object[] castObject(final short[] arr) {
		final Object[] narr = new Object[arr.length];
		for (int i = 0; i < arr.length; i++) {
			narr[i] = (Object) arr[i];
		}
		return narr;
	}

	public static final Object[] castObject(final char[] arr) {
		final Object[] narr = new Object[arr.length];
		for (int i = 0; i < arr.length; i++) {
			narr[i] = (Object) arr[i];
		}
		return narr;
	}

	public static final Object[] castObject(final byte[] arr) {
		final Object[] narr = new Object[arr.length];
		for (int i = 0; i < arr.length; i++) {
			narr[i] = (Object) arr[i];
		}
		return narr;
	}

	public static final Object[] castObject(final double[] arr) {
		final Object[] narr = new Object[arr.length];
		for (int i = 0; i < arr.length; i++) {
			narr[i] = (Object) arr[i];
		}
		return narr;
	}

	public static final Object[] castObject(final float[] arr) {
		final Object[] narr = new Object[arr.length];
		for (int i = 0; i < arr.length; i++) {
			narr[i] = (Object) arr[i];
		}
		return narr;
	}

	public static final int[] toIntArray(final byte[] arr) {
		final int[] out = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			out[i] = arr[i];
		}
		return out;
	}

	public static final Object[] toObjectArray(final int[] data) {
		return Arrays.stream(data).mapToObj(Integer::valueOf).toArray();
	}

	public static final byte[] toByteArray(final ByteBuffer cb) {
		if (cb.hasArray()) {
			return cb.array();
		} else {
			final int old = cb.position();
			cb.rewind();
			final byte[] c = new byte[cb.remaining()];
			cb.get(c);
			cb.position(old);
			return c;
		}
	}

	public static final double round(final double value, final int decimals) {
		final double places = Math.pow(10, decimals);
		return Math.round(value * places) / places;
	}

	public static final String roundFill(final double value, final int decimals) {
		return String.format("%." + decimals + "f", value);
	}

	public static final float applyMinThreshold(final float x, final float min) {
		return Math.abs(x) < min ? 0 : x;
	}

	public static final Color randomColor(final boolean alpha) {
		if (alpha)
			return new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255),
					(int) (Math.random() * 255));
		else
			return new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
	}

	public static final Color clampColor(final int red, final int green, final int blue) {
		return new Color(clamp(0, 255, red), clamp(0, 255, green), clamp(0, 255, blue));
	}

	public static final Color clampColor(final int red, final int green, final int blue, final int alpha) {
		return new Color(clamp(0, 255, red), clamp(0, 255, green), clamp(0, 255, blue), clamp(0, 255, alpha));
	}

	public static final byte clamp(final byte from, final byte to, final byte x) {
		return x < from ? from : (x > to ? to : x);
	}

	public static final short clamp(final short from, final short to, final short x) {
		return x < from ? from : (x > to ? to : x);
	}

	public static final char clamp(final char from, final char to, final char x) {
		return x < from ? from : (x > to ? to : x);
	}

	public static final int clamp(final int from, final int to, final int x) {
		return x < from ? from : (x > to ? to : x);
	}

	public static final float clamp(final float from, final float to, final float x) {
		return x < from ? from : (x > to ? to : x);
	}

	public static final double clamp(final double from, final double to, final double x) {
		return x < from ? from : (x > to ? to : x);
	}

	public static final byte clampRange(final byte from, final byte to, final byte x) {
		return clamp(min(from, to), max(from, to), x);
	}

	public static final short clampRange(final short from, final short to, final short x) {
		return clamp(min(from, to), max(from, to), x);
	}

	public static final char clampRange(final char from, final char to, final char x) {
		return clamp(min(from, to), max(from, to), x);
	}

	public static final int clampRange(final int from, final int to, final int x) {
		return clamp(Math.min(from, to), Math.max(from, to), x);
	}

	public static final float clampRange(final float from, final float to, final float x) {
		return clamp(Math.min(from, to), Math.max(from, to), x);
	}

	public static final double clampRange(final double from, final double to, final double x) {
		return clamp(Math.min(from, to), Math.max(from, to), x);
	}

	public static final byte min(final byte a, final byte b) {
		return a < b ? a : b;
	}

	public static final byte max(final byte a, final byte b) {
		return a > b ? a : b;
	}

	public static final short min(final short a, final short b) {
		return a < b ? a : b;
	}

	public static final short max(final short a, final short b) {
		return a > b ? a : b;
	}

	public static final char min(final char a, final char b) {
		return a < b ? a : b;
	}

	public static final char max(final char a, final char b) {
		return a > b ? a : b;
	}

	public static final String fillString(final String str, final String place, final int length) {
		return (str.length() < length ? repeatString(place, length - str.length()) + str : str);
	}

	public static final int[] randomIntArray(final int length, final int min, final int max) {
		final Random rand = new Random();
		final int[] arr = new int[length];
		for (int i = 0; i < length; i++) {
			arr[i] = rand.nextInt(max - min) + min;
		}
		return arr;
	}

	public static String getIncrement(final String filePath) {
		final String woExt = removeFileExtension(filePath);
		final String ext = getFileExtension(filePath);

		int index = 1;
		while (Files.exists(Paths.get(woExt + "-" + index + "." + ext))) {
			index++;
			if (index < 0) {
				throw new BufferOverflowException();
			}
		}

		return woExt + "-" + index + "." + ext;
	}

	public static String readStringFile(final String filePath) {
		String str;
		if (!Files.exists(Paths.get(filePath))) {
			throw new RuntimeException(new FileNotFoundException("File [" + filePath + "] does not exist"));
		}

		try {
			str = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (final Exception excp) {
			throw new RuntimeException("Error reading file [" + filePath + "]", excp);
		}

		return str;
	}

	public static byte[] readBytesFile(final String filePath) {
		if (!Files.exists(Paths.get(filePath))) {
			throw new RuntimeException(new FileNotFoundException("File [" + filePath + "] does not exist"));
		}

		try {
			return Files.readAllBytes(Paths.get(filePath));
		} catch (final Exception excp) {
			throw new RuntimeException("Error reading file [" + filePath + "]", excp);
		}
	}

	public static String readStringFile(final File file) {
		return readStringFile(file.getPath());
	}

	public static String listAll(final String path) throws IOException {
		String list = "";
		// list all the files in the 'path' directory and add them to the string 'list'
		final File directory = new File(path);
		final File[] files = directory.listFiles();
		if (files != null) {
			for (final File file : files) {
				if (file.isFile()) {
					list += file + "\n";
				} else {
					list += listAll(file.getCanonicalPath());
				}
			}
		}
		return list;
	}

	public static String recursiveTree(final String path) throws IOException {
		final String list = ".";
		return list + recursiveTree(1, path);
	}

	public static String recursiveTree(final int depth, final String path) throws IOException {
		final String prefix = repeatString("|  ", depth).trim() + "- ";

		String list = "";

		final File directory = new File(path);
		final File[] files = directory.listFiles();
		if (files != null) {
			for (final File file : files) {
				if (file.isFile()) {
					list += prefix + file.getName() + " (" + getHumanFormatFileSize(getFileSize(file)) + ")\n";
				} else {
					list += prefix + file.getName() + " (" + file.list().length + ")\n";
					list += recursiveTree(depth + 1, file.getPath());
				}
			}
		}
		return list;
	}

	public static final String[] HUMAN_FILE_SIZE_UNITS = new String[] { "B", "KB", "MB", "GB", "TB" };

	public static String getHumanFormatFileSize(final long fileSize) {
		if (fileSize <= 0)
			return "0 B";

		final int digitGroups = (int) (Math.log10(fileSize) / Math.log10(1024));

		return String.format("%.1f %s", fileSize / Math.pow(1024, digitGroups), HUMAN_FILE_SIZE_UNITS[digitGroups]);
	}

	public static long getFileSize(final File file) {
		return getFileSize(Paths.get(file.getPath()));
	}

	public static long getFileSize(final Path path) {
		try {
			final FileChannel imageFileChannel = FileChannel.open(path);

			final long imageFileSize = imageFileChannel.size();
			imageFileChannel.close();

			return imageFileSize;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String appendFileName(final String path, final String suffix) {
		return path.replaceAll("(.+)(\\.[^.]+)$", "$1" + suffix + "$2");
	}

	public static String replaceFileExtension(final String path, final String ext) {
		return path.replaceAll("(.+)(\\.[^.]+)$", "$1." + ext);
	}

	public static String removeFileExtension(final String path) {
		return path.replaceAll("(.+)(\\.[^.]+)$", "$1");
	}

	public static String getFileName(final String path) {
		return Paths.get(path).getFileName().toString().replaceAll("(.+)(\\.[^.]+)$", "$1");
	}

	public static String getFileExtension(final String path) {
		if (path.contains(".")) {
			return path.replaceAll("(.+\\.)([^.]+)$", "$2");
		} else {
			return path;
		}
	}

	private static final Collector<?, ?, ?> SHUFFLER = Collectors
			.collectingAndThen(Collectors.toCollection(ArrayList::new), list -> {
				Collections.shuffle(list);
				return list;
			});

	@SuppressWarnings("unchecked")
	public static <T> Collector<T, ?, List<T>> toShuffledList() {
		return (Collector<T, ?, List<T>>) SHUFFLER;
	}

	public static double clampGreaterOrEquals(final double min, final double x) {
		return x <= min ? min : x;
	}

	public static float clampGreaterOrEquals(final float min, final float x) {
		return x <= min ? min : x;
	}

	public static int clampGreaterOrEquals(final int min, final int x) {
		return x <= min ? min : x;
	}

	public static long clampGreaterOrEquals(final long min, final long x) {
		return x <= min ? min : x;
	}

	public static String wrapLine(final String text, final int lineWidth) {
		if (text == null || lineWidth < 1) {
			throw new IllegalArgumentException("Invalid input");
		}

		final StringBuilder wrappedText = new StringBuilder();
		final String[] words = text.split(" ");
		int currentLineLength = 0;

		for (final String word : words) {
			if (currentLineLength + word.length() > lineWidth) {
				wrappedText.append("\n");
				currentLineLength = 0;
			} else if (currentLineLength > 0) {
				wrappedText.append(" ");
				currentLineLength++;
			}
			wrappedText.append(word);
			currentLineLength += word.length();
			if (word.contains("\n")) {
				currentLineLength = 0;
			}
		}

		return wrappedText.toString();
	}

	public static <T> List<T> limitSize(final List<T> lines, final int count, final boolean trailing) {
		if (lines.size() <= count) {
			return lines;
		}

		if (trailing) {
			int size = 0;
			while ((size = lines.size()) > count) {
				lines.remove(size - 1);
			}
		} else {
			while (lines.size() > count) {
				lines.remove(0);
			}
		}

		return lines;
	}

	public static <T> T try_(final ThrowingSupplier<T, Throwable> suplier, final Function<Throwable, T> except) {
		try {
			return suplier.get();
		} catch (final Throwable e) {
			return except.apply(e);
		}
	}

	@SafeVarargs
	public static <T> ArrayList<T> asArrayList(final T... data) {
		final ArrayList<T> arraylist = new ArrayList<>(data.length);
		Collections.addAll(arraylist, data);
		return arraylist;
	}

	public static Set<Class<?>> getTypesInPackage(final String packageName) {
		final InputStream stream = ClassLoader.getSystemClassLoader()
				.getResourceAsStream(packageName.replaceAll("[.]", "/"));
		final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		return reader.lines().filter(line -> line.endsWith(".class")).map(line -> getClass(line, packageName))
				.collect(Collectors.toSet());
	}

	private static Class<?> getClass(final String className, final String packageName) {
		try {
			return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static int[] byteBufferToIntArray(final ByteBuffer bData, final int length) {
		final int[] data = new int[length];
		for (int i = 0; i < length; i++) {
			data[i] = bData.getInt();
		}
		return data;
	}

	public static ByteBuffer intArrayToByteBuffer(final int[] data) {
		final ByteBuffer buffer = ByteBuffer.allocate(data.length * Integer.BYTES);
		for (final int element : data) {
			buffer.putInt(element);
		}
		return (ByteBuffer) buffer.flip();
	}

	public static int[] toPrimitiveInt(final Object data) {
		if (data instanceof int[]) {
			return (int[]) data;
		}
		return Arrays.stream((Object[]) data).map((final Object i) -> (int) (i == null ? 0 : i))
				.mapToInt(Integer::intValue).toArray();
	}

	public static byte[] toPrimitiveByte(final Object data) {
		if (data instanceof byte[]) {
			return (byte[]) data;
		}
		final Object[] arr = (Object[]) data;
		final byte[] y = new byte[arr.length];
		for (int i = 0; i < arr.length; i++)
			y[i] = Byte.valueOf((byte) arr[i]);
		return y;
	}

	public static float[] toPrimitiveFloat(final Object data) {
		if (data instanceof float[]) {
			return (float[]) data;
		}
		final Object[] arr = (Object[]) data;
		final float[] y = new float[arr.length];
		for (int i = 0; i < arr.length; i++)
			y[i] = Float.valueOf((float) arr[i]);
		return y;
	}

	public static String joinString(final String[] tokens, final int start, final int end) {
		return IntStream.range(start, end).mapToObj(i -> tokens[i]).collect(Collectors.joining());
	}

	public static List<String> recursiveList(final Path directory) throws IOException {
		try (Stream<Path> walk = Files.walk(directory)) {
			return walk.filter(Files::isRegularFile).map(path -> directory.relativize(path).toString())
					.collect(Collectors.toList());
		}
	}

	public static String toString(final InputStream inputStream) {
		Objects.requireNonNull(inputStream, "InputStream cannot be null.");
		try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			return br.lines().collect(Collectors.joining("\n"));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] toBytes(final InputStream inputStream) {
		Objects.requireNonNull(inputStream, "InputStream cannot be null.");

		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
			final byte[] data = new byte[8192];
			int bytesRead;
			while ((bytesRead = inputStream.read(data)) != -1) {
				buffer.write(data, 0, bytesRead);
			}
			return buffer.toByteArray();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Stream<String> toLineStream(final InputStream inputStream) {
		Objects.requireNonNull(inputStream, "InputStream cannot be null.");
		return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines();
	}

	public static String replaceLast(final String original, final String target, final String replacement) {
		// Escape the target string for regex special characters
		final String escapedTarget = target.replaceAll("([\\W])", "\\\\$1");
		// Replace the last occurrence using regex lookahead
		return original.replaceFirst("(?s)(.*)" + escapedTarget, "$1" + replacement);
	}

	public static <V> Iterable<V> toIterable(final Iterator<V> iterator) {
		return new Iterable<V>() {
			@Override
			public Iterator<V> iterator() {
				return iterator;
			}

			@Override
			public void forEach(final Consumer<? super V> action) {
				iterator.forEachRemaining(action);
			}
		};
	}

	/**
	 * Removes the rightmost characters from a string. "abcdef", 5 -> "abcde"
	 */
	public static String rightTrimToLength(final String str, final int length) {
		return str.length() > length ? str.substring(0, length) : str;
	}

	/**
	 * Removes the leftmost characters from a string.<br>
	 * "abcdef", 5 -> "bcdef"
	 */
	public static String leftTrimToLength(final String str, final int maxLength) {
		return str.length() <= maxLength ? str : str.substring(str.length() - maxLength);
	}

	public static String leftPadString(final String str, final String fill, final int length) {
		return (str.length() < length ? repeatString(fill, length - str.length()) + str : str);
	}

	public static String rightPadString(final String str, final String fill, final int length) {
		return (str.length() < length ? str + repeatString(fill, length - str.length()) : str);
	}

	/**
	 * "abcdef", " ", 5 -> "bcdef"<br>
	 * "abc", " ", 5 -> " abc"
	 */
	public static String leftPadStringLeftTrim(final String str, final String fill, final int length) {
		return (str.length() < length ? repeatString(fill, length - str.length()) + str
				: leftTrimToLength(str, length));
	}

	/**
	 * "abcdef", " ", 5 -> "abcde"<br>
	 * "abc", " ", 5 -> " abc"
	 */
	public static String leftPadStringRightTrim(final String str, final String fill, final int length) {
		return (str.length() < length ? repeatString(fill, length - str.length()) + str
				: rightTrimToLength(str, length));
	}

	public static ByteBuffer readFile(final File file) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}

		if (file.isDirectory()) {
			throw new IllegalArgumentException("File is a directory: " + file.getAbsolutePath());
		}

		if (file.length() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("File is too large: " + file.getAbsolutePath());
		}

		final ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
		final FileInputStream fis = new FileInputStream(file);
		final FileChannel fc = fis.getChannel();

		fc.read(buffer);

		fc.close();
		fis.close();

		buffer.flip();

		return buffer;
	}

	@DependsOn("lu.pcy113.jbcodec")
	/*
	 * public static Object decodeFile(CodecManager cm, File file) throws
	 * IOException { return cm.decode(readFile(file)); }
	 */

	public static String leftPadLine(final String str, final String fill) {
		return Arrays.stream(str.split("\n")).collect(Collectors.joining("\n" + fill, fill, ""));
	}

	@DependsOn("org.json.JSONObject")
	public static Object getSubKey(final String[] keys, final JSONObject obj) {
		// System.out.println(keys.length + "> " + String.join(".", keys));
		JSONObject currentObj = obj;
		Object value = null;

		for (int i = 0; i < keys.length; i++) {
			final String key = keys[i];

			// If it's the last key in the array, return the value
			if (i == keys.length - 1) {
				return currentObj.get(key);
			}

			// Get the next JSONObject if it exists, otherwise return null
			value = currentObj.get(key);
			if (value instanceof JSONObject) {
				currentObj = (JSONObject) value;
			} else {
				return null; // If the key doesn't lead to another JSONObject
			}
		}
		return value;
	}

	/**
	 * Extracts all keys from the given JSONObject, including nested keys, in the
	 * format of "key.subkey".
	 *
	 * @param jsonObject The JSONObject to extract keys from.
	 * @return A Set containing all keys in the desired format.
	 */
	@DependsOn("org.json.JSONObject")
	public static Set<String> extractKeys(final JSONObject jsonObject) {
		final Set<String> keys = new HashSet<>();
		extractKeys(jsonObject, "", keys);
		return keys;
	}

	/**
	 * Helper method to recursively extract keys from the JSONObject.
	 *
	 * @param jsonObject The current JSONObject being processed.
	 * @param parentKey  The parent key used to build the key string.
	 * @param keys       The set to accumulate keys.
	 */
	@DependsOn("org.json.JSONObject")
	private static void extractKeys(final JSONObject jsonObject, final String parentKey, final Set<String> keys) {
		final Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			final String key = iterator.next();
			final String fullKey = parentKey.isEmpty() ? key : parentKey + "." + key;

			// Add the full key to the set
			keys.add(fullKey);

			// If the value associated with the key is a JSONObject, recurse
			if (jsonObject.get(key) instanceof JSONObject) {
				extractKeys(jsonObject.getJSONObject(key), fullKey, keys);
			}
		}
	}

	public static boolean extractFile(final String inPath, final File outFile) throws IOException {
		final File dir = outFile.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (!outFile.exists()) {
			final byte[] bytes = PCUtils.readBytesSource(inPath);

			outFile.createNewFile();
			Files.write(Paths.get(outFile.getPath()), bytes);

			return true;
		}
		return false;
	}

	public static <K, V> Map<K, V> castMap(final Map<?, ?> map, final Supplier<Map<K, V>> supplier,
			final Class<K> keyClass, final Class<V> valueClass) {
		return map.entrySet().stream().collect(Collectors.toMap(e -> keyClass.cast(e.getKey()),
				e -> valueClass.cast(e.getValue()), (k1, k2) -> k1, supplier));
	}

	public static <T> T throw_(final Throwable e) throws Throwable {
		throw e;
	}

	public static <T> T throw_(final Exception e) throws Exception {
		throw e;
	}

	public static <T> T throwRuntime(final Throwable e) throws RuntimeException {
		throw new RuntimeException(e);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> HashMap<K, V> hashMap(final Object... objects) {
		final HashMap<K, V> map = new HashMap<>();

		for (int i = 0; i < objects.length; i += 2) {
			map.put((K) objects[i], (V) objects[i + 1]);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(final Object obj) {
		return (T) obj;
	}

	public static boolean validEmail(final String email) {
		return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
	}

	public static long map(final long x, final long in_min, final long in_max, final long out_min, final long out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static int map(final int x, final int in_min, final int in_max, final int out_min, final int out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static double map(final double x, final double in_min, final double in_max, final double out_min,
			final double out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static float map(final float x, final float in_min, final float in_max, final float out_min,
			final float out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	/*
	 * public static short map(short x, short in_min, short in_max, short out_min,
	 * short out_max) { return (x - in_min) * (out_max - out_min) / (in_max -
	 * in_min) + out_min; }
	 *
	 * public static byte map(byte x, byte in_min, byte in_max, byte out_min, byte
	 * out_max) { return (x - in_min) * (out_max - out_min) / (in_max - in_min) +
	 * out_min; }
	 */

	/**
	 * Compares two JSONObjects for a match.
	 */
	@DependsOn("org.json.JSONObject")
	public static boolean matches(final JSONObject obj1, final JSONObject solution) {
		// Check for null objects
		if (obj1 == null || solution == null) {
			return false;
		}

		for (final String key : solution.keySet()) {
			if (!obj1.has(key) || !obj1.get(key).equals(solution.get(key))) {
				return false;
			}
		}

		return true;
	}

	public static int snap(final int x, final int interval) {
		return ((x + interval - 1) / interval) * interval;
	}

	public static double snap(final double x, final double interval) {
		return ((x + interval - 1) / interval) * interval;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] fillArray(final T[] arr, final Object... objects) {
		for (int i = 0; i < objects.length; i++) {
			arr[i] = (T) objects[i];
		}
		return arr;
	}

	public static <T> T[] fillArray(final T[] arr, final Function<Integer, T> provider) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = provider.apply(i);
		}
		return arr;
	}

	public static <T> List<T> reversed(final List<T> list) {
		Collections.reverse(list);
		return list;
	}

	public static <T> List<T> shuffled(final List<T> list) {
		Collections.shuffle(list);
		return list;
	}

	public static long nanoTime(final Runnable run) {
		final long start = System.nanoTime();
		run.run();
		return System.nanoTime() - start;
	}

	public static long millisTime(final Runnable run) {
		final long start = System.currentTimeMillis();
		run.run();
		return System.currentTimeMillis() - start;
	}

	public static <T> Pair<T, Long> nanoTime(final Supplier<T> run) {
		final long start = System.nanoTime();
		final T output = run.get();
		return Pairs.readOnly(output, System.nanoTime() - start);
	}

	public static <T> Pair<T, Long> millisTime(final Supplier<T> run) {
		final long start = System.currentTimeMillis();
		final T output = run.get();
		return Pairs.readOnly(output, System.currentTimeMillis() - start);
	}

	public static int clampLessOrEquals(final int x, final int max) {
		return x > max ? max : x;
	}

	public static long clampLessOrEquals(final long x, final long max) {
		return x > max ? max : x;
	}

	public static double clampLessOrEquals(final double x, final double max) {
		return x > max ? max : x;
	}

	public static float clampLessOrEquals(final float x, final float max) {
		return x > max ? max : x;
	}

	public static <T> T[] add(final T[] beanPackages, final T name) {
		final T[] newArray = Arrays.copyOf(beanPackages, beanPackages.length + 1);
		newArray[beanPackages.length] = name;
		return newArray;
	}

	public static <T> List<T> add(final List<T> beanPackages, final T name) {
		beanPackages.add(name);
		return beanPackages;
	}

	public static <T> List<T> addAll(final List<T> beanPackages, final List<T> name) {
		beanPackages.addAll(name);
		return beanPackages;
	}

	public static String camelCaseToSnakeCase(final String input) {
		if (input == null || input.isEmpty())
			return input;

		final StringBuilder result = new StringBuilder();
		final char[] chars = input.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			final char current = chars[i];

			if (Character.isUpperCase(current)) {
				// Insert underscore if:
				// - The previous char is lowercase -> start of a new word
				// - the next char is lowercase -> end of a acronym
				if (i > 0 && (Character.isLowerCase(chars[i - 1])
						|| (i + 1 < chars.length && Character.isLowerCase(chars[i + 1])))) {
					result.append('_');
				}
				result.append(Character.toLowerCase(current));
			} else {
				result.append(current);
			}
		}

		return result.toString();
	}

	public static String sqlEscapeIdentifier(final String identifier) {
		if (identifier.equals("*") || identifier.endsWith(".*")) {
			return identifier;
		}
		final String[] parts = identifier.split("\\.");
		for (int i = 0; i < parts.length; i++) {
			if (!parts[i].equals("*") && !parts[i].startsWith("`") && !parts[i].endsWith("`")) {
				parts[i] = "`" + parts[i].replace("`", "``") + "`";
			}
		}
		return String.join(".", parts);
	}

	public static boolean duplicates(final String[] refCols) {
		return Arrays.stream(refCols).distinct().count() < refCols.length;
	}

	public static Map<String, Object> asMap(final ResultSet rs) throws SQLException {
		final ResultSetMetaData metaData = rs.getMetaData();
		final int columnCount = metaData.getColumnCount();
		final Map<String, Object> map = new HashMap<>();
		for (int i = 1; i <= columnCount; i++) {
			final String colName = rs.getMetaData().getColumnName(i);
			map.put(colName, rs.getObject(i));
		}
		return map;
	}

	public static boolean hasColumn(final ResultSet rs, final String columnName) throws SQLException {
		final ResultSetMetaData metaData = rs.getMetaData();
		final int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			if (columnName.equalsIgnoreCase(metaData.getColumnLabel(i))) {
				return true;
			}
		}
		return false;
	}

	public static Map<String, Integer> getColumnMapping(final ResultSet rs) throws SQLException {
		final int count = rs.getMetaData().getColumnCount();
		final Map<String, Integer> map = new HashMap<>();
		for (int i = 1; i <= count; i++) {
			final String colName = rs.getMetaData().getColumnName(i);
			map.put(colName, i);
		}
		return map;
	}

	public static int getColumnIndex(final ResultSet rs, final String columnName) throws SQLException {
		final int count = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= count; i++) {
			final String colName = rs.getMetaData().getColumnLabel(i);
			if (colName.equals(columnName)) {
				return i;
			}
		}
		throw new IllegalArgumentException("No column found for: " + columnName);
	}

	public static <T, R> ThrowingFunction<List<T>, R, Throwable> first(final Function<T, R> transformer) {
		return (final List<T> list) -> {
			if (list.isEmpty()) {
				throw new NoSuchElementException();
			} else {
				return transformer.apply(list.get(0));
			}
		};
	}

	public static <T, R> ThrowingFunction<List<T>, R, Throwable> first(final Function<T, R> transformer,
			final Supplier<R> default_) {
		return (final List<T> list) -> {
			if (list.isEmpty()) {
				return default_.get();
			} else {
				return transformer.apply(list.get(0));
			}
		};
	}

	public static <T, R> ThrowingFunction<List<T>, R, Throwable> first(final Function<T, R> transformer,
			final R default_) {
		return (final List<T> list) -> {
			if (list.isEmpty()) {
				return default_;
			} else {
				return transformer.apply(list.get(0));
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] insert(final T[] array, final int index, final T element) {
		if (index < 0 || index > array.length) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
		}

		final T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length + 1);

		System.arraycopy(array, 0, result, 0, index);
		result[index] = element;
		System.arraycopy(array, index, result, index + 1, array.length - index);

		return result;
	}

	public static void notImplemented() {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public static Timestamp toTimestamp(final Date value) {
		return new Timestamp(value.getTime());
	}

	public static Date toDate(final Timestamp value) {
		return new Date(value.getTime());
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] combineArrays(final T[] first, final T[] second) {
		final T[] result = (T[]) Array.newInstance(first.getClass().getComponentType(), first.length + second.length);

		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);

		return result;
	}

	public static <T> T newInstance(final Class<T> clazz) {
		Objects.requireNonNull(clazz);

		try {
			return clazz.newInstance();
		} catch (final Exception e) {
			throw new RuntimeException("Error while creating new instance of type [" + clazz.getName() + "]", e);
		}
	}

	public static Class<?> getRawClass(final Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) type).getRawType();
		} else if (type instanceof GenericArrayType) {
			final Type componentType = ((GenericArrayType) type).getGenericComponentType();
			final Class<?> rawComponent = getRawClass(componentType);
			return Array.newInstance(rawComponent, 0).getClass();
		}

		throw new IllegalArgumentException("Unsupported Type: " + type);
	}

	public static String constantToCamelCase(final String enumName) {
		final StringBuilder result = new StringBuilder();
		final String[] parts = enumName.toLowerCase().split("_");

		for (int i = 0; i < parts.length; i++) {
			if (i == 0) {
				result.append(parts[i]);
			} else {
				result.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
			}
		}

		return result.toString();
	}

	public static String camelCaseToConstant(final String camelCase) {
		final StringBuilder result = new StringBuilder();

		for (final char c : camelCase.toCharArray()) {
			if (Character.isUpperCase(c)) {
				result.append('_');
			}
			result.append(Character.toUpperCase(c));
		}

		return result.toString();
	}

	public static Field[] getAllFields(final Class<?> clazz) {
		final List<Field> fields = new ArrayList<>();
		Class<?> current = clazz;
		while (current != null && current != Object.class) {
			Collections.addAll(fields, current.getDeclaredFields());
			current = current.getSuperclass();
		}
		return fields.toArray(new Field[0]);
	}

	public static String getStatementAsSQL(final PreparedStatement pstmt) {
		return (pstmt instanceof ClientPreparedStatement
				? ((com.mysql.cj.PreparedQuery) ((ClientPreparedStatement) pstmt).getQuery()).asSql()
				: pstmt.toString());
	}

	public static String enumToNameString(final Enum<?> c) {
		return PCUtils.capitalize(c.name().replace('_', ' ').toLowerCase());
	}

	public static String nameStringToEnum(final String c) {
		return c.toUpperCase().replace(' ', '_');
	}

	public static <T extends Enum<T>> T enumValuetoEnum(final Class<T> enumClass, final String e) {
		try {
			final T val = Enum.valueOf(enumClass, e);
			return val;
		} catch (final IllegalArgumentException es) {
			return null;
		}
	}

	public static Color oppositeColor(final Color color) {
		if (color == null)
			return null;

		final int r = 255 - color.getRed();
		final int g = 255 - color.getGreen();
		final int b = 255 - color.getBlue();

		return new Color(r, g, b, color.getAlpha());
	}

	public static double luminance(final Color c) {
		final double[] rgb = { c.getRed(), c.getGreen(), c.getBlue() };
		for (int i = 0; i < 3; i++) {
			final double v = rgb[i] / 255.0;
			rgb[i] = (v <= 0.03928) ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
		}
		return 0.2126 * rgb[0] + 0.7152 * rgb[1] + 0.0722 * rgb[2];
	}

	public static double contrast(final Color c1, final Color c2) {
		final double l1 = luminance(c1);
		final double l2 = luminance(c2);
		final double brightest = Math.max(l1, l2);
		final double darkest = Math.min(l1, l2);
		return (brightest + 0.05) / (darkest + 0.05);
	}

	public static Color maxContrast(final Color background) {
		final Color black = Color.BLACK;
		final Color white = Color.WHITE;
		return contrast(background, black) >= contrast(background, white) ? black : white;
	}

	public static Color maxContrast(final Color background, final Color... choices) {
		return maxContrast(background, Arrays.stream(choices));
	}

	public static Color maxContrast(final Color background, final List<Color> choices) {
		return maxContrast(background, choices.stream());
	}

	public static Color maxContrast(final Color background, final Stream<Color> stream) {
		return stream.sorted((c1, c2) -> (int) Math.signum(contrast(background, c2) - contrast(background, c1)))
				.findFirst().orElseGet(() -> maxContrast(background));
	}

	public static void close(final Closeable... result) throws IOException {
		if (result == null)
			return;
		for (final Closeable r : result)
			if (r != null)
				r.close();
	}

	public static void close(final AutoCloseable... result) throws Exception {
		if (result == null)
			return;
		for (final AutoCloseable r : result)
			if (r != null)
				r.close();
	}

	public static boolean isToday(final java.sql.Date date) {
		return isToday(date.toLocalDate());
	}

	public static boolean isToday(final LocalDate localDate) {
		Objects.requireNonNull(localDate);

		final LocalDate today = LocalDate.now();
		return today.equals(localDate);
	}

	public static String toSimpleIdentityString(final Object value) {
		Objects.requireNonNull(value);
		return value.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(value));
	}

	public static String readPackagedStringFile(final String path) {
		try {
			final InputStream in = PCUtils.class.getResourceAsStream(path);
			return toString(in);
		} catch (final Exception e) {
			throw new RuntimeException("Error while reading packaged file `" + path + "`", e);
		}
	}

	public static byte[] readPackagedBytesFile(final String path) {
		try {
			final InputStream in = PCUtils.class.getResourceAsStream(path);
			return toBytes(in);
		} catch (final Exception e) {
			throw new RuntimeException("Error while reading packaged file `" + path + "`", e);
		}
	}

	public static String readStringSource(final String location) {
		if (location.startsWith("classpath:")) {
			final String path = location.substring("classpath:".length());
			return readPackagedStringFile(path);
		} else if (location.startsWith("resource:")) {
			return readStringSource("classpath:" + location.substring("resource:".length()));
		} else if (location.startsWith("file:")) {
			final String path = location.substring("file:".length());
			return PCUtils.readStringFile(path);
		} else {
			return PCUtils.readStringFile(location);
		}
	}

	public static <T> Constructor<T> findCompatibleConstructor(final Class<T> clazz, final Class<?>... argTypes)
			throws NoSuchMethodException {
		for (final Constructor<?> ctor : clazz.getConstructors()) {
			final Class<?>[] params = ctor.getParameterTypes();
			if (params.length != argTypes.length) {
				continue;
			}

			boolean compatible = true;
			for (int i = 0; i < params.length; i++) {
				if (!params[i].isAssignableFrom(argTypes[i])) {
					compatible = false;
					break;
				}
			}

			if (compatible) {
				return (Constructor<T>) ctor;
			}
		}

		throw new NoSuchMethodException(
				"No compatible constructor found in " + clazz.getName() + " for args: " + Arrays.toString(argTypes));
	}

	public static String progressBar(final long budgetNanos, final long usedNanos, final boolean showOverFlow) {
		if (budgetNanos <= 0) {
			return "[??????????]";
		}

		final double ratio = (double) usedNanos / budgetNanos;

		final int filled = (int) Math.floor(Math.min(ratio, 1.0) * 10);

		final StringBuilder sb = new StringBuilder(20);
		sb.append('[');
		for (int i = 0; i < filled; i++)
			sb.append('X');
		for (int i = filled; i < 10; i++)
			sb.append(' ');
		sb.append(']');

		if (ratio > 1.0) {
			final int overflow = (int) Math.floor((ratio - 1.0) * 10);
			for (int i = 0; i < overflow; i++)
				sb.append('x');
		}

		return sb.toString();
	}

	public static byte[] readBytesSource(final String location) {
		if (location.startsWith("classpath:")) {
			final String path = location.substring("classpath:".length());
			return readPackagedBytesFile(path);
		} else if (location.startsWith("resource:")) {
			return readBytesSource("classpath:" + location.substring("resource:".length()));
		} else if (location.startsWith("file:")) {
			final String path = location.substring("file:".length());
			return PCUtils.readBytesFile(path);
		} else {
			return PCUtils.readBytesFile(location);
		}
	}

	/**
	 * @param hex <b>RRGGBBAA</b>
	 */
	public static Color hexToColor(String hex) {
		if (hex == null || hex.isEmpty()) {
			throw new IllegalArgumentException("Hex color cannot be null or empty");
		}

		hex = hex.trim();
		if (hex.startsWith("#")) {
			hex = hex.substring(1);
		}

		if (hex.length() != 6 && hex.length() != 8) {
			throw new IllegalArgumentException("Hex color must be 6 or 8 characters long");
		}

		final int rgb = (int) Long.parseLong(hex, 16);

		if (hex.length() == 6) {
			return new Color(rgb);
		} else {
			final int red = (rgb >> 24) & 0xFF;
			final int green = (rgb >> 16) & 0xFF;
			final int blue = (rgb >> 8) & 0xFF;
			final int alpha = rgb & 0xFF;
			return new Color(red, green, blue, alpha);

		}
	}

	public static void throwUnsupported() {
		throw new UnsupportedOperationException();
	}

	public static void throwUnsupported(final String msg) {
		throw new UnsupportedOperationException(msg);
	}

	public static int countElements(final Iterator<?> e) {
		int count = 0;
		while (e.hasNext()) {
			count++;
			e.next();
		}
		return count;
	}

	public static int countElements(final ResultSet e) throws SQLException {
		int count = 0;
		while (e.next()) {
			count++;
		}
		return count;
	}

	@SafeVarargs
	public static <T> Stream<? extends T> concatStreams(final Stream<? extends T>... streams) {
		return Arrays.stream(streams).flatMap(c -> c);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] removeArray(final T[] args, final int i) {
		if (args == null || i < 0 || i >= args.length) {
			throw new IllegalArgumentException("Invalid index or null array");
		}

		final T[] result = (T[]) Array.newInstance(args.getClass().getComponentType(), args.length - 1);

		if (i > 0) {
			System.arraycopy(args, 0, result, 0, i);
		}
		if (i < args.length - 1) {
			System.arraycopy(args, i + 1, result, i, args.length - i - 1);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] removeArray(final T[] args, final BiPredicate<Integer, Object> isToRemove) {
		if (args == null || isToRemove == null) {
			throw new IllegalArgumentException("Arguments cannot be null");
		}

		final List<T> kept = new ArrayList<>(args.length);
		for (int i = 0; i < args.length; i++) {
			final T value = args[i];
			if (!isToRemove.test(i, value)) {
				kept.add(value);
			}
		}

		final T[] result = (T[]) Array.newInstance(args.getClass().getComponentType(), kept.size());
		return kept.toArray(result);
	}

	public static int deleteOldFiles(final File directory, final int keepCount) {
		if (!directory.isDirectory()) {
			return 0;
		}

		final File[] files = directory.listFiles();
		if (files == null || files.length <= keepCount) {
			return 0;
		}

		Arrays.sort(files, Comparator.comparing(File::lastModified));

		int deleteCount = 0;
		for (int i = 0; i < files.length - keepCount; i++) {
			final File file = files[i];
			if (!file.delete()) {
				deleteCount++;
			}
		}

		return deleteCount;
	}

	public static String insertChar(final String s, int pos, final char c) {
		if (pos < 0)
			pos = 0;
		if (pos > s.length())
			pos = s.length();

		return s.substring(0, pos) + c + s.substring(pos);
	}

	public static String backspace(final String s, int pos) {
		if (s.isEmpty() || (pos <= 0))
			return s; // nothing to delete
		if (pos > s.length())
			pos = s.length();

		return s.substring(0, pos - 1) + s.substring(pos);
	}

	public static String deleteChar(final String s, final int pos) {
		if (s.isEmpty() || pos < 0 || pos >= s.length())
			return s;

		return s.substring(0, pos) + s.substring(pos + 1);
	}

	public static String replace(final String s, final char target, final char replacement, final int maxCount,
			final boolean fromEnd) {
		if (s == null || maxCount <= 0)
			return s;

		final char[] chars = s.toCharArray();
		int replaced = 0;

		if (fromEnd) {
			for (int i = chars.length - 1; i >= 0; i--) {
				if (chars[i] == target) {
					chars[i] = replacement;
					replaced++;
					if (replaced == maxCount)
						break;
				}
			}
		} else {
			for (int i = 0; i < chars.length; i++) {
				if (chars[i] == target) {
					chars[i] = replacement;
					replaced++;
					if (replaced == maxCount)
						break;
				}
			}
		}

		return new String(chars);
	}

	public static long randomId(final String seed) {
		return System.identityHashCode(PCUtils.hashStringSha256(seed)) + System.nanoTime();
	}

	public static boolean validString(final String content) {
		return content != null && !content.isEmpty() && !content.matches("^\\s*$");
	}

	public static float[] fill(final float[] bs, final float id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static double[] fill(final double[] bs, final double id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static byte[] fill(final byte[] bs, final byte id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static short[] fill(final short[] bs, final short id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static char[] fill(final char[] bs, final char id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static int[] fill(final int[] bs, final int id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static long[] fill(final long[] bs, final long id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static List<Thread> getAllThreads() {
		final Thread[] ts = new Thread[Thread.activeCount()];
		Thread.enumerate(ts);
		return Arrays.asList(ts);
	}

	public static int[] combineArrays(final int[] array1, final int[] array2) {
		// https://stackoverflow.com/questions/4697255/combine-two-integer-arrays
		final int[] array1and2 = new int[array1.length + array2.length];
		System.arraycopy(array1, 0, array1and2, 0, array1.length);
		System.arraycopy(array2, 0, array1and2, array1.length, array2.length);
		return array1and2;
	}

	public static float[] pack(final float[]... arrays) {
		int total = 0;
		for (final float[] arr : arrays)
			total += arr.length;

		final float[] out = new float[total];
		int offset = 0;

		for (final float[] arr : arrays) {
			System.arraycopy(arr, 0, out, offset, arr.length);
			offset += arr.length;
		}
		return out;
	}

	public static int[] getMaxIndices(final float[] arr, final int n) {
		if (n <= 0)
			return new int[0];

		final PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[0]));

		for (int i = 0; i < arr.length; i++) {
			final float value = arr[i];

			if (pq.size() < n) {
				pq.offer(new int[] { Float.floatToRawIntBits(value), i });
			} else if (value > Float.intBitsToFloat(pq.peek()[0])) {
				pq.poll();
				pq.offer(new int[] { Float.floatToRawIntBits(value), i });
			}
		}

		final int[] result = new int[pq.size()];
		int idx = 0;
		for (final int[] entry : pq) {
			result[idx++] = entry[1];
		}
		return result;
	}

	public static float stdDev(final float[] values) {
		final int n = values.length;
		if (n == 0)
			return 0f;

		float mean = 0f;
		for (final float v : values)
			mean += v;
		mean /= n;

		float sumSq = 0f;
		for (final float v : values) {
			final float d = v - mean;
			sumSq += d * d;
		}

		return (float) Math.sqrt(sumSq / n);
	}

	public static float median(final float[] values) {
		final float[] copy = values.clone();
		Arrays.sort(copy);

		final int n = copy.length;
		return (n & 1) == 1 ? copy[n / 2] : (copy[n / 2 - 1] + copy[n / 2]) * 0.5f;
	}

	public static int compareVersion(final String a, final String b) {
		final String[] pa = a.split("\\.");
		final String[] pb = b.split("\\.");

		final int len = Math.max(pa.length, pb.length);

		for (int i = 0; i < len; i++) {
			final int va = i < pa.length ? parseInteger(pa[i], 0) : 0;
			final int vb = i < pb.length ? parseInteger(pb[i], 0) : 0;

			if (va != vb) {
				return Integer.compare(va, vb);
			}
		}

		return 0;
	}

	public static boolean getBoolean(String name, boolean default_) {
		return Boolean.parseBoolean(System.getProperty(name, Boolean.toString(default_)));
	}

}
