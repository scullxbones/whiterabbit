package whiterabbit;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestReporter implements Reporter {

	public final CountDownLatch latch = new CountDownLatch(1);
	public Thread toDump;
	public long delay;
	public TimeUnit unit;

	@Override
	public void reportTimeout(ReportContext context)
	{
		this.toDump = context.getToDump();
		this.delay = context.getDelay();
		this.unit = context.getUnit();
		latch.countDown();
	}

	@Override
	public void configure(Map<String, String> parameters) {
		// Nothing to do
	}
	
	public boolean await(long maxTimeInMillis) throws InterruptedException
	{
		return latch.await(maxTimeInMillis, TimeUnit.MILLISECONDS);
	}

}