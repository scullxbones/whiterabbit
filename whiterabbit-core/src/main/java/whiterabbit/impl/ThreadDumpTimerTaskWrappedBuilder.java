package whiterabbit.impl;

import java.util.Map;
import java.util.concurrent.Callable;

import whiterabbit.Delay;
import whiterabbit.Rabbit.WrappedBuilder;
import whiterabbit.Reporter;
import whiterabbit.io.netty.util.HashedWheelTimer;

public class ThreadDumpTimerTaskWrappedBuilder<T> implements WrappedBuilder<T> {
	
	private final Iterable<Reporter> reporters;
	private final HashedWheelTimer timer;
	private final Callable<T> callable;

	ThreadDumpTimerTaskWrappedBuilder(Callable<T> callable, Iterable<Reporter> reporters, HashedWheelTimer timer) {
		this.callable = callable;
		this.reporters = reporters;
		this.timer = timer;
	}
	
	private Delay delay;
	private String name;
	private Map<String,Object> context;

	@Override
	public WrappedBuilder<T> timeout(Delay delay) {
		this.delay = delay;
		return this;
	}

	@Override
	public WrappedBuilder<T> named(String name) {
		this.name = name;
		return this;
	}

	@Override
	public WrappedBuilder<T> extraInfo(Map<String, Object> context) {
		this.context = context;
		return this;
	}

	@Override
	public Callable<T> build() {
		ThreadDumpTimerTask task = 
				new ThreadDumpTimerTask(name, context, Thread.currentThread(), delay, reporters);
		timer.newTimeout(task, delay.duration(), delay.unit());
		return new WrappedCallable<T>(callable, task, delay, timer);
	}

}
