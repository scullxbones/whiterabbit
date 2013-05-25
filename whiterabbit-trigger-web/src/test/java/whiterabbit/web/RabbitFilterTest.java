package whiterabbit.web;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import whiterabbit.Delay;
import whiterabbit.Rabbit;
import whiterabbit.ReportContext;

@RunWith(MockitoJUnitRunner.class)
public class RabbitFilterTest {

	private RabbitFilter underTest;
	
	@Mock private FilterConfig config;
	@Mock private ServletContext context;
	@Mock private HttpServletRequest request;
	@Mock private HttpServletResponse response;
	@Mock private Rabbit rabbit;
	
	private TimeoutFilterChain chain;
	private FilterTestReporter reporter = new FilterTestReporter();
	
	@Before
	public void setUp() {
		when(config.getServletContext()).thenReturn(context);
		when(request.getRequestURI()).thenReturn("/test");
	}
	
	@After
	public void tearDown()
	{
		reporter.reset();
	}
	
	@Test
	public void correctlyInitializesInAbsenceOfAnyConfiguration() {
		underTest = new RabbitFilter();
		runFilter(50,100);
	}

	@Test
	public void correctlyInitializesUsingListenerConfiguration() {
		
	}
	

	private void setupTimeout(long timeout) {
		// initParams.put(RabbitunderTest.TIMEOUT_INIT_PARAMETER, Long.toString(timeout));
		underTest.setDefaultDelay(Delay.millis(timeout));
	}
	
	private void setupReporter() {
		// initParams.put(RabbitunderTest.REPORTER_TYPE_PARAMETER, "whiterabbit.web.FilterTestReporter");
		// initParams.put(RabbitunderTest.REPORTER_CONFIG_PARAMETER, "{ \"name\" : \"value\" }");
	}
	
	private void runFilter(long configuredTimeout, long actualTimeout) {
		setupTimeout(configuredTimeout);
		setupReporter();
		underTest.init(config);
		chain = new TimeoutFilterChain(underTest,actualTimeout,request,response);
		chain.run();
	}
	
	private void verifyConfiguration() {
		Map<String, String> next = reporter.collectedConfigurations.iterator().next();
		assertEquals("name",next.keySet().iterator().next());
		assertEquals("value",next.get("name"));
	}

	@Test
	public void testCorrectInit() {
		try {
			underTest.init(config);
			fail("Illegal Argument Exception should be thrown");
		}
		catch(IllegalStateException e) {
			// pass
		}
	}
	
	@Test
	public void testDoFilterWithTimeout() {
		runFilter(50,100);
		verifyConfiguration();
		ReportContext next = reporter.collectedTimeouts.iterator().next();
		assertEquals(50,next.getDelay());
	}

	@Test
	public void testDoFilterWithoutTimeout() {
		runFilter(50,10);
		verifyConfiguration();
		assertEquals(0,reporter.collectedTimeouts.size());
	}

}
