package whiterabbit;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReportContext {
	private final String name;
	private final Map<String,Object> context;
	private final List<StackTraceElement> list;
	private final Thread toDump; 
	private final Delay delay;
	
	public ReportContext(String name, Map<String,Object> context, 
			List<StackTraceElement> list,
			Thread toDump, Delay delay)
	{
		this.name = name;
		this.context = context;
		this.list = list;
		this.toDump = toDump;
		this.delay = delay;
	}

	public String getName() {
		return name;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public List<StackTraceElement> getStack() {
		return list;
	}

	public Thread getToDump() {
		return toDump;
	}

	public long getDelay() {
		return delay.duration();
	}

	public TimeUnit getUnit() {
		return delay.unit();
	}

}
