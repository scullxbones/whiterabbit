package whiterabbit.guice;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import whiterabbit.Delay;
import whiterabbit.Rabbit;
import whiterabbit.ReportContext;
import whiterabbit.Reporter;
import whiterabbit.impl.RabbitImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class InterceptorTest {

	private Injector injector;
	private Rabbit rabbit;
	
	private Reporter testReporter = mock(Reporter.class);
	
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
		rabbit = RabbitImpl.builder().reportingTo(testReporter).tick(50).buildAndStart();
		rabbitMod = new RabbitModule(rabbit,Delay.millis(100));
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
		Thread.sleep(150);
		verify(testReporter).reportCancellation(any(ReportContext.class));
		verify(testReporter,never()).reportTimeout(any(ReportContext.class));
		//assertThat(testReporter.await(150),is(false));
	}
	
	
	@Test
	public void test150Millis() throws InterruptedException
	{
		injector = Guice.createInjector(rabbitMod, testModule);
		IService service = injector.getInstance(TestService.class);
		service.setMillisSleep(150);
		service.invoke();
		Thread.sleep(150);
		verify(testReporter).reportTimeout(any(ReportContext.class));
		verify(testReporter,never()).reportCancellation(any(ReportContext.class));
		//assertThat(testReporter.await(150),is(true));
		//RabbitTest.assertReportWasCalled(testReporter, 100);
	}
}
