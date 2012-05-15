package whiterabbit.guice;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import whiterabbit.Rabbit;
import whiterabbit.RabbitTest;
import whiterabbit.TestReporter;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class InterceptorTest {

	private Injector injector;
	private Rabbit rabbit;
	private final TestReporter testReporter = new TestReporter();
	
	private Module rabbitMod;
	private Module testModule =  new AbstractModule() {
		
		@Override
		protected void configure() {
			bind(IService.class).to(TestService.class);
		}
	};
	
	@Before
 	public void setUp()
	{
		rabbit = Rabbit.builder().reportingTo(testReporter).withTickLength(50).buildAndStart();
		rabbitMod = new RabbitModule(rabbit,100,TimeUnit.MILLISECONDS);
	}
	
	@After
	public void tearDown()
	{
		rabbit.stop();
	}
	
	@Test
	public void test75Millis() throws InterruptedException
	{
		injector = Guice.createInjector(rabbitMod, testModule);
		IService service = injector.getInstance(TestService.class);
		service.setMillisSleep(75);
		service.invoke();
		assertThat(testReporter.await(150),is(false));
	}
	
	
	@Test
	public void test150Millis() throws InterruptedException
	{
		injector = Guice.createInjector(rabbitMod, testModule);
		IService service = injector.getInstance(TestService.class);
		service.setMillisSleep(150);
		service.invoke();
		assertThat(testReporter.await(150),is(true));
		RabbitTest.assertReportWasCalled(testReporter, 100);
	}
}
