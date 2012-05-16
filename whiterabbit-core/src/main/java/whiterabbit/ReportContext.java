package whiterabbit;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReportContext {
	private final String name;
	private final Map<String,Object> context;
	private final List<StackTraceElement> list;
	private final Thread toDump; 
	private final long delay;
	private final TimeUnit unit;
	
	public ReportContext(String name, Map<String,Object> context, 
			List<StackTraceElement> list,
			Thread toDump, long delay, TimeUnit unit)
	{
		this.name = name;
		this.context = context;
		this.list = list;
		this.toDump = toDump;
		this.delay = delay;
		this.unit = unit;
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
		return delay;
	}

	public TimeUnit getUnit() {
		return unit;
	}

}
