package whiterabbit;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import whiterabbit.Rabbit.Cancelable;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.List;

public class RabbitTest {

	private Rabbit rabbit;
	private TestReporter mockReporter = new TestReporter();

	public static class TestReporter implements Reporter {

		private CountDownLatch latch = new CountDownLatch(1);
		private Thread toDump;
		private long delay;
		private TimeUnit unit;

		public void reportTimeout(List<StackTraceElement> list, Thread toDump, long delay, TimeUnit unit)
		{
			this.toDump = toDump;
			this.delay = delay;
			this.unit = unit;
			latch.countDown();
		}

		public void await() throws InterruptedException
		{
			latch.await();
		}
	}

	@Before
	public void setUp()
	{
		rabbit = Rabbit.builder()
									 .withTickLength(10)
									 .reportingTo(mockReporter).buildAndStart();
	}

	@After
	public void tearDown()
	{
		rabbit.stop();
	}

	@Test
	public void testTimeout() throws Exception
	{
		rabbit.registerTimeout(50,TimeUnit.MILLISECONDS);
		mockReporter.await();
		assertEquals(Thread.currentThread(),mockReporter.toDump);
		assertEquals(50L,mockReporter.delay);
		assertEquals(TimeUnit.MILLISECONDS,mockReporter.unit);
	}
	
	@Test
	public void testCancel() throws Exception
	{
		Cancelable to = rabbit.registerTimeout(50,TimeUnit.MILLISECONDS);
		to.cancel();
		Thread.sleep(50);
		assertEquals(1,mockReporter.latch.getCount());
	}
	
}