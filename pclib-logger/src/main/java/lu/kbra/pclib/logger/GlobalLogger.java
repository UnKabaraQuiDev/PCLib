package lu.kbra.pclib.logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @deprecated use a dedicated library like SLF4J instead
 */
@Deprecated(forRemoval = true, since = "v1.1.0")
public final class GlobalLogger {

	private static PCLogger logger;

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static boolean INIT_DEFAULT_IF_NOT_INITIALIZED = true;

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void close() {
		GlobalLogger.checkNull();
		GlobalLogger.logger.close();
		GlobalLogger.logger = null;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static PCLogger getLogger() {
		GlobalLogger.checkNull();
		return GlobalLogger.logger;
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void info(final Object msg) {
		GlobalLogger.log(Level.INFO, msg);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void info(final String msg) {
		GlobalLogger.log(Level.INFO, msg);
	}

	/**
	 * @see lu.kbra.pclib.logger.PCLogger#PCLogger(File file)
	 */
	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void init(final File file) throws IOException {
		if (GlobalLogger.logger != null) {
			throw new IllegalStateException("GlobalLogger already initialized");
		}

		GlobalLogger.logger = new PCLogger(file);
		if (!GlobalLogger.logger.isInit()) {
			throw new IllegalStateException("Could not initialize GlobalLogger");
		}
		GlobalLogger.logger.addCallerWhiteList(GlobalLogger.class.getName());
		GlobalLogger.log("Initialized GlobalLogger");
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void init(final String fileContent) throws IOException {
		if (GlobalLogger.logger != null) {
			throw new IllegalStateException("GlobalLogger already initialized");
		}

		GlobalLogger.logger = new PCLogger(fileContent);
		if (!GlobalLogger.logger.isInit()) {
			throw new IllegalStateException("Could not initialize GlobalLogger");
		}
		GlobalLogger.logger.addCallerWhiteList(GlobalLogger.class.getName());
		GlobalLogger.log("Initialized GlobalLogger");
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void initDefault() throws IOException {
		GlobalLogger.init((File) null);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static boolean isInit() {
		return GlobalLogger.logger != null && GlobalLogger.logger.isInit();
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void log() {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log();
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void log(final Level lvl) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log(lvl);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void log(final Level lvl, final Object msg) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log(lvl, "", msg);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void log(final Level lvl, final String msg) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log(lvl, msg);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void log(final Level lvl, final String msg, final Object... objs) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log(lvl, msg, objs);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void log(final Level lvl, final Throwable thr, final String msg) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log(lvl, msg, thr);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void log(final Object obj) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log(obj);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void logRaw(final Level lvl, final String msg) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.logRaw(lvl, msg);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void severe(final Object msg) {
		GlobalLogger.log(Level.SEVERE, msg);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void severe(final String msg) {
		GlobalLogger.log(Level.SEVERE, msg);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void severe(final String msg, final Object obj) {
		GlobalLogger.log(Level.SEVERE, msg, obj);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void warning(final Object msg) {
		GlobalLogger.log(Level.WARNING, msg);
	}

	@Deprecated(forRemoval = true, since = "v1.1.0")
	public static void warning(final String msg) {
		GlobalLogger.log(Level.WARNING, msg);
	}

	private static void checkNull() {
		if (GlobalLogger.logger == null) {
			if (GlobalLogger.INIT_DEFAULT_IF_NOT_INITIALIZED) {
				try {
					GlobalLogger.init((File) null);
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				throw new IllegalStateException("GlobalLogger not initialized");
			}
		}
	}

}
