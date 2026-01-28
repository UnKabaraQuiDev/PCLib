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

	public static Throwable getCause(Throwable e) {
		Throwable cause = null;
		Throwable result = e;

		while (null != (cause = result.getCause()) && (result != cause)) {
			result = cause;
		}
		return result;
	}

	public static String getRootCauseMessage(Throwable th) {
		return getCause(th).getMessage();
	}

	public static String getStackTraceAsString(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		return sw.toString();
	}

	public static <T> T[] castArray(Object[] arr, Function<Object, T> transformer, IntFunction<T[]> supplier) {
		return Arrays.stream(arr).map(transformer).toArray(supplier);
	}

	public static <T> T defaultIfNull(T obj, Supplier<T> orElse) {
		return obj == null ? orElse.get() : obj;
	}

	public static <T> T defaultIfNull(T obj, ThrowingSupplier<T, Throwable> orElse) throws Throwable {
		return obj == null ? orElse.get() : obj;
	}

	public static <T> T defaultIfNull(T obj, T orElse) {
		return obj == null ? orElse : obj;
	}

	public static boolean isInteger(String str) {
		Objects.requireNonNull(str);

		str = str.trim();
		return !str.isEmpty() && str.matches("[0-9]+");
	}

	public static byte randomShortRange(byte min, byte max) {
		return (byte) ((Math.random() * (max - min)) + min);
	}

	public static short randomShortRange(short min, short max) {
		return (short) ((Math.random() * (max - min)) + min);
	}

	public static char randomCharRange(char min, char max) {
		return (char) ((Math.random() * (max - min)) + min);
	}

	public static int randomIntRange(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

	public static long randomLongRange(long min, long max) {
		return (long) ((Math.random() * (max - min)) + min);
	}

	public static double randomDoubleRange(double min, double max) {
		return Math.random() * (max - min) + min;
	}

	public static float randomFloatRange(float min, float max) {
		return (float) (Math.random() * (max - min) + min);
	}

	public static int parseInteger(String value, int else_) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return else_;
		}
	}

	public static boolean parseBoolean(String value, boolean else_) {
		if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
			return Boolean.parseBoolean(value);
		} else {
			return else_;
		}
	}

	public static final int SHA_256_CHAR_LENGTH = 64;

	public static String hashString(String input, String algorithm) {
		Objects.requireNonNull(input);

		try {
			final MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			final byte[] hashBytes = messageDigest.digest(input.getBytes());
			return bytesArrayToHexString(hashBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Hashing algorithm not found", e);
		}
	}

	public static String hashStringSha256(String input) {
		return hashString(input, "SHA-256");
	}

	private static String bytesArrayToHexString(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static boolean compare(int x, int target, int delta) {
		return Math.abs(target - x) < delta;
	}

	public static boolean compare(double x, double target, double delta) {
		return Math.abs(target - x) < delta;
	}

	public static boolean compare(byte x, byte target, byte delta) {
		return Math.abs(target - x) < delta;
	}

	public static boolean compare(float x, float target, float delta) {
		return Math.abs(target - x) < delta;
	}

	public static boolean compare(char x, char target, char delta) {
		return Math.abs(target - x) < delta;
	}

	public static boolean compare(short x, short target, short delta) {
		return Math.abs(target - x) < delta;
	}

	public static <T> T[] setArray(T[] arr, int index, Function<Integer, T> sup) {
		arr[index] = sup.apply(index);
		return arr;
	}

	public static <T> T[] setArray(T[] arr, int index, T val) {
		arr[index] = val;
		return arr;
	}

	public static <T> T[] shuffle(T[] arr) {
		return shuffle(arr, 1);
	}

	public static <T> T[] shuffle(T[] arr, int fac) {
		for (int i = 0; i < arr.length * fac; i++) {
			swap(arr, i % arr.length, (int) (Math.random() * arr.length));
		}

		return arr;
	}

	public static <T> T[] swap(T[] arr, int i, int j) {
		T temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;

		return arr;
	}

	public static String capitalize(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public static final String getCallerClassName(boolean parent) {
		return getCallerClassName(parent, false);
	}

	public static final String getCallerClassName(boolean parent, boolean simple) {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
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

	public static String getCallerClassName(boolean parent, boolean simple, Class<?>... ignored) {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();

		Set<String> ignoredClasses = Arrays.stream(ignored).map(Class::getName).collect(Collectors.toSet());

		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			String className = ste.getClassName();

			if (!PCUtils.class.getName().equals(className) && !ignoredClasses.contains(className)) {
				if (!parent) {
					return (simple ? PCUtils.getFileExtension(className) : className) + "#" + ste.getMethodName() + "@"
							+ ste.getLineNumber();
				} else if (i + 1 < stElements.length) {
					StackTraceElement parentSte = stElements[i + 1];
					String parentClassName = parentSte.getClassName();
					return (simple ? PCUtils.getFileExtension(parentClassName) : parentClassName) + "#"
							+ parentSte.getMethodName() + "@" + parentSte.getLineNumber();
				}
			}
		}
		return null;
	}

	public static String getCallerClassName(boolean parent, boolean simple, String... ignorePatterns) {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();

		List<Pattern> regexList = Arrays.stream(ignorePatterns).map(Pattern::compile).collect(Collectors.toList());

		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			String className = ste.getClassName();

			if (!PCUtils.class.getName().equals(className)
					&& regexList.stream().noneMatch(p -> p.matcher(className).matches())) {
				if (!parent) {
					return (simple ? PCUtils.getFileExtension(className) : className) + "#" + ste.getMethodName() + "@"
							+ ste.getLineNumber();
				} else if (i + 1 < stElements.length) {
					StackTraceElement parentSte = stElements[i + 1];
					String parentClassName = parentSte.getClassName();

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
	public static final String getCallerClassName(int offset, boolean simple) {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
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

	public static int byteToInt(byte[] byteArray) {
		if (byteArray.length != 4) {
			throw new NumberFormatException("Array length should be 4.");
		}

		int result = 0;
		for (int i = 0; i < byteArray.length; i++) {
			result = (result << 8) | (byteArray[i] & 0xFF);
		}

		return result;
	}

	public static byte[] intToByteArray(int val) {
		return new byte[] { (byte) ((val >> 24) & 0xFF), (byte) ((val >> 16) & 0xFF), (byte) ((val >> 8) & 0xFF),
				(byte) (val & 0xFF) };
	}

	public static byte[] remainingByteBufferToArray(ByteBuffer bb) {
		int length = bb.remaining();
		byte[] cc = new byte[length];

		bb.get(cc);

		return cc;
	}

	public static final int[] intCountingUp(int start, int end, int steps, int count) {
		int[] in = new int[end - start];
		for (int i = 0; i < in.length; i++)
			in[i] = start + steps * (i / count);
		return in;
	}

	public static final int[] intCountingUp(int start, int end) {
		int[] in = new int[end - start];
		for (int i = 0; i < in.length; i++)
			in[i] = start + i;
		return in;
	}

	public static final int[] intCountingUpTriQuads(int quadCount) {
		int[] in = new int[quadCount * 6];
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

	public static float[] floatRepeating(float[] is, int count) {
		if (count <= 0) {
			throw new IllegalArgumentException("Size should be greater than 0.");
		}

		int originalLength = is.length;
		int repeatedLength = originalLength * count;
		float[] result = new float[repeatedLength];

		for (int i = 0; i < count; i++) {
			System.arraycopy(is, 0, result, i * originalLength, originalLength);
		}

		return result;
	}

	public static final ArrayList<Byte> byteArrayToList(byte[] b) {
		ArrayList<Byte> al = new ArrayList<>(b.length);
		for (int i = 0; i < b.length; i++)
			al.add(b[i]);
		return al;
	}

	public static final byte[] byteListToPrimitive(List<Byte> bytes) {
		byte[] b = new byte[bytes.size()];
		for (int i = 0; i < b.length; i++)
			b[i] = bytes.get(i);
		return b;
	}

	public static final String byteArrayToHexString(byte[] byteArray) {
		StringBuilder sb = new StringBuilder();
		for (byte b : byteArray) {
			sb.append(String.format("%02X ", b));
		}
		return sb.toString();
	}

	/**
	 * Does not change the reader's position
	 */
	public static final String byteBufferToHexString(ByteBuffer bb) {
		int x = bb.position();
		StringBuilder sb = new StringBuilder();
		while (bb.hasRemaining()) {
			sb.append(String.format("%02X ", bb.get()));
		}
		bb.position(x);
		return sb.toString();
	}

	public static String byteBufferToHexStringTable(ByteBuffer bb, int columnCount, int columnWidth) {
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

	public static String byteBufferToHexString(ByteBuffer bb, int startingPos) {
		int x = bb.position();
		bb.position(startingPos);
		StringBuilder sb = new StringBuilder();
		while (bb.hasRemaining()) {
			sb.append(String.format("%02X ", bb.get()));
		}
		bb.position(x);
		return sb.toString();
	}

	public static final String repeatString(String str, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++)
			sb.append(str);
		return sb.toString();
	}

	public static final String ipToString(int ipv4) {
		return ipToString(intToByteArray(ipv4));
	}

	public static String ipToString(byte[] ipv4) {
		return String.format("%d.%d.%d.%d", ipv4[0], ipv4[1], ipv4[2], ipv4[3]);
	}

	public static final int[] castInt(Object[] arr) {
		return Arrays.stream((Object[]) arr).mapToInt(s -> (int) s).toArray();
	}

	public static final <T> Object[] castObject(T[] arr) {
		return Arrays.stream(arr).map(s -> (Object) s).toArray();
	}

	public static final Object[] castObject(long[] arr) {
		return Arrays.stream(arr).mapToObj(s -> (Object) s).toArray();
	}

	public static final Object[] castObject(int[] arr) {
		return Arrays.stream(arr).mapToObj(s -> (Object) s).toArray();
	}

	public static final Object[] castObject(short[] arr) {
		final Object[] narr = new Object[arr.length];
		for (int i = 0; i < arr.length; i++) {
			narr[i] = (Object) arr[i];
		}
		return narr;
	}

	public static final Object[] castObject(char[] arr) {
		final Object[] narr = new Object[arr.length];
		for (int i = 0; i < arr.length; i++) {
			narr[i] = (Object) arr[i];
		}
		return narr;
	}

	public static final Object[] castObject(byte[] arr) {
		final Object[] narr = new Object[arr.length];
		for (int i = 0; i < arr.length; i++) {
			narr[i] = (Object) arr[i];
		}
		return narr;
	}

	public static final Object[] castObject(double[] arr) {
		final Object[] narr = new Object[arr.length];
		for (int i = 0; i < arr.length; i++) {
			narr[i] = (Object) arr[i];
		}
		return narr;
	}

	public static final Object[] castObject(float[] arr) {
		final Object[] narr = new Object[arr.length];
		for (int i = 0; i < arr.length; i++) {
			narr[i] = (Object) arr[i];
		}
		return narr;
	}

	public static final int[] toIntArray(byte[] arr) {
		int[] out = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			out[i] = arr[i];
		}
		return out;
	}

	public static final Object[] toObjectArray(int[] data) {
		return Arrays.stream(data).mapToObj(Integer::valueOf).toArray();
	}

	public static final byte[] toByteArray(ByteBuffer cb) {
		if (cb.hasArray()) {
			return cb.array();
		} else {
			int old = cb.position();
			cb.rewind();
			byte[] c = new byte[cb.remaining()];
			cb.get(c);
			cb.position(old);
			return c;
		}
	}

	public static final double round(double value, int decimals) {
		final double places = Math.pow(10, decimals);
		return Math.round(value * places) / places;
	}

	public static final String roundFill(double value, int decimals) {
		return String.format("%." + decimals + "f", value);
	}

	public static final float applyMinThreshold(float x, float min) {
		return Math.abs(x) < min ? 0 : x;
	}

	public static final Color randomColor(boolean alpha) {
		if (alpha)
			return new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255),
					(int) (Math.random() * 255));
		else
			return new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
	}

	public static final Color clampColor(int red, int green, int blue) {
		return new Color(clamp(0, 255, red), clamp(0, 255, green), clamp(0, 255, blue));
	}

	public static final Color clampColor(int red, int green, int blue, int alpha) {
		return new Color(clamp(0, 255, red), clamp(0, 255, green), clamp(0, 255, blue), clamp(0, 255, alpha));
	}

	public static final byte clamp(byte from, byte to, byte x) {
		return x < from ? from : (x > to ? to : x);
	}

	public static final short clamp(short from, short to, short x) {
		return x < from ? from : (x > to ? to : x);
	}

	public static final char clamp(char from, char to, char x) {
		return x < from ? from : (x > to ? to : x);
	}

	public static final int clamp(int from, int to, int x) {
		return x < from ? from : (x > to ? to : x);
	}

	public static final float clamp(float from, float to, float x) {
		return x < from ? from : (x > to ? to : x);
	}

	public static final double clamp(double from, double to, double x) {
		return x < from ? from : (x > to ? to : x);
	}

	public static final byte clampRange(byte from, byte to, byte x) {
		return clamp(min(from, to), max(from, to), x);
	}

	public static final short clampRange(short from, short to, short x) {
		return clamp(min(from, to), max(from, to), x);
	}

	public static final char clampRange(char from, char to, char x) {
		return clamp(min(from, to), max(from, to), x);
	}

	public static final int clampRange(int from, int to, int x) {
		return clamp(Math.min(from, to), Math.max(from, to), x);
	}

	public static final float clampRange(float from, float to, float x) {
		return clamp(Math.min(from, to), Math.max(from, to), x);
	}

	public static final double clampRange(double from, double to, double x) {
		return clamp(Math.min(from, to), Math.max(from, to), x);
	}

	public static final byte min(byte a, byte b) {
		return a < b ? a : b;
	}

	public static final byte max(byte a, byte b) {
		return a > b ? a : b;
	}

	public static final short min(short a, short b) {
		return a < b ? a : b;
	}

	public static final short max(short a, short b) {
		return a > b ? a : b;
	}

	public static final char min(char a, char b) {
		return a < b ? a : b;
	}

	public static final char max(char a, char b) {
		return a > b ? a : b;
	}

	public static final String fillString(String str, String place, int length) {
		return (str.length() < length ? repeatString(place, length - str.length()) + str : str);
	}

	public static final int[] randomIntArray(int length, int min, int max) {
		Random rand = new Random();
		int[] arr = new int[length];
		for (int i = 0; i < length; i++) {
			arr[i] = rand.nextInt(max - min) + min;
		}
		return arr;
	}

	public static String getIncrement(String filePath) {
		String woExt = removeFileExtension(filePath);
		String ext = getFileExtension(filePath);

		int index = 1;
		while (Files.exists(Paths.get(woExt + "-" + index + "." + ext))) {
			index++;
			if (index < 0) {
				throw new BufferOverflowException();
			}
		}

		return woExt + "-" + index + "." + ext;
	}

	public static String readStringFile(String filePath) {
		String str;
		if (!Files.exists(Paths.get(filePath))) {
			throw new RuntimeException(new FileNotFoundException("File [" + filePath + "] does not exist"));
		}

		try {
			str = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (Exception excp) {
			throw new RuntimeException("Error reading file [" + filePath + "]", excp);
		}

		return str;
	}

	public static byte[] readBytesFile(String filePath) {
		if (!Files.exists(Paths.get(filePath))) {
			throw new RuntimeException(new FileNotFoundException("File [" + filePath + "] does not exist"));
		}

		try {
			return Files.readAllBytes(Paths.get(filePath));
		} catch (Exception excp) {
			throw new RuntimeException("Error reading file [" + filePath + "]", excp);
		}
	}

	public static String readStringFile(File file) {
		return readStringFile(file.getPath());
	}

	public static String listAll(String path) throws IOException {
		String list = "";
		// list all the files in the 'path' directory and add them to the string 'list'
		File directory = new File(path);
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					list += file + "\n";
				} else {
					list += listAll(file.getCanonicalPath());
				}
			}
		}
		return list;
	}

	public static String recursiveTree(String path) throws IOException {
		String list = ".";
		return list + recursiveTree(1, path);
	}

	public static String recursiveTree(int depth, String path) throws IOException {
		final String prefix = repeatString("|  ", depth).trim() + "- ";

		String list = "";

		final File directory = new File(path);
		final File[] files = directory.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				final File file = files[i];

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

	public static String getHumanFormatFileSize(long fileSize) {
		if (fileSize <= 0)
			return "0 B";

		int digitGroups = (int) (Math.log10(fileSize) / Math.log10(1024));

		return String.format("%.1f %s", fileSize / Math.pow(1024, digitGroups), HUMAN_FILE_SIZE_UNITS[digitGroups]);
	}

	public static long getFileSize(File file) {
		return getFileSize(Paths.get(file.getPath()));
	}

	public static long getFileSize(Path path) {
		try {
			FileChannel imageFileChannel = FileChannel.open(path);

			long imageFileSize = imageFileChannel.size();
			imageFileChannel.close();

			return imageFileSize;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String appendFileName(String path, String suffix) {
		return path.replaceAll("(.+)(\\.[^.]+)$", "$1" + suffix + "$2");
	}

	public static String replaceFileExtension(String path, String ext) {
		return path.replaceAll("(.+)(\\.[^.]+)$", "$1." + ext);
	}

	public static String removeFileExtension(String path) {
		return path.replaceAll("(.+)(\\.[^.]+)$", "$1");
	}

	public static String getFileName(String path) {
		return Paths.get(path).getFileName().toString().replaceAll("(.+)(\\.[^.]+)$", "$1");
	}

	public static String getFileExtension(String path) {
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

	public static double clampGreaterOrEquals(double min, double x) {
		return x <= min ? min : x;
	}

	public static float clampGreaterOrEquals(float min, float x) {
		return x <= min ? min : x;
	}

	public static int clampGreaterOrEquals(int min, int x) {
		return x <= min ? min : x;
	}

	public static long clampGreaterOrEquals(long min, long x) {
		return x <= min ? min : x;
	}

	public static String wrapLine(String text, int lineWidth) {
		if (text == null || lineWidth < 1) {
			throw new IllegalArgumentException("Invalid input");
		}

		StringBuilder wrappedText = new StringBuilder();
		String[] words = text.split(" ");
		int currentLineLength = 0;

		for (String word : words) {
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

	public static <T> List<T> limitSize(List<T> lines, int count, boolean trailing) {
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

	public static <T> T try_(ThrowingSupplier<T, Throwable> suplier, Function<Throwable, T> except) {
		try {
			return suplier.get();
		} catch (Throwable e) {
			return except.apply(e);
		}
	}

	@SafeVarargs
	public static <T> ArrayList<T> asArrayList(T... data) {
		ArrayList<T> arraylist = new ArrayList<>(data.length);
		Collections.addAll(arraylist, data);
		return arraylist;
	}

	public static Set<Class<?>> getTypesInPackage(String packageName) {
		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		return reader.lines().filter(line -> line.endsWith(".class")).map(line -> getClass(line, packageName))
				.collect(Collectors.toSet());
	}

	private static Class<?> getClass(String className, String packageName) {
		try {
			return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static int[] byteBufferToIntArray(ByteBuffer bData, int length) {
		int[] data = new int[length];
		for (int i = 0; i < length; i++) {
			data[i] = bData.getInt();
		}
		return data;
	}

	public static ByteBuffer intArrayToByteBuffer(int[] data) {
		final ByteBuffer buffer = ByteBuffer.allocate(data.length * Integer.BYTES);
		for (int i = 0; i < data.length; i++) {
			buffer.putInt(data[i]);
		}
		return (ByteBuffer) buffer.flip();
	}

	public static int[] toPrimitiveInt(Object data) {
		if (data instanceof int[]) {
			return (int[]) data;
		}
		return Arrays.stream((Object[]) data).map((Object i) -> (int) (i == null ? 0 : i)).mapToInt(Integer::intValue)
				.toArray();
	}

	public static byte[] toPrimitiveByte(Object data) {
		if (data instanceof byte[]) {
			return (byte[]) data;
		}
		final Object[] arr = (Object[]) data;
		final byte[] y = new byte[arr.length];
		for (int i = 0; i < arr.length; i++)
			y[i] = Byte.valueOf((byte) arr[i]);
		return y;
	}

	public static float[] toPrimitiveFloat(Object data) {
		if (data instanceof float[]) {
			return (float[]) data;
		}
		final Object[] arr = (Object[]) data;
		final float[] y = new float[arr.length];
		for (int i = 0; i < arr.length; i++)
			y[i] = Float.valueOf((float) arr[i]);
		return y;
	}

	public static String joinString(String[] tokens, int start, int end) {
		return IntStream.range(start, end).mapToObj(i -> tokens[i]).collect(Collectors.joining());
	}

	public static List<String> recursiveList(Path directory) throws IOException {
		try (Stream<Path> walk = Files.walk(directory)) {
			return walk.filter(Files::isRegularFile).map(path -> directory.relativize(path).toString())
					.collect(Collectors.toList());
		}
	}

	public static String toString(InputStream inputStream) {
		Objects.requireNonNull(inputStream, "InputStream cannot be null.");
		try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			return br.lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] toBytes(InputStream inputStream) {
		Objects.requireNonNull(inputStream, "InputStream cannot be null.");

		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
			byte[] data = new byte[8192];
			int bytesRead;
			while ((bytesRead = inputStream.read(data)) != -1) {
				buffer.write(data, 0, bytesRead);
			}
			return buffer.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Stream<String> toLineStream(InputStream inputStream) {
		Objects.requireNonNull(inputStream, "InputStream cannot be null.");
		return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines();
	}

	public static String replaceLast(String original, String target, String replacement) {
		// Escape the target string for regex special characters
		String escapedTarget = target.replaceAll("([\\W])", "\\\\$1");
		// Replace the last occurrence using regex lookahead
		return original.replaceFirst("(?s)(.*)" + escapedTarget, "$1" + replacement);
	}

	public static <V> Iterable<V> toIterable(Iterator<V> iterator) {
		return new Iterable<V>() {
			@Override
			public Iterator<V> iterator() {
				return iterator;
			}

			@Override
			public void forEach(Consumer<? super V> action) {
				iterator.forEachRemaining(action);
			}
		};
	}

	/**
	 * Removes the rightmost characters from a string. "abcdef", 5 -> "abcde"
	 */
	public static String rightTrimToLength(String str, int length) {
		return str.length() > length ? str.substring(0, length) : str;
	}

	/**
	 * Removes the leftmost characters from a string.<br>
	 * "abcdef", 5 -> "bcdef"
	 */
	public static String leftTrimToLength(String str, int maxLength) {
		return str.length() <= maxLength ? str : str.substring(str.length() - maxLength);
	}

	public static String leftPadString(String str, String fill, int length) {
		return (str.length() < length ? repeatString(fill, length - str.length()) + str : str);
	}

	public static String rightPadString(String str, String fill, int length) {
		return (str.length() < length ? str + repeatString(fill, length - str.length()) : str);
	}

	/**
	 * "abcdef", " ", 5 -> "bcdef"<br>
	 * "abc", " ", 5 -> " abc"
	 */
	public static String leftPadStringLeftTrim(String str, String fill, int length) {
		return (str.length() < length ? repeatString(fill, length - str.length()) + str
				: leftTrimToLength(str, length));
	}

	/**
	 * "abcdef", " ", 5 -> "abcde"<br>
	 * "abc", " ", 5 -> " abc"
	 */
	public static String leftPadStringRightTrim(String str, String fill, int length) {
		return (str.length() < length ? repeatString(fill, length - str.length()) + str
				: rightTrimToLength(str, length));
	}

	public static ByteBuffer readFile(File file) throws IOException {
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

	public static String leftPadLine(String str, String fill) {
		return Arrays.stream(str.split("\n")).collect(Collectors.joining("\n" + fill, fill, ""));
	}

	@DependsOn("org.json.JSONObject")
	public static Object getSubKey(String[] keys, JSONObject obj) {
		// System.out.println(keys.length + "> " + String.join(".", keys));
		JSONObject currentObj = obj;
		Object value = null;

		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];

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
	public static Set<String> extractKeys(JSONObject jsonObject) {
		Set<String> keys = new HashSet<>();
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
	private static void extractKeys(JSONObject jsonObject, String parentKey, Set<String> keys) {
		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();
			String fullKey = parentKey.isEmpty() ? key : parentKey + "." + key;

			// Add the full key to the set
			keys.add(fullKey);

			// If the value associated with the key is a JSONObject, recurse
			if (jsonObject.get(key) instanceof JSONObject) {
				extractKeys(jsonObject.getJSONObject(key), fullKey, keys);
			}
		}
	}

	public static boolean extractFile(String inPath, File outFile) throws IOException {
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

	public static <K, V> Map<K, V> castMap(Map<?, ?> map, Supplier<Map<K, V>> supplier, Class<K> keyClass,
			Class<V> valueClass) {
		return map.entrySet().stream().collect(Collectors.toMap(e -> keyClass.cast(e.getKey()),
				e -> valueClass.cast(e.getValue()), (k1, k2) -> k1, supplier));
	}

	public static <T> T throw_(Throwable e) throws Throwable {
		throw e;
	}

	public static <T> T throw_(Exception e) throws Exception {
		throw e;
	}

	public static <T> T throwRuntime(Throwable e) throws RuntimeException {
		throw new RuntimeException(e);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> HashMap<K, V> hashMap(Object... objects) {
		HashMap<K, V> map = new HashMap<>();

		for (int i = 0; i < objects.length; i += 2) {
			map.put((K) objects[i], (V) objects[i + 1]);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	public static boolean validEmail(String email) {
		return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
	}

	public static long map(long x, long in_min, long in_max, long out_min, long out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static int map(int x, int in_min, int in_max, int out_min, int out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static float map(float x, float in_min, float in_max, float out_min, float out_max) {
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
	public static boolean matches(JSONObject obj1, JSONObject solution) {
		// Check for null objects
		if (obj1 == null || solution == null) {
			return false;
		}

		for (String key : solution.keySet()) {
			if (!obj1.has(key) || !obj1.get(key).equals(solution.get(key))) {
				return false;
			}
		}

		return true;
	}

	public static int snap(int x, int interval) {
		return ((x + interval - 1) / interval) * interval;
	}

	public static double snap(double x, double interval) {
		return ((x + interval - 1) / interval) * interval;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] fillArray(T[] arr, Object... objects) {
		for (int i = 0; i < objects.length; i++) {
			arr[i] = (T) objects[i];
		}
		return arr;
	}

	public static <T> T[] fillArray(T[] arr, Function<Integer, T> provider) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = provider.apply(i);
		}
		return arr;
	}

	public static <T> List<T> reversed(List<T> list) {
		Collections.reverse(list);
		return list;
	}

	public static <T> List<T> shuffled(List<T> list) {
		Collections.shuffle(list);
		return list;
	}

	public static long nanoTime(Runnable run) {
		final long start = System.nanoTime();
		run.run();
		return System.nanoTime() - start;
	}

	public static long millisTime(Runnable run) {
		final long start = System.currentTimeMillis();
		run.run();
		return System.currentTimeMillis() - start;
	}

	public static <T> Pair<T, Long> nanoTime(Supplier<T> run) {
		final long start = System.nanoTime();
		final T output = run.get();
		return Pairs.readOnly(output, System.nanoTime() - start);
	}

	public static <T> Pair<T, Long> millisTime(Supplier<T> run) {
		final long start = System.currentTimeMillis();
		final T output = run.get();
		return Pairs.readOnly(output, System.currentTimeMillis() - start);
	}

	public static int clampLessOrEquals(int x, int max) {
		return x > max ? max : x;
	}

	public static long clampLessOrEquals(long x, long max) {
		return x > max ? max : x;
	}

	public static double clampLessOrEquals(double x, double max) {
		return x > max ? max : x;
	}

	public static float clampLessOrEquals(float x, float max) {
		return x > max ? max : x;
	}

	public static <T> T[] add(T[] beanPackages, T name) {
		T[] newArray = Arrays.copyOf(beanPackages, beanPackages.length + 1);
		newArray[beanPackages.length] = name;
		return newArray;
	}

	public static <T> List<T> add(List<T> beanPackages, T name) {
		beanPackages.add(name);
		return beanPackages;
	}

	public static <T> List<T> addAll(List<T> beanPackages, List<T> name) {
		beanPackages.addAll(name);
		return beanPackages;
	}

	public static String camelCaseToSnakeCase(String input) {
		if (input == null || input.isEmpty())
			return input;

		StringBuilder result = new StringBuilder();
		char[] chars = input.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			char current = chars[i];

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

	public static String sqlEscapeIdentifier(String identifier) {
		if (identifier.equals("*") || identifier.endsWith(".*")) {
			return identifier;
		}
		String[] parts = identifier.split("\\.");
		for (int i = 0; i < parts.length; i++) {
			if (!parts[i].equals("*") && !parts[i].startsWith("`") && !parts[i].endsWith("`")) {
				parts[i] = "`" + parts[i].replace("`", "``") + "`";
			}
		}
		return String.join(".", parts);
	}

	public static boolean duplicates(String[] refCols) {
		return Arrays.stream(refCols).distinct().count() < refCols.length;
	}

	public static Map<String, Object> asMap(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		final Map<String, Object> map = new HashMap<>();
		for (int i = 1; i <= columnCount; i++) {
			String colName = rs.getMetaData().getColumnName(i);
			map.put(colName, rs.getObject(i));
		}
		return map;
	}

	public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			if (columnName.equalsIgnoreCase(metaData.getColumnLabel(i))) {
				return true;
			}
		}
		return false;
	}

	public static Map<String, Integer> getColumnMapping(ResultSet rs) throws SQLException {
		final int count = rs.getMetaData().getColumnCount();
		final Map<String, Integer> map = new HashMap<>();
		for (int i = 1; i <= count; i++) {
			String colName = rs.getMetaData().getColumnName(i);
			map.put(colName, i);
		}
		return map;
	}

	public static int getColumnIndex(ResultSet rs, String columnName) throws SQLException {
		final int count = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= count; i++) {
			String colName = rs.getMetaData().getColumnLabel(i);
			if (colName.equals(columnName)) {
				return i;
			}
		}
		throw new IllegalArgumentException("No column found for: " + columnName);
	}

	public static <T, R> ThrowingFunction<List<T>, R, Throwable> first(Function<T, R> transformer) {
		return (List<T> list) -> {
			if (list.isEmpty()) {
				throw new NoSuchElementException();
			} else {
				return transformer.apply(list.get(0));
			}
		};
	}

	public static <T, R> ThrowingFunction<List<T>, R, Throwable> first(Function<T, R> transformer,
			Supplier<R> default_) {
		return (List<T> list) -> {
			if (list.isEmpty()) {
				return default_.get();
			} else {
				return transformer.apply(list.get(0));
			}
		};
	}

	public static <T, R> ThrowingFunction<List<T>, R, Throwable> first(Function<T, R> transformer, R default_) {
		return (List<T> list) -> {
			if (list.isEmpty()) {
				return default_;
			} else {
				return transformer.apply(list.get(0));
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] insert(T[] array, int index, T element) {
		if (index < 0 || index > array.length) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
		}

		T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length + 1);

		System.arraycopy(array, 0, result, 0, index);
		result[index] = element;
		System.arraycopy(array, index, result, index + 1, array.length - index);

		return result;
	}

	public static void notImplemented() {
		throw new UnsupportedOperationException("Not implemented.");
	}

	public static Timestamp toTimestamp(Date value) {
		return new Timestamp(value.getTime());
	}

	public static Date toDate(Timestamp value) {
		return new Date(value.getTime());
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] combineArrays(T[] first, T[] second) {
		T[] result = (T[]) Array.newInstance(first.getClass().getComponentType(), first.length + second.length);

		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);

		return result;
	}

	public static <T> T newInstance(Class<T> clazz) {
		Objects.requireNonNull(clazz);

		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Error while creating new instance of type [" + clazz.getName() + "]", e);
		}
	}

	public static Class<?> getRawClass(Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) type).getRawType();
		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			Class<?> rawComponent = getRawClass(componentType);
			return Array.newInstance(rawComponent, 0).getClass();
		}

		throw new IllegalArgumentException("Unsupported Type: " + type);
	}

	public static String constantToCamelCase(String enumName) {
		StringBuilder result = new StringBuilder();
		String[] parts = enumName.toLowerCase().split("_");

		for (int i = 0; i < parts.length; i++) {
			if (i == 0) {
				result.append(parts[i]);
			} else {
				result.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
			}
		}

		return result.toString();
	}

	public static String camelCaseToConstant(String camelCase) {
		StringBuilder result = new StringBuilder();

		for (char c : camelCase.toCharArray()) {
			if (Character.isUpperCase(c)) {
				result.append('_');
			}
			result.append(Character.toUpperCase(c));
		}

		return result.toString();
	}

	public static Field[] getAllFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		Class<?> current = clazz;
		while (current != null && current != Object.class) {
			Collections.addAll(fields, current.getDeclaredFields());
			current = current.getSuperclass();
		}
		return fields.toArray(new Field[0]);
	}

	public static String getStatementAsSQL(PreparedStatement pstmt) {
		return (pstmt instanceof ClientPreparedStatement
				? ((com.mysql.cj.PreparedQuery) ((ClientPreparedStatement) pstmt).getQuery()).asSql()
				: pstmt.toString());
	}

	public static String enumToNameString(Enum<?> c) {
		return PCUtils.capitalize(c.name().replace('_', ' ').toLowerCase());
	}

	public static String nameStringToEnum(String c) {
		return c.toUpperCase().replace(' ', '_');
	}

	public static <T extends Enum<T>> T enumValuetoEnum(Class<T> enumClass, String e) {
		try {
			final T val = Enum.valueOf(enumClass, e);
			return val;
		} catch (IllegalArgumentException es) {
			return null;
		}
	}

	public static Color oppositeColor(Color color) {
		if (color == null)
			return null;

		final int r = 255 - color.getRed();
		final int g = 255 - color.getGreen();
		final int b = 255 - color.getBlue();

		return new Color(r, g, b, color.getAlpha());
	}

	public static double luminance(Color c) {
		final double[] rgb = { c.getRed(), c.getGreen(), c.getBlue() };
		for (int i = 0; i < 3; i++) {
			final double v = rgb[i] / 255.0;
			rgb[i] = (v <= 0.03928) ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
		}
		return 0.2126 * rgb[0] + 0.7152 * rgb[1] + 0.0722 * rgb[2];
	}

	public static double contrast(Color c1, Color c2) {
		final double l1 = luminance(c1);
		final double l2 = luminance(c2);
		final double brightest = Math.max(l1, l2);
		final double darkest = Math.min(l1, l2);
		return (brightest + 0.05) / (darkest + 0.05);
	}

	public static Color maxContrast(Color background) {
		final Color black = Color.BLACK;
		final Color white = Color.WHITE;
		return contrast(background, black) >= contrast(background, white) ? black : white;
	}

	public static Color maxContrast(Color background, Color... choices) {
		return maxContrast(background, Arrays.stream(choices));
	}

	public static Color maxContrast(Color background, List<Color> choices) {
		return maxContrast(background, choices.stream());
	}

	public static Color maxContrast(Color background, Stream<Color> stream) {
		return stream.sorted((c1, c2) -> (int) Math.signum(contrast(background, c2) - contrast(background, c1)))
				.findFirst().orElseGet(() -> maxContrast(background));
	}

	public static void close(Closeable... result) throws IOException {
		if (result == null)
			return;
		for (Closeable r : result)
			if (r != null)
				r.close();
	}

	public static void close(AutoCloseable... result) throws Exception {
		if (result == null)
			return;
		for (AutoCloseable r : result)
			if (r != null)
				r.close();
	}

	public static boolean isToday(java.sql.Date date) {
		return isToday(date.toLocalDate());
	}

	public static boolean isToday(LocalDate localDate) {
		Objects.requireNonNull(localDate);

		LocalDate today = LocalDate.now();
		return today.equals(localDate);
	}

	public static String toSimpleIdentityString(Object value) {
		Objects.requireNonNull(value);
		return value.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(value));
	}

	public static String readPackagedStringFile(String path) {
		try {
			final InputStream in = PCUtils.class.getResourceAsStream(path);
			return toString(in);
		} catch (Exception e) {
			throw new RuntimeException("Error while reading packaged file `" + path + "`", e);
		}
	}

	public static byte[] readPackagedBytesFile(String path) {
		try {
			final InputStream in = PCUtils.class.getResourceAsStream(path);
			return toBytes(in);
		} catch (Exception e) {
			throw new RuntimeException("Error while reading packaged file `" + path + "`", e);
		}
	}

	public static String readStringSource(String location) {
		if (location.startsWith("classpath:")) {
			String path = location.substring("classpath:".length());
			return readPackagedStringFile(path);
		} else if (location.startsWith("resource:")) {
			return readStringSource("classpath:" + location.substring("resource:".length()));
		} else if (location.startsWith("file:")) {
			String path = location.substring("file:".length());
			return PCUtils.readStringFile(path);
		} else {
			return PCUtils.readStringFile(location);
		}
	}

	public static <T> Constructor<T> findCompatibleConstructor(Class<T> clazz, Class<?>... argTypes)
			throws NoSuchMethodException {
		for (Constructor<?> ctor : clazz.getConstructors()) {
			Class<?>[] params = ctor.getParameterTypes();
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

	public static String progressBar(long budgetNanos, long usedNanos, boolean showOverFlow) {
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

	public static byte[] readBytesSource(String location) {
		if (location.startsWith("classpath:")) {
			String path = location.substring("classpath:".length());
			return readPackagedBytesFile(path);
		} else if (location.startsWith("resource:")) {
			return readBytesSource("classpath:" + location.substring("resource:".length()));
		} else if (location.startsWith("file:")) {
			String path = location.substring("file:".length());
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

		int rgb = (int) Long.parseLong(hex, 16);

		if (hex.length() == 6) {
			return new Color(rgb);
		} else {
			int red = (rgb >> 24) & 0xFF;
			int green = (rgb >> 16) & 0xFF;
			int blue = (rgb >> 8) & 0xFF;
			int alpha = rgb & 0xFF;
			return new Color(red, green, blue, alpha);

		}
	}

	public static void throwUnsupported() {
		throw new UnsupportedOperationException();
	}

	public static void throwUnsupported(String msg) {
		throw new UnsupportedOperationException(msg);
	}

	public static int countElements(Iterator<?> e) {
		int count = 0;
		while (e.hasNext()) {
			count++;
			e.next();
		}
		return count;
	}

	public static int countElements(ResultSet e) throws SQLException {
		int count = 0;
		while (e.next()) {
			count++;
		}
		return count;
	}

	@SafeVarargs
	public static <T> Stream<? extends T> concatStreams(Stream<? extends T>... streams) {
		return Arrays.stream(streams).flatMap(c -> c);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] removeArray(T[] args, int i) {
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
	public static <T> T[] removeArray(T[] args, BiPredicate<Integer, Object> isToRemove) {
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

	public static int deleteOldFiles(File directory, int keepCount) {
		if (!directory.isDirectory()) {
			return 0;
		}

		final File[] files = directory.listFiles();
		if (files == null || files.length <= keepCount) {
			return 0;
		}

		Arrays.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

		int deleteCount = 0;
		for (int i = 0; i < files.length - keepCount; i++) {
			final File file = files[i];
			if (!file.delete()) {
				deleteCount++;
			}
		}

		return deleteCount;
	}

	public static String insertChar(String s, int pos, char c) {
		if (pos < 0)
			pos = 0;
		if (pos > s.length())
			pos = s.length();

		return s.substring(0, pos) + c + s.substring(pos);
	}

	public static String backspace(String s, int pos) {
		if (s.isEmpty())
			return s;
		if (pos <= 0)
			return s; // nothing to delete
		if (pos > s.length())
			pos = s.length();

		return s.substring(0, pos - 1) + s.substring(pos);
	}

	public static String deleteChar(String s, int pos) {
		if (s.isEmpty())
			return s;
		if (pos < 0 || pos >= s.length())
			return s;

		return s.substring(0, pos) + s.substring(pos + 1);
	}

	public static String replace(String s, char target, char replacement, int maxCount, boolean fromEnd) {
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

	public static long randomId(String seed) {
		return System.identityHashCode(PCUtils.hashStringSha256(seed)) + System.nanoTime();
	}

	public static boolean validString(String content) {
		return content != null && !content.isEmpty() && !content.matches("^\\s*$");
	}

	public static float[] fill(float[] bs, float id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static double[] fill(double[] bs, double id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static byte[] fill(byte[] bs, byte id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static short[] fill(short[] bs, short id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static char[] fill(char[] bs, char id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static int[] fill(int[] bs, int id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static long[] fill(long[] bs, long id) {
		Arrays.fill(bs, id);
		return bs;
	}

	public static List<Thread> getAllThreads() {
		final Thread[] ts = new Thread[Thread.activeCount()];
		Thread.enumerate(ts);
		return Arrays.asList(ts);
	}

	public static int[] combineArrays(int[] array1, int[] array2) {
		// https://stackoverflow.com/questions/4697255/combine-two-integer-arrays
		final int[] array1and2 = new int[array1.length + array2.length];
		System.arraycopy(array1, 0, array1and2, 0, array1.length);
		System.arraycopy(array2, 0, array1and2, array1.length, array2.length);
		return array1and2;
	}

	public static float[] pack(float[]... arrays) {
		int total = 0;
		for (float[] arr : arrays)
			total += arr.length;

		float[] out = new float[total];
		int offset = 0;

		for (float[] arr : arrays) {
			System.arraycopy(arr, 0, out, offset, arr.length);
			offset += arr.length;
		}
		return out;
	}

	public static int[] getMaxIndices(float[] arr, int n) {
		if (n <= 0)
			return new int[0];

		PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[0]));

		for (int i = 0; i < arr.length; i++) {
			float value = arr[i];

			if (pq.size() < n) {
				pq.offer(new int[] { Float.floatToRawIntBits(value), i });
			} else if (value > Float.intBitsToFloat(pq.peek()[0])) {
				pq.poll();
				pq.offer(new int[] { Float.floatToRawIntBits(value), i });
			}
		}

		int[] result = new int[pq.size()];
		int idx = 0;
		for (int[] entry : pq) {
			result[idx++] = entry[1];
		}
		return result;
	}

	public static float stdDev(float[] values) {
		int n = values.length;
		if (n == 0)
			return 0f;

		float mean = 0f;
		for (float v : values)
			mean += v;
		mean /= n;

		float sumSq = 0f;
		for (float v : values) {
			float d = v - mean;
			sumSq += d * d;
		}

		return (float) Math.sqrt(sumSq / n);
	}

	public static float median(float[] values) {
		float[] copy = values.clone();
		Arrays.sort(copy);

		int n = copy.length;
		if ((n & 1) == 1) {
			return copy[n / 2];
		}
		return (copy[n / 2 - 1] + copy[n / 2]) * 0.5f;
	}

}
