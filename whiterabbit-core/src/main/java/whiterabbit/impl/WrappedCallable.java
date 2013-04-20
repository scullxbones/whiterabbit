package whiterabbit.impl;

import java.util.concurrent.Callable;

import whiterabbit.Delay;
import whiterabbit.io.netty.util.HashedWheelTimer;

class WrappedCallable<T> implements Callable<T> {
	
	private final Callable<T> delegate;
	private final ThreadDumpTimerTask timerTask;
	private final Delay delay;
	private HashedWheelTimer timer;

	public WrappedCallable(Callable<T> delegate, ThreadDumpTimerTask timerTask, Delay delay, HashedWheelTimer timer)
	{
		this.delegate = delegate;
		this.timerTask = timerTask;
		this.delay = delay;
		this.timer = timer;
	}

	@Override
	public T call() throws Exception {
		timer.newTimeout(timerTask, delay.duration(), delay.unit());
		try
		{
			return delegate.call();
		}
		finally
		{
			timerTask.cancel();
		}
	}
	
}