package whiterabbit.web;

import javax.servlet.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import whiterabbit.*;

public class RabbitFilter implements Filter {

	public static final String TIMEOUT_INIT_PARAMETER = "rabbit.filter.timeout.seconds";

	private Rabbit rabbit;
	private long timeout;
	
	public void init(FilterConfig filterConfig) 
	{
		rabbit = Rabbit.builder().buildAndStart();
		String to = filterConfig.getInitParameter(TIMEOUT_INIT_PARAMETER);
		timeout = Long.parseLong(to);
	}
 
 	public void destroy()
 	{

 	}

 	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
 	{
 		rabbit.registerTimeout(timeout,TimeUnit.SECONDS);
 		chain.doFilter(request,response);
 	}
 

}