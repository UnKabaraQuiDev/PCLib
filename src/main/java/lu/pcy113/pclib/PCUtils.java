package lu.pcy113.pclib;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class PCUtils {

	public static final String getCallerClassName(boolean parent) {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!PCUtils.class.getName().equals(ste.getClassName())) {
				if (!parent)
					return ste.getClassName() + "#" + ste.getMethodName() + "@" + ste.getLineNumber();
				else {
					ste = stElements[i + 1];
					return ste.getClassName() + "#" + ste.getMethodName() + "@" + ste.getLineNumber();
				}

			}
		}
		return null;
	}
	
	public static int byteToInt(byte[] byteArray) {
		if(byteArray.length != 4) {
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

	public static final String repeatString(String str, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++)
			sb.append(str);
		return sb.toString();
	}

	public static final int[] castInt(Object[] arr) {
		return Arrays.stream(arr).mapToInt(s -> (int) s).toArray();
	}

	public static final int[] castInt(Integer[] arr) {
		return Arrays.stream(arr).mapToInt(Integer::valueOf).toArray();
	}

	public static final Object[] toObjectArray(int[] data) {
		return Arrays.stream(data).mapToObj(Integer::valueOf).toArray();
	}

	public static final byte[] toByteArray(ByteBuffer cb) {
		int old = cb.position();
		byte[] c = new byte[cb.remaining()];
		cb.get(c);
		cb.position(old);
		return c;
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
		}

		return woExt + "-" + index + "." + ext;
	}
	
	public static String readStringFile(String filePath) {
		String str;
		if (!Files.exists(Paths.get(filePath))) {
			throw new RuntimeException("File [" + filePath + "] does not exist");
		}
		try {
			str = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException excp) {
			throw new RuntimeException("Error reading file [" + filePath + "]", excp);
		}
		return str;
	}

	public static String recursiveTree(String path) throws IOException {
		String list = "";
		// list all the files in the 'path' directory and add them to the string 'list'
		File directory = new File(path);
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					list += file + "\n";
				} else {
					list += recursiveTree(file.getCanonicalPath());
				}
			}
		}
		return list;
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
		return path.replaceAll("(.+\\.)([^.]+)$", "$2");
	}

}
