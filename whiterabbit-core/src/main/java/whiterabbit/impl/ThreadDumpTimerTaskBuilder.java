package whiterabbit.impl;

import java.util.Map;

import whiterabbit.Cancelable;
import whiterabbit.Cancelable.Builder;
import whiterabbit.Delay;
import whiterabbit.Reporter;
import whiterabbit.io.netty.util.HashedWheelTimer;

public class ThreadDumpTimerTaskBuilder implements Builder {
	
	private String name;
	private Delay delay;
	private Map<String,Object> context;
	
	private final HashedWheelTimer timer;
	private final Iterable<Reporter> reporters;
	
	ThreadDumpTimerTaskBuilder(Iterable<Reporter> reporters, HashedWheelTimer timer) {
		this.reporters = reporters;
		this.timer = timer;
	}

	@Override
	public Builder timeout(Delay delay) {
		this.delay = delay;
		return this;
	}

	@Override
	public Builder named(String name) {
		this.name = name;
		return this;
	}

	@Override
	public Builder extraInfo(Map<String, Object> context) {
		this.context = context;
		return this;
	}

	@Override
	public Cancelable build() {
		ThreadDumpTimerTask task = 
				new ThreadDumpTimerTask(name,context,Thread.currentThread(), delay, reporters);
		timer.newTimeout(task, delay.duration(), delay.unit());
		return new CancelableHandle(task);
	}

}
