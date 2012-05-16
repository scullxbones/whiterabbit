package whiterabbit;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import whiterabbit.io.netty.util.HashedWheelTimer;
import whiterabbit.io.netty.util.Timeout;
import whiterabbit.io.netty.util.TimerTask;

public class Rabbit {
	
	private static final long DEFAULT_TICK_DURATION = 100;
	private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
	private static final int DEFAULT_WHEEL_SIZE = 512;
	
	private final Reporter reporter;
	
	private static final Builder builder = new InternalBuilder();
	private static final AtomicReference<Rabbit> instanceRef = new AtomicReference<Rabbit>();
	private final HashedWheelTimer timer;
	
	public interface Builder 
	{
		Builder withTickLength(long tick);
		Builder ofUnit(TimeUnit unit);
		Builder withSize(int wheelSize);
		Builder usingThreadFactory(ThreadFactory factory);
		Builder reportingTo(Reporter reporter);
		Rabbit buildAndStart();
	}
	
	private static class InternalBuilder implements Builder {
		
		private long tickDuration = DEFAULT_TICK_DURATION;
		private TimeUnit unit = DEFAULT_TIME_UNIT;
		private int wheelSize = DEFAULT_WHEEL_SIZE;
		private ThreadFactory factory = Executors.defaultThreadFactory();
		private Reporter reporter;
		
		private InternalBuilder() {}

		@Override
		public Builder withTickLength(long tick) {
			this.tickDuration = tick;
			return this;
		}

		@Override
		public Builder ofUnit(TimeUnit unit) {
			this.unit = unit;
			return this;
		}

		@Override
		public Builder withSize(int wheelSize) {
			this.wheelSize = wheelSize;
			return this;
		}

		@Override
		public Builder usingThreadFactory(ThreadFactory factory) {
			this.factory = factory;
			return this;
		}
		
		@Override
		public Builder reportingTo(Reporter reporter) {
			this.reporter = reporter;
			return this;
		}
		
		@Override
		public Rabbit buildAndStart() {
			Rabbit instance = 
					new Rabbit(
							new HashedWheelTimer(factory,tickDuration,unit,wheelSize), reporter)
								.start();
			Rabbit oldInstance = instanceRef.getAndSet(instance);
			if (oldInstance != null)
				oldInstance.stop();
			return instance;
		}
		
	}
	
	public static final Builder builder() {
		return builder;
	}
	
	private Rabbit(HashedWheelTimer timer, Reporter reporter)
	{
		this.timer = timer;
		this.reporter = reporter;
	}
	
	private Rabbit start()
	{
		timer.start();
		return this;
	}
	
	public Rabbit stop()
	{
		timer.stop();
		return this;
	}
	
	public interface Cancelable {
		void cancel();
	}
	
	public Cancelable registerTimeout(long delay, TimeUnit unit)
	{
		ThreadDumpTimerTask task = 
				new ThreadDumpTimerTask(null,null,Thread.currentThread(), delay, unit);
		timer.newTimeout(task, delay, unit);
		return new CancelableHandle(task);
	}
	
	public Cancelable registerTimeoutWithName(String name, long delay, TimeUnit unit)
	{
		ThreadDumpTimerTask task = 
				new ThreadDumpTimerTask(name,null,Thread.currentThread(), delay, unit);
		timer.newTimeout(task, delay, unit);
		return new CancelableHandle(task);
	}
	
	public Cancelable registerTimeoutWithContext(String name, Map<String,Object> context, long delay, TimeUnit unit)
	{
		ThreadDumpTimerTask task = 
				new ThreadDumpTimerTask(name, context, Thread.currentThread(), delay, unit);
		timer.newTimeout(task, delay, unit);
		return new CancelableHandle(task);
	}
	
	private class CancelableHandle implements Cancelable {
		private final Cancelable delegate;

		private CancelableHandle(Cancelable delegate)
		{
			this.delegate = delegate;
		}

		@Override
		public void cancel() {
			delegate.cancel();
		}
	}
	
	private class ThreadDumpTimerTask implements TimerTask, Cancelable
	{
		private final Thread toDump;
		private final long delay;
		private final TimeUnit unit;
		private final AtomicBoolean cancelled = new AtomicBoolean(false);
		private final Map<String, Object> context;
		private final String name;

		public ThreadDumpTimerTask(String name, Map<String,Object> context, Thread toDump, long delay, TimeUnit unit)
		{
			this.name = name;
			this.context = context;
			this.toDump = toDump;
			this.delay = delay;
			this.unit = unit;
		}

		@Override
		public void run(Timeout timeout) throws Exception
		{
			if (timeout.isExpired() && !cancelled.get())
			{
				ReportContext ctx = 
						new ReportContext(name,context,Arrays.asList(toDump.getStackTrace()), toDump, delay, unit);
				reporter.reportTimeout(ctx);
			}
		}

		@Override
		public void cancel() {
			cancelled.set(true);
		}
		
	}

}
