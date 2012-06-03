package whiterabbit.web;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import whiterabbit.ReportContext;

@RunWith(MockitoJUnitRunner.class)
public class RabbitFilterTest {

	private RabbitFilter filter;
	@Mock private FilterConfig config;
	private TimeoutFilterChain chain;
	private Map<String,String> initParams;
	
	private class TimeoutFilterChain implements FilterChain, Runnable {
		
		private final long timeoutMillis;
		private final Filter toExecute;

		public TimeoutFilterChain(Filter toExecute, long timeoutMillis)
		{
			this.toExecute = toExecute;
			this.timeoutMillis = timeoutMillis;
		}
		

		@Override
		public void doFilter(ServletRequest arg0, ServletResponse arg1) throws IOException, ServletException {
			try {
				Thread.sleep(timeoutMillis);
			} 
			catch (InterruptedException e) {
				throw new ServletException(e);
			}
		}


		@Override
		public void run() {
			ServletRequest mockServletRequest = mock(ServletRequest.class);
			ServletResponse mockServletResponse = mock(ServletResponse.class);
			try {
				toExecute.doFilter(mockServletRequest, mockServletResponse, this);
			} 
			catch (RuntimeException e) {
				throw e;
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	public Enumeration<String> createEnumeration() {
		return new Enumeration<String>() {
			
			private final Iterator<String> names = initParams.keySet().iterator();

			@Override
			public boolean hasMoreElements() {
				return names.hasNext();
			}

			@Override
			public String nextElement() {
				return names.next();
			}
			
		};
	}
	
	@Before
	public void setUp()
	{
		when(config.getInitParameterNames()).then(new Answer<Enumeration<String>>() {

			@Override
			public Enumeration<String> answer(InvocationOnMock invocation) throws Throwable {
				return createEnumeration();
			}
			
		});
		
		when(config.getInitParameter(anyString())).then(new Answer<String>(){

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return initParams.get(invocation.getArguments()[0]);
			}
			
		});
		initParams = new HashMap<String,String>();
		filter = new RabbitFilter();
	}
	
	@After
	public void tearDown()
	{
		FilterTestReporter.reset();
	}
	
	private void setupTimeout(long timeout) {
		initParams.put(RabbitFilter.TIMEOUT_INIT_PARAMETER, Long.toString(timeout));
	}
	
	private void setupReporter() {
		initParams.put(RabbitFilter.REPORTER_TYPE_PARAMETER, "whiterabbit.web.FilterTestReporter");
		initParams.put(RabbitFilter.REPORTER_CONFIG_PARAMETER, "{ \"name\" : \"value\" }");
	}
	
	private void runFilter(long configuredTimeout, long actualTimeout) {
		setupTimeout(configuredTimeout);
		setupReporter();
		filter.init(config);
		chain = new TimeoutFilterChain(filter,actualTimeout);
		chain.run();
	}
	
	private void verifyConfiguration() {
		Map<String, String> next = FilterTestReporter.collectedConfigurations.iterator().next();
		assertEquals("name",next.keySet().iterator().next());
		assertEquals("value",next.get("name"));
	}

	@Test
	public void testCorrectInit() {
		try {
			filter.init(config);
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
		ReportContext next = FilterTestReporter.collectedContexts.iterator().next();
		assertEquals(50,next.getDelay());
	}

	@Test
	public void testDoFilterWithoutTimeout() {
		runFilter(50,10);
		verifyConfiguration();
		assertEquals(0,FilterTestReporter.collectedContexts.size());
	}

}
