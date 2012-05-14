package whiterabbit.web;

import javax.servlet.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import whiterabbit.*;
import whiterabbit.Rabbit.Cancelable;

public class RabbitFilter implements Filter {

	public static final String TIMEOUT_INIT_PARAMETER = "rabbit.filter.timeout";
	public static final String TIMEUNIT_INIT_PARAMETER = "rabbit.filter.timeunit";

	private Rabbit rabbit;
	private long timeout;
	private TimeUnit unit = TimeUnit.MILLISECONDS;
	
	public void init(FilterConfig filterConfig) 
	{
		rabbit = Rabbit.builder().buildAndStart();
		String to = filterConfig.getInitParameter(TIMEOUT_INIT_PARAMETER);
		if (to == null)
			throw new IllegalStateException(TIMEOUT_INIT_PARAMETER+" must be specified as an init-param");
		String unit = filterConfig.getInitParameter(TIMEUNIT_INIT_PARAMETER);
		if (unit != null)
			this.unit = TimeUnit.valueOf(unit.toUpperCase());
		timeout = Long.parseLong(to);
	}
 
 	public void destroy()
 	{

 	}

 	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
 	{
 		Cancelable cancelable = rabbit.registerTimeout(timeout,TimeUnit.SECONDS);
 		try {
	 		chain.doFilter(request,response);
 		}
 		finally {
 			cancelable.cancel();
 		}
 	}
 

}