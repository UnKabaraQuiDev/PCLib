package lu.pcy113.pclib;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lu.pcy113.pclib.impl.ExceptionSupplier;

public final class PCUtils {

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

	public static String hashString(String input, String algorithm) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			byte[] hashBytes = messageDigest.digest(input.getBytes());
			return bytesArrayToHexString(hashBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Hashing algorithm not found", e);
		}
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
					return (simple ? PCUtils.getFileExtension(ste.getClassName()) : ste.getClassName()) + "#" + ste.getMethodName() + "@" + ste.getLineNumber();
				} else {
					ste = stElements[i + 1];
					return (simple ? PCUtils.getFileExtension(ste.getClassName()) : ste.getClassName()) + "#" + ste.getMethodName() + "@" + ste.getLineNumber();
				}

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

	public static byte[] byteBufferToArray(ByteBuffer bb) {
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

	public static float[] floatRepeating(float[] is, int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("Size should be greater than 0");
		}

		int originalLength = is.length;
		int repeatedLength = originalLength * size;
		float[] result = new float[repeatedLength];

		for (int i = 0; i < size; i++) {
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

	public static final int[] castInt(Object[] arr) {
		return Arrays.stream(arr).mapToInt(s -> (int) s).toArray();
	}

	public static final int[] toIntArray(byte[] arr) {
		int[] out = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			out[i] = arr[i];
		}
		return out;
	}

	public static final int[] castInt(Integer[] arr) {
		return Arrays.stream(arr).mapToInt(Integer::valueOf).toArray();
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

	public static final double round(double round, int decimales) {
		double places = Math.pow(10, decimales);
		return Math.round(round * places) / places;
	}

	public static final float applyMinThreshold(float x, float min) {
		return Math.abs(x) < min ? 0 : x;
	}

	public static final Color randomColor(boolean alpha) {
		if (alpha)
			return new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
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

	public static String getFileExtension(String path) {
		if (path.contains(".")) {
			return path.replaceAll("(.+\\.)([^.]+)$", "$2");
		} else {
			return path;
		}
	}

	private static final Collector<?, ?, ?> SHUFFLER = Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), list -> {
		Collections.shuffle(list);
		return list;
	});

	@SuppressWarnings("unchecked")
	public static <T> Collector<T, ?, List<T>> toShuffledList() {
		return (Collector<T, ?, List<T>>) SHUFFLER;
	}

	public static float clampGreaterOrEquals(float min, float x) {
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

	public static <T> T try_(ExceptionSupplier<T> suuplier, Function<Exception, T> except) {
		try {
			return suuplier.get();
		} catch (Exception e) {
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
		return reader.lines().filter(line -> line.endsWith(".class")).map(line -> getClass(line, packageName)).collect(Collectors.toSet());
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
		ByteBuffer buffer = ByteBuffer.allocate(data.length * Integer.BYTES);
		for (int i = 0; i < data.length; i++) {
			buffer.putInt(data[i]);
		}
		return (ByteBuffer) buffer.flip();
	}

	public static int[] toPrimitiveInt(Object[] data) {
		return Arrays.stream(data).map((Object i) -> (int) (i == null ? 0 : i)).mapToInt(Integer::intValue).toArray();
	}

	public static byte[] toPrimitiveByte(Byte[] data) {
		byte[] y = new byte[data.length];
		for (int i = 0; i < data.length; i++)
			y[i] = Byte.valueOf((byte) data[i]);
		return y;
	}

	public static float[] toPrimitiveFloat(Object[] data) {
		float[] y = new float[data.length];
		for (int i = 0; i < data.length; i++)
			y[i] = Float.valueOf((float) data[i]);
		return y;
	}

	public static String joinString(String[] tokens, int start, int end) {
		return IntStream.range(start, end).mapToObj(i -> tokens[i]).collect(Collectors.joining());
	}
}
