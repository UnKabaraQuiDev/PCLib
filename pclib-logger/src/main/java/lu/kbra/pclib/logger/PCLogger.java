package lu.kbra.pclib.logger;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import lu.kbra.pclib.PCUtils;

/**
 * @deprecated use a dedicated library like SLF4J instead
 */
@Deprecated(forRemoval = true, since = "v1.1.0")
public class PCLogger implements Closeable {

	/**
	 * Exports the default configuration file to the specified file path
	 */
	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static final void exportDefaultConfig(final String outPath) throws IOException {
		if (Files.exists(Paths.get(outPath))) {
			return;
		}
		new File(outPath).getParentFile().mkdirs();
		Files.copy(PCLogger.class.getResourceAsStream("logs.properties"), Paths.get(outPath));
	}

	private boolean init, disabled = false, forwardContent = true, simpleClassNameLog = false;
	private Level minForwardLevel = Level.FINEST;
	private final Properties config;
	private File logFile;
	private final PrintWriter output;
	private final SimpleDateFormat sdf;
	private final String lineFormat, lineRawFormat;

	private List<String> callerWhiteList = new ArrayList<>();

	/**
	 * @param file The file to load the configuration from, uses the default configuration if null. Use
	 *             {@link #exportDefaultConfig(File)} to export the default configuration.
	 */
	@Deprecated(forRemoval = true, since = "v1.1.0")
	public PCLogger(final File file) throws IOException {
		this(file == null ? new InputStreamReader(PCLogger.class.getResourceAsStream("logs.properties")) : new FileReader(file));
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public PCLogger(final Reader reader) throws IOException {
		this.callerWhiteList.add(this.getClass().getName());

		this.config = new Properties();
		this.config.load(reader);

		if (this.config.containsKey("whitelist") && !((String) this.config.get("whitelist")).trim().isEmpty()) {
			final String[] whitelistArray = ((String) this.config.getOrDefault("whitelist", "")).trim().split(",");
			this.callerWhiteList.addAll(Arrays.asList(whitelistArray));
		}

		final String dateFormat = this.config.getProperty("date.format", "dd-MM-yyyy HH:mm:ss");
		this.sdf = new SimpleDateFormat(dateFormat);

		final SimpleDateFormat fdf = new SimpleDateFormat(this.config.getProperty("file.time.format", "dd-MM-yyyy HH-mm-ss"));

		final String format = this.config.getProperty("file.format", "./logs/log-%CURRENTMS%.txt")
				.replace("%CURRENTMS%", System.currentTimeMillis() + "")
				.replace("%TIME%", fdf.format(Date.from(Instant.now())));

		this.logFile = new File(format);
		if (!this.logFile.getParentFile().exists()) {
			this.logFile.getParentFile().mkdirs();
		}
		if (!this.logFile.exists()) {
			this.logFile.createNewFile();
		}

		this.lineFormat = this.config.getProperty("line.format", "[%TIME%][%THREAD%/%LEVEL%](%SIMPLECLASS%) %MSG%");
		this.lineRawFormat = this.config.getProperty("line.rawformat", "[%TIME%][%LEVEL%] %MSG%");

		this.forwardContent = Boolean.parseBoolean(this.config.getProperty("sysout.forward", "true"));
		this.simpleClassNameLog = Boolean.parseBoolean(this.config.getProperty("log.simpleclassname", "false"));

		this.output = new PrintWriter(new FileOutputStream(this.logFile), true);
		this.init = true;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public PCLogger(final String source) throws IOException {
		this(new StringReader(source));
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void addCallerWhiteList(final String s) {
		this.callerWhiteList.add(s);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	@Override
	public void close() {
		if (this.init) {
			this.output.flush();
			this.output.close();

			this.logFile = null;
			this.init = false;
		}
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public String getCallerClassName(final boolean parent, final boolean simple) {
		final StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i = 1; parent ? i < stElements.length - 1 : i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!this.callerWhiteList.contains(ste.getClassName())) {
				if (parent) {
					ste = stElements[i + 1];
					return (simple ? PCUtils.getFileExtension(ste.getClassName()) : ste.getClassName()) + "#" + ste.getMethodName() + "@"
							+ ste.getLineNumber();
				} else {
					return (simple ? PCUtils.getFileExtension(ste.getClassName()) : ste.getClassName()) + "#" + ste.getMethodName() + "@"
							+ ste.getLineNumber();
				}

			}
		}
		return "null";
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public List<String> getCallerWhiteList() {
		return this.callerWhiteList;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public PrintWriter getFileWriter() {
		return this.output;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public File getLogFile() {
		return this.logFile;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public Level getMinForwardLevel() {
		return this.minForwardLevel;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public boolean isDisabled() {
		return this.disabled;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public boolean isForwardContent() {
		return this.forwardContent;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public boolean isInit() {
		return this.init;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void log() {
		if (this.disabled) {
			return;
		}

		this.log(Level.INFO, "<- " + this.getCallerClassName(true, this.simpleClassNameLog));
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void log(final Level lvl) {
		if (this.disabled) {
			return;
		}

		this.log(lvl, "<- " + this.getCallerClassName(true, this.simpleClassNameLog));
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void log(final Level lvl, final String msg) {
		if (this.disabled) {
			return;
		}

		this._log(0, lvl, msg, false);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void log(final Level lvl, final String msg, final Object... objs) {
		if (this.disabled) {
			return;
		}

		this.log(lvl, msg);
		final int i = 0;
		for (final Object obj : objs) {
			if (objs[i] instanceof Throwable) {
				this._logException(i + 1, lvl, (Throwable) obj, false);
			} else {
				this._log(i + 1, lvl, obj.toString(), true);
			}
		}
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void log(final Level lvl, final Throwable thr, final String msg) {
		if (this.disabled) {
			return;
		}

		this.log(lvl, msg);
		this._log(0,
				lvl,
				thr.getClass().getName() + ": " + (thr.getLocalizedMessage() != null ? thr.getLocalizedMessage() : thr.getMessage()),
				true);

		final StackTraceElement[] el = thr.getStackTrace();
		for (int i = el.length - 1; i >= 0; i--) {
			this._log(i + 1, lvl, el[i].toString(), true);
		}

		if (thr.getCause() != null) {
			this._log(0, lvl, "Caused by: ", true);
			this.log(lvl, thr.getCause(), msg);
		}
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void log(final Object string) {
		if (this.disabled) {
			return;
		}

		this.log(Level.FINEST, string == null ? "null" : string.toString());
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void logRaw(final Level lvl, final String msg) {
		if (this.disabled) {
			return;
		}

		this._log(0, lvl, msg, true);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void removeCallerWhiteList(final String s) {
		this.callerWhiteList.remove(s);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void setCallerWhiteList(final List<String> callerWhiteList) {
		this.callerWhiteList = callerWhiteList;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void setForwardContent(final boolean forwardContent) {
		this.forwardContent = forwardContent;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public void setMinForwardLevel(final Level minForwardLevel) {
		this.minForwardLevel = minForwardLevel;
	}

	private void _log(final int depth, final Level lvl, final String msg, final boolean raw) {
		this._log(depth, lvl, msg, raw ? this.lineRawFormat : this.lineFormat);
	}

	private void _log(final int depth, final Level lvl, final String msg, final String lineRawFormat) {
		if (this.disabled) {
			return;
		}

		final String content = lineRawFormat.replace("%TIME%", this.sdf.format(Date.from(Instant.now())))
				.replace("%LEVEL%", lvl.toString())
				.replace("%CLASS%", this.getCallerClassName(false, false))
				.replace("%SIMPLECLASS%", this.getCallerClassName(false, true))
				.replace("%CURRENTMS%", System.currentTimeMillis() + "")
				.replace("%THREAD%", Thread.currentThread().getName())
				.replace("%MSG%", (depth > 0 ? this.indent(depth) : "") + msg);

		this.output.println(content);
		if (this.forwardContent && lvl.intValue() >= this.minForwardLevel.intValue()) {
			System.out.println(content);
		}
	}

	private void _logException(final int i, final Level lvl, final Throwable obj, final boolean cause) {
		if (cause) {
			this._log(i + 1, lvl, "Caused by: " + obj.getClass().getName() + ": " + obj.getMessage(), true);
		} else {
			this._log(i + 1, lvl, obj.getClass().getName() + ": " + obj.getLocalizedMessage(), true);
		}
		Arrays.stream(obj.getStackTrace()).map(StackTraceElement::toString).forEach(c -> this._log(i + 1, lvl, c, true));

		if (obj.getCause() != null) {
			this._logException(i + 1, lvl, obj.getCause(), false);
		}
	}

	private String indent(final int depth) {
		String s = "";
		for (int i = 0; i < Math.max(0, 5 - (depth + "").length()); i++) {
			s += " ";
		}
		return depth + s;
	}

}
