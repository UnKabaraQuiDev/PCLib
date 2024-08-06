package lu.pcy113.pclib.datastructure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.impl.Configuration;

public class YMLConfiguration extends YMLConfigurationSection implements Configuration {

	public void load(final FileReader fr) throws IOException {
		Objects.requireNonNull(fr);

		final BufferedReader br = new BufferedReader(fr);

		Stack<String> currentPath = new Stack<>();
		int currentDepth = 0;

		while (br.ready()) {
			final String line = br.readLine();

			// recalculate current depth
			if (!line.startsWith(PCUtils.repeatString("  ", currentDepth))) {
				final int newCurrentDepth = IntStream.range(0, currentDepth).map((depth) -> line.startsWith(PCUtils.repeatString("  ", depth)) ? depth : 0).max().orElse(0);
				for (int i = 0; i < currentDepth - newCurrentDepth; i++) {
					currentDepth--;
					currentPath.pop();
				}
			}

			String line2 = line.substring(2 * currentDepth).trim();

			if (line2.startsWith("  ")) {
				currentDepth++;
				line2 = line2.substring(2);
			}

			final String[] tokens = line2.split(":");

			if (tokens.length == 1) { // new section
				currentPath.push(tokens[0]);
			} else {
				this.set(createPath(currentPath) + "." + tokens[0], parseUntyped(tokens[1].trim()));
			}
		}
	}

	private Object parseUntyped(final String value) {
		System.out.println("untyped: '" + value + "'");

		if (value == null) {
			return null;
		}

		if (value.toCharArray().length == 0) {
			return value;
		}

		if (value.contains(".") && Character.isDigit(value.toCharArray()[0])) {
			return Double.parseDouble(value);
		} else if (Character.isDigit(value.toCharArray()[0])) {
			return Long.parseLong(value);
		}

		return value;
	}

	private String createPath(Stack<String> currentPath) {
		return currentPath.stream().collect(Collectors.joining("."));
	}

}
