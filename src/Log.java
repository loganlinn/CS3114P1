import java.io.PrintStream;

/**
 * 
 * @author loganlinn
 * 
 */
public class Log {
	private final String separator = " | ";
	private String className;
	private String methodName;
	private PrintStream outStream = System.out;
	private PrintStream errorStream = System.err;
	private String messagePrefix;

	public Log(Class c) {
		setClassName(c.getName());
	}

	public void entry(String method) {
		setMethodName(method);
		updateMessagePrefix();
	}

	public void exit() {
		setMethodName(null);
		updateMessagePrefix();
	}

	protected void updateMessagePrefix() {
		setMessagePrefix(getTime() + getSeparator() + className
				+ getSeparator() + methodName + separator);
	}

	protected String getTime() {
		return String.valueOf(System.currentTimeMillis());
	}

	public void info(String m) {
		String info = getMessagePrefix() + "info" + getSeparator() + m;
		outStream.println(info);
	}

	public void debug(String m) {
		String debug = getMessagePrefix() + "debug" + getSeparator() + m;
		outStream.println(debug);
	}

	public void warn(String m) {
		String warn = getMessagePrefix() + "warn" + getSeparator() + m;
		outStream.println(warn);
	}

	public void error(String m) {
		String error = getMessagePrefix() + "error" + getSeparator() + m;
		errorStream.println(error);
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName
	 *            the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the outStream
	 */
	public PrintStream getOutStream() {
		return outStream;
	}

	/**
	 * @param outStream
	 *            the outStream to set
	 */
	public void setOutStream(PrintStream outStream) {
		this.outStream = outStream;
	}

	/**
	 * @return the errorStream
	 */
	public PrintStream getErrorStream() {
		return errorStream;
	}

	/**
	 * @param errorStream
	 *            the errorStream to set
	 */
	public void setErrorStream(PrintStream errorStream) {
		this.errorStream = errorStream;
	}

	/**
	 * @return the messagePrefix
	 */
	public String getMessagePrefix() {
		return messagePrefix;
	}

	/**
	 * @param messagePrefix
	 *            the messagePrefix to set
	 */
	public void setMessagePrefix(String messagePrefix) {
		this.messagePrefix = messagePrefix;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}
}
