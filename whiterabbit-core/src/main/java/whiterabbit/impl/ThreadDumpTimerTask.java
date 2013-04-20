package whiterabbit.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import whiterabbit.Cancelable;
import whiterabbit.Delay;
import whiterabbit.ReportContext;
import whiterabbit.Reporter;
import whiterabbit.io.netty.util.Timeout;
import whiterabbit.io.netty.util.TimerTask;

class ThreadDumpTimerTask implements TimerTask, Cancelable
{
	private final Thread toDump;
	private final Delay delay;
	private final AtomicBoolean cancelled = new AtomicBoolean(false);
	private final Map<String, Object> context;
	private final String name;
	private final Iterable<Reporter> reporters;
	private final AtomicBoolean timedOut = new AtomicBoolean(false);

	public ThreadDumpTimerTask(String name, Map<String,Object> context, Thread toDump, Delay delay, Iterable<Reporter> reporters)
	{
		this.name = name;
		this.context = context;
		this.toDump = toDump;
		this.delay = delay;
		this.reporters = reporters;
	}

	@Override
	public void run(Timeout timeout) throws Exception
	{
		if (timeout.isExpired() && !cancelled.get())
		{
			timedOut.set(true);
			ReportContext ctx = 
					new ReportContext(name,context,Arrays.asList(toDump.getStackTrace()), toDump, delay);
			for(Reporter reporter : reporters)
				reporter.reportTimeout(ctx);
		}
	}

	@Override
	public void cancel() {
		if (!timedOut.get()) {
			cancelled.set(true);
			ReportContext ctx = 
					new ReportContext(name,context,Arrays.asList(toDump.getStackTrace()), toDump, delay);
			for(Reporter reporter : reporters)
				reporter.reportCancellation(ctx);
		}
	}
	
}