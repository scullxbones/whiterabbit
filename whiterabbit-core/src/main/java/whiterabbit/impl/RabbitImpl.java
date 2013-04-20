package whiterabbit.impl;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import whiterabbit.Cancelable;
import whiterabbit.Rabbit;
import whiterabbit.Reporter;
import whiterabbit.io.netty.util.HashedWheelTimer;

public class RabbitImpl implements Rabbit {
	
	private static final AtomicReference<RabbitImpl> instanceRef = new AtomicReference<RabbitImpl>();

	private final Set<Reporter> reporters;
	private final HashedWheelTimer timer;
	
	public static final Builder builder() {
		return new RabbitImplBuilder();
	}
	
	RabbitImpl(HashedWheelTimer timer, Set<Reporter> reporters)
	{
		this.timer = timer;
		this.reporters = reporters;
	}
	
	RabbitImpl start()
	{
		timer.start();
		RabbitImpl oldInstance = instanceRef.getAndSet(this);
		if (oldInstance != null)
			oldInstance.stop();
		return this;
	}
	
	public RabbitImpl stop()
	{
		timer.stop();
		return this;
	}
	
	public Cancelable.Builder register() {
		return new ThreadDumpTimerTaskBuilder(reporters,timer);
	}
	
	public <T> WrappedBuilder<T> wrap(Callable<T> callable) {
		return new ThreadDumpTimerTaskWrappedBuilder<T>(callable, reporters, timer);
	}

}
