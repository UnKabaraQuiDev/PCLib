package lu.pcy113.pclib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

public final class GlobalLogger {

	private static PCLogger logger;
	
	public static void init(File file) throws FileNotFoundException, IOException {
		logger = new PCLogger(file);
	}
	public static void close() {
		logger.close();
	}
	
	public static void log(Level lvl, Throwable thr, String msg) {
		logger.log(lvl, msg, thr);
	}
	
	public static void log(Level lvl, String msg) {
		logger.log(lvl, msg);
	}
	
	public static void log(Level lvl, String msg, Object... objs) {
		logger.log(lvl, msg, objs);
	}
	
	public static void log(Object obj) {
		logger.log(obj);
	}
	public static void log() {
		logger.log();
	}
	
	public static PCLogger getLogger() {return logger;}

}
