package whiterabbit;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import whiterabbit.impl.RabbitImpl;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class RabbitTest {

	private Rabbit rabbit;
	private TestReporter mockReporter = new TestReporter();

	@Before
	public void setUp()
	{
		rabbit = RabbitImpl.builder()
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
		rabbit.register().timeout(Delay.millis(50)).build();
		assertThat(mockReporter.await(100),is(true));
		assertReportWasCalled(mockReporter,50);
	}

	public static void assertReportWasCalled(TestReporter reporter, long delay) {
		assertEquals(Thread.currentThread(),reporter.toDump);
		assertEquals(delay,reporter.delay);
		assertEquals(TimeUnit.MILLISECONDS,reporter.unit);
	}
	
	@Test
	public void testCancel() throws Exception
	{
		Cancelable to = rabbit.register().timeout(Delay.millis(50)).build();
		to.cancel();
		Thread.sleep(50);
		assertEquals(1,mockReporter.latch.getCount());
	}
	
	public class Sleeper implements Callable<Long> {
		
		private final long howLong;

		public Sleeper(long howLong)
		{
			this.howLong = howLong;
		}

		@Override
		public Long call() throws Exception {
			Thread.sleep(howLong);
			return howLong;
		}
		
	}
	
	@Test
	public void testCallWithTimeout() throws Exception
	{
		rabbit.wrap(new Sleeper(100)).timeout(Delay.millis(50)).build().call();
		assertReportWasCalled(mockReporter,50);
	}
	
}