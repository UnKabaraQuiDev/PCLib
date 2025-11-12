package lu.pcy113.pclib.logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class GlobalLogger {

	private static PCLogger logger;

	public static boolean INIT_DEFAULT_IF_NOT_INITIALIZED = true;

	/**
	 * @see lu.pcy113.pclib.logger.PCLogger#PCLogger(File file)
	 */
	public static void init(File file) throws IOException {
		if (logger != null)
			throw new IllegalStateException("GlobalLogger already initialized");

		logger = new PCLogger(file);
		if (!logger.isInit())
			throw new IllegalStateException("Could not initialize GlobalLogger");
		logger.addCallerWhiteList(GlobalLogger.class.getName());
		log("Initialized GlobalLogger");
	}
	
	public static void init(String fileContent) throws IOException {
		if (logger != null)
			throw new IllegalStateException("GlobalLogger already initialized");

		logger = new PCLogger(fileContent);
		if (!logger.isInit())
			throw new IllegalStateException("Could not initialize GlobalLogger");
		logger.addCallerWhiteList(GlobalLogger.class.getName());
		log("Initialized GlobalLogger");
	}

	public static void close() {
		checkNull();
		logger.close();
		logger = null;
	}

	public static void log(Level lvl, Throwable thr, String msg) {
		checkNull();
		logger.log(lvl, msg, thr);
	}

	public static void log(Level lvl, String msg) {
		checkNull();
		logger.log(lvl, msg);
	}

	public static void log(Level lvl, Object msg) {
		checkNull();
		logger.log(lvl, "", msg);
	}

	public static void logRaw(Level lvl, String msg) {
		checkNull();
		logger.logRaw(lvl, msg);
	}

	public static void log(Level lvl, String msg, Object... objs) {
		checkNull();
		logger.log(lvl, msg, objs);
	}

	public static void log(Object obj) {
		checkNull();
		logger.log(obj);
	}

	public static void log(Level lvl) {
		checkNull();
		logger.log(lvl);
	}

	public static void log() {
		checkNull();
		logger.log();
	}

	public static PCLogger getLogger() {
		return logger;
	}

	private static void checkNull() {
		if (logger == null) {
			if (INIT_DEFAULT_IF_NOT_INITIALIZED) {
				try {
					init((File) null);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				throw new IllegalStateException("GlobalLogger not initialized");
			}
		}
	}

	public static void info(Object msg) {
		log(Level.INFO, msg);
	}

	public static void severe(Object msg) {
		log(Level.SEVERE, msg);
	}

	public static void warning(Object msg) {
		log(Level.WARNING, msg);
	}

	public static void info(String msg) {
		log(Level.INFO, msg);
	}

	public static void severe(String msg) {
		log(Level.SEVERE, msg);
	}

	public static void warning(String msg) {
		log(Level.WARNING, msg);
	}

	public static boolean isInit() {
		return logger != null && logger.isInit();
	}

	public static void severe(String msg, Object obj) {
		log(Level.SEVERE, msg, obj);
	}

}
