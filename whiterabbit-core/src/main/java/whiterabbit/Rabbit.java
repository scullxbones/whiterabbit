package whiterabbit;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import whiterabbit.io.netty.util.HashedWheelTimer;
import whiterabbit.io.netty.util.Timeout;
import whiterabbit.io.netty.util.TimerTask;

public class Rabbit {
	
	private static final long DEFAULT_TICK_DURATION = 100;
	private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
	private static final int DEFAULT_WHEEL_SIZE = 512;
	
	private final Reporter reporter;
	
	public interface Builder 
	{
		Builder withTicks(long tick);
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
		public Builder withTicks(long tick) {
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
			return new Rabbit(new HashedWheelTimer(factory,tickDuration,unit,wheelSize), reporter).start();
		}
		
	}
	
	public static final Builder builder() {
		return new InternalBuilder();
	}
	
	
	private final HashedWheelTimer timer;
	
	private Rabbit(HashedWheelTimer timer, Reporter reporter)
	{
		this.timer = timer;
		this.reporter = reporter;
	}
	
	public Rabbit start()
	{
		timer.start();
		return this;
	}
	
	public Rabbit stop()
	{
		timer.stop();
		return this;
	}
	
	public Rabbit registerTimeout(long delay, TimeUnit unit)
	{
		timer.newTimeout(new ThreadDumpTimerTask(Thread.currentThread(), delay, unit), delay, unit);
		return this;
	}
	
	private class ThreadDumpTimerTask implements TimerTask 
	{
		
		private final Thread toDump;
		private final long delay;
		private final TimeUnit unit;

		public ThreadDumpTimerTask(Thread toDump, long delay, TimeUnit unit)
		{
			this.toDump = toDump;
			this.delay = delay;
			this.unit = unit;
		}

		@Override
		public void run(Timeout timeout) throws Exception
		{
			if (timeout.isExpired())
			{
				reporter.reportTimeout(Arrays.asList(toDump.getStackTrace()), toDump, delay, unit);
			}
		}
		
	}

}
