package whiterabbit;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.List;

public class RabbitTest {

	private Rabbit rabbit;
	private TestReporter mockReporter = new TestReporter();

	public static class TestReporter implements Reporter {

		private CountDownLatch latch = new CountDownLatch(1);
		private List<StackTraceElement> list;
		private Thread toDump;
		private long delay;
		private TimeUnit unit;

		public void reportTimeout(List<StackTraceElement> list, Thread toDump, long delay, TimeUnit unit)
		{
			this.list = list;
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
		rabbit = Rabbit.builder().reportingTo(mockReporter).buildAndStart();
	}

	@After
	public void tearDown()
	{
		rabbit.stop();
	}

	@Test
	public void testTimeout() throws Exception
	{
		rabbit.registerTimeout(100,TimeUnit.MILLISECONDS);
		mockReporter.await();
		assertEquals(Thread.currentThread(),mockReporter.toDump);
		assertEquals(100L,mockReporter.delay);
		assertEquals(TimeUnit.MILLISECONDS,mockReporter.unit);
	}
	
}