
public class Log {
	private final String mSep = " | ";
	
	private String mClass;
	private String mMethod;
	
	
	public Log(Class c){
		mClass = c.getName();
	}
	
	public void entry(String method){
		mMethod = method;
	}
	
	public void exit(){
		mMethod = null;
	}
	
	protected String getPrefix(){
		return getTime() + mSep + mClass + mSep + mMethod + mSep;
	}
	
	protected String getTime(){
		return String.valueOf(System.currentTimeMillis());
	}
	
	public void info(String m){
		String info = getPrefix()+"info"+mSep+m;
		System.out.println(info);
	}
	
	public void debug(String m){
		String debug = getPrefix()+"debug"+mSep+m;
		System.out.println(debug);
	}
	
	public void error(String m){
		String error = getPrefix()+"error"+mSep+m;
		System.err.println(error);
	}
}
