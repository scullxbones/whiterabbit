package whiterabbit.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class TimeoutFilterChain implements FilterChain, Runnable {
	
	private final long timeoutMillis;
	private final Filter toExecute;
	private HttpServletRequest mockServletRequest;
	private HttpServletResponse mockServletResponse;

	public TimeoutFilterChain(Filter toExecute, long timeoutMillis, HttpServletRequest mockServletRequest, HttpServletResponse mockServletResponse)
	{
		this.toExecute = toExecute;
		this.timeoutMillis = timeoutMillis;
		this.mockServletRequest = mockServletRequest;
		this.mockServletResponse = mockServletResponse;
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