package whiterabbit;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import whiterabbit.Rabbit.Cancelable;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.concurrent.TimeUnit;

public class RabbitTest {

	private Rabbit rabbit;
	private TestReporter mockReporter = new TestReporter();

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
		Cancelable to = rabbit.registerTimeout(50,TimeUnit.MILLISECONDS);
		to.cancel();
		Thread.sleep(50);
		assertEquals(1,mockReporter.latch.getCount());
	}
	
}