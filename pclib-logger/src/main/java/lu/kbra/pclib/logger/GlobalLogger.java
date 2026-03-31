package lu.kbra.pclib.logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class GlobalLogger {

	private static PCLogger logger;

	public static boolean INIT_DEFAULT_IF_NOT_INITIALIZED = true;

	public static void initDefault() throws IOException {
		GlobalLogger.init((File) null);
	}

	/**
	 * @see lu.kbra.pclib.logger.PCLogger#PCLogger(File file)
	 */
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

	public static void close() {
		GlobalLogger.checkNull();
		GlobalLogger.logger.close();
		GlobalLogger.logger = null;
	}

	public static void log(final Level lvl, final Throwable thr, final String msg) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log(lvl, msg, thr);
	}

	public static void log(final Level lvl, final String msg) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log(lvl, msg);
	}

	public static void log(final Level lvl, final Object msg) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log(lvl, "", msg);
	}

	public static void logRaw(final Level lvl, final String msg) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.logRaw(lvl, msg);
	}

	public static void log(final Level lvl, final String msg, final Object... objs) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log(lvl, msg, objs);
	}

	public static void log(final Object obj) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log(obj);
	}

	public static void log(final Level lvl) {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log(lvl);
	}

	public static void log() {
		GlobalLogger.checkNull();
		GlobalLogger.logger.log();
	}

	public static PCLogger getLogger() {
		GlobalLogger.checkNull();
		return GlobalLogger.logger;
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

	public static void info(final Object msg) {
		GlobalLogger.log(Level.INFO, msg);
	}

	public static void severe(final Object msg) {
		GlobalLogger.log(Level.SEVERE, msg);
	}

	public static void warning(final Object msg) {
		GlobalLogger.log(Level.WARNING, msg);
	}

	public static void info(final String msg) {
		GlobalLogger.log(Level.INFO, msg);
	}

	public static void severe(final String msg) {
		GlobalLogger.log(Level.SEVERE, msg);
	}

	public static void warning(final String msg) {
		GlobalLogger.log(Level.WARNING, msg);
	}

	public static boolean isInit() {
		return GlobalLogger.logger != null && GlobalLogger.logger.isInit();
	}

	public static void severe(final String msg, final Object obj) {
		GlobalLogger.log(Level.SEVERE, msg, obj);
	}

}
