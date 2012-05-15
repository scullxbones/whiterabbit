package whiterabbit;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestReporter implements Reporter {

	public final CountDownLatch latch = new CountDownLatch(1);
	public Thread toDump;
	public long delay;
	public TimeUnit unit;

	public void reportTimeout(List<StackTraceElement> list, Thread toDump, long delay, TimeUnit unit)
	{
		this.toDump = toDump;
		this.delay = delay;
		this.unit = unit;
		latch.countDown();
	}

	public boolean await(long maxTimeInMillis) throws InterruptedException
	{
		return latch.await(maxTimeInMillis, TimeUnit.MILLISECONDS);
	}
}