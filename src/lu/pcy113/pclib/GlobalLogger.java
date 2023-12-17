package lu.pcy113.pclib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

public final class GlobalLogger {

	private static PCLogger logger;

	public static void init(File file) throws FileNotFoundException, IOException {
		if (logger != null)
			throw new IllegalStateException("GlobalLogger already initialized");

		logger = new PCLogger(file);
		logger.addCallerWhiteList(GlobalLogger.class.getName());
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

	public static void log(Level lvl, String msg, Object... objs) {
		checkNull();
		logger.log(lvl, msg, objs);
	}

	public static void log(Object obj) {
		checkNull();
		logger.log(obj);
	}

	public static void log() {
		checkNull();
		logger.log();
	}

	public static PCLogger getLogger() {
		return logger;
	}

	private static void checkNull() {
		if (logger == null)
			throw new IllegalStateException("GlobalLogger not initialized");
	}

}
