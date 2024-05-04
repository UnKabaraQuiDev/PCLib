package lu.pcy113.pclib;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

public class PCLogger implements Closeable {

	private boolean init, disabled = false, forwardContent = true;
	private Level minForwardLevel = Level.FINEST;
	private Properties config;
	private File logFile;
	private PrintWriter output;
	private SimpleDateFormat sdf;
	private String lineFormat, lineRawFormat;
	private List<String> callerWhiteList = new ArrayList<String>();

	public PCLogger(File file) throws FileNotFoundException, IOException {
		callerWhiteList.add(this.getClass().getName());

		config = new Properties();
		config.load(new FileReader(file));

		String dateFormat = config.getProperty("date.format", "dd-MM-yyyy HH:mm:ss");
		sdf = new SimpleDateFormat(dateFormat);

		SimpleDateFormat fdf = new SimpleDateFormat(config.getProperty("file.time.format", "dd-MM-yyyy HH-mm-ss"));

		String format = config.getProperty("file.format", "./logs/log-%CURRENTMS%.txt").replace("%CURRENTMS%", System.currentTimeMillis() + "").replace("%TIME%", fdf.format(Date.from(Instant.now())));

		logFile = new File(format);
		if (!logFile.getParentFile().exists())
			logFile.getParentFile().mkdirs();
		if (!logFile.exists())
			logFile.createNewFile();

		lineFormat = config.getProperty("line.format", "[%TIME%][%LEVEL%](%CLASS%) %MSG%");
		lineRawFormat = config.getProperty("line.rawformat", "[%TIME%][%LEVEL%] %MSG%");

		forwardContent = Boolean.parseBoolean(config.getProperty("sysout.forward", "true"));

		output = new PrintWriter(new FileOutputStream(logFile), true);
		init = true;
	}

	public void log(Level lvl, Throwable thr, String msg) {
		if (disabled)
			return;

		log(lvl, msg);
		_log(0, lvl, thr.getClass().getName() + ": " + (thr.getLocalizedMessage() != null ? thr.getLocalizedMessage() : thr.getMessage()), true);

		StackTraceElement[] el = thr.getStackTrace();
		for (int i = el.length - 1; i >= 0; i--) {
			_log(i + 1, lvl, el[i].toString(), true);
		}

		if (thr.getCause() != null) {
			_log(0, lvl, "Caused by: ", true);
			log(lvl, thr.getCause(), msg);
		}
	}

	public void log(Level lvl, String msg) {
		if (disabled)
			return;

		_log(0, lvl, msg, false);
	}

	public void log(Level lvl, String msg, Object... objs) {
		if (disabled)
			return;

		log(lvl, msg);
		for (int i = 0; i < objs.length; i++) {
			_log(i + 1, lvl, objs[i].toString(), true);
		}
	}

	private void _log(int depth, Level lvl, String msg, boolean raw) {
		if (disabled)
			return;

		String content = null;
		if (raw)
			content = (lineRawFormat.replace("%TIME%", sdf.format(Date.from(Instant.now()))).replace("%LEVEL%", lvl.toString()).replace("%CLASS%", getCallerClassName(false)).replace("%CURRENTMS%", System.currentTimeMillis() + "").replace("%MSG%",
					(depth > 0 ? indent(depth) : "") + msg));
		else
			content = (lineFormat.replace("%TIME%", sdf.format(Date.from(Instant.now()))).replace("%LEVEL%", lvl.toString()).replace("%CLASS%", getCallerClassName(false)).replace("%CURRENTMS%", System.currentTimeMillis() + "").replace("%MSG%",
					(depth > 0 ? indent(depth) : "") + msg));

		output.println(content);
		if (forwardContent && lvl.intValue() >= minForwardLevel.intValue()) {
			System.out.println(content);
		}
	}

	private String indent(int depth) {
		String s = "";
		for (int i = 0; i < Math.max(0, 5 - (depth + "").length()); i++)
			s += " ";
		return depth + s;
	}

	public String getCallerClassName(boolean parent) {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < stElements.length; i++) {
			StackTraceElement ste = stElements[i];
			if (!callerWhiteList.contains(ste.getClassName())/* && ste.getClassName().indexOf("java.lang.Thread")!=0 */) {
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

	@Override
	public void close() {
		if (init) {
			output.flush();
			output.close();
			// output.closeSecondary();

			logFile = null;
			init = false;
		}
	}

	public void log(Object string) {
		if (disabled)
			return;

		log(Level.FINEST, string == null ? "null" : string.toString());
	}

	public void log() {
		if (disabled)
			return;

		log(Level.INFO, "<- " + getCallerClassName(true));
	}

	public List<String> getCallerWhiteList() {
		return callerWhiteList;
	}

	public void setCallerWhiteList(List<String> callerWhiteList) {
		this.callerWhiteList = callerWhiteList;
	}

	public void addCallerWhiteList(String s) {
		this.callerWhiteList.add(s);
	}

	public void removeCallerWhiteList(String s) {
		this.callerWhiteList.remove(s);
	}

	public boolean isInit() {
		return init;
	}

	public File getLogFile() {
		return logFile;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isForwardContent() {
		return forwardContent;
	}

	public void setForwardContent(boolean forwardContent) {
		this.forwardContent = forwardContent;
	}

	public Level getMinForwardLevel() {
		return minForwardLevel;
	}

	public void setMinForwardLevel(Level minForwardLevel) {
		this.minForwardLevel = minForwardLevel;
	}

}
