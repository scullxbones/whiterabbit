package whiterabbit.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import whiterabbit.Cancelable;
import whiterabbit.Delay;
import whiterabbit.Rabbit;
import whiterabbit.Reporter;
import whiterabbit.impl.RabbitImpl;

public class RabbitFilter implements Filter {

	public static final String TICK_LENGTH_PARAMETER = "rabbit.tick.length";
	public static final String TICK_UNIT_PARAMETER = "rabbit.tick.unit";
	public static final String WHEEL_SIZE_PARAMETER = "rabbit.wheel.size";
	public static final String TIMEOUT_INIT_PARAMETER = "rabbit.filter.timeout";
	public static final String TIMEUNIT_INIT_PARAMETER = "rabbit.filter.timeunit";
	public static final String REPORTER_TYPE_PARAMETER = "rabbit.reporter.class";
	public static final String REPORTER_CONFIG_PARAMETER = "rabbit.reporter.configuration";
	
	private static final long DEFAULT_TICK_LENGTH = 100;
	private static final TimeUnit DEFAULT_TICK_UNIT = TimeUnit.MILLISECONDS;
	private static final int DEFAULT_WHEEL_SIZE = 512;
			
	private static final ObjectMapper mapper = new ObjectMapper();

	private Rabbit rabbit;
	private long timeout;
	private TimeUnit unit = TimeUnit.MILLISECONDS;
	
	public void init(FilterConfig filterConfig) 
	{
		Map<String,String[]> reporterConfigs = new HashMap<String,String[]>();
		@SuppressWarnings("unchecked")
		List<String> names = Collections.list(filterConfig.getInitParameterNames());
		for(String name : names)
		{
			if(name.startsWith(REPORTER_TYPE_PARAMETER))
			{
				String instance = name.replaceFirst(REPORTER_TYPE_PARAMETER.replace(".", "\\."), "");
				String[] tuple = reporterConfigs.get(instance);
				if (tuple == null)
					tuple = new String[2];
				tuple[0] = filterConfig.getInitParameter(name);
				reporterConfigs.put(instance, tuple);
			}
			else if(name.startsWith(REPORTER_CONFIG_PARAMETER))
			{
				String instance = name.replaceFirst(REPORTER_CONFIG_PARAMETER.replace(".", "\\."), "");
				String[] tuple = reporterConfigs.get(instance);
				if (tuple == null)
					tuple = new String[2];
				tuple[1] = filterConfig.getInitParameter(name);
				reporterConfigs.put(instance, tuple);
			}
		}
		
		List<Reporter> reporters = new ArrayList<Reporter>();
		for(String[] config : reporterConfigs.values())
		{
			if (config[0] == null || config[0].trim().length() == 0)
				throw new IllegalStateException(REPORTER_CONFIG_PARAMETER+" cannot be used without a corresponding "+REPORTER_TYPE_PARAMETER);
			
			try {
				Class<?> clazz = Class.forName(config[0]);
				Reporter instance = (Reporter) clazz.newInstance();
				if (config[1] != null)
				{
					@SuppressWarnings("unchecked")
					Map<String,String> fromJson = mapper.readValue(config[1],Map.class);
					instance.configure(fromJson);
				}
				reporters.add(instance);
			} 
			catch (JsonParseException e) {
				throw new IllegalArgumentException("Invalid JSON configuration "+config[1],e);
			} 
			catch (JsonMappingException e) {
				throw new IllegalArgumentException("Could not map JSON configuration "+config[1],e);
			} 
			catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("Could not find class named "+config[0],e);
			} 
			catch (InstantiationException e) {
				throw new IllegalArgumentException("Could not instantiate object of type "+config[0],e);
			} 
			catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Incorrect access level for constructor of type "+config[0],e);
			} 
			catch (IOException e) {
				throw new IllegalArgumentException("Unable to deserialize configuration "+config[1],e);
			}
		}

		long tickLength = DEFAULT_TICK_LENGTH;
		String tickLengthParam = filterConfig.getInitParameter(TICK_LENGTH_PARAMETER);
		if (tickLengthParam != null)
			tickLength = Long.parseLong(tickLengthParam);
		
		TimeUnit tickUnit = DEFAULT_TICK_UNIT;
		String tickUnitParam = filterConfig.getInitParameter(TICK_UNIT_PARAMETER);
		if (tickUnitParam != null)
			tickUnit = TimeUnit.valueOf(tickUnitParam);
		
		int wheelSize = DEFAULT_WHEEL_SIZE;
		String wheelSizeParam = filterConfig.getInitParameter(WHEEL_SIZE_PARAMETER);
		if (wheelSizeParam != null)
			wheelSize = Integer.parseInt(wheelSizeParam);
			
		rabbit = RabbitImpl.builder()
								.size(wheelSize)
								.tick(tickLength)
								.unit(tickUnit)
								.reportingTo(reporters)
								.buildAndStart();
		
		String to = filterConfig.getInitParameter(TIMEOUT_INIT_PARAMETER);
		if (to == null)
			throw new IllegalStateException(TIMEOUT_INIT_PARAMETER+" must be specified as an init-param");
		timeout = Long.parseLong(to);
		
		String unit = filterConfig.getInitParameter(TIMEUNIT_INIT_PARAMETER);
		if (unit != null)
			this.unit = TimeUnit.valueOf(unit.toUpperCase());
	}
 
 	public void destroy()
 	{

 	}

 	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
 	{
 		Cancelable.Builder builder = rabbit.register().timeout(Delay.of(timeout,unit));
 		
 		if (request instanceof HttpServletRequest) {
 			HttpServletRequest rq = (HttpServletRequest)request;
 			builder.named(rq.getRequestURI());
 		}
 				
 		Cancelable cancelable =	builder.build();
 		try {
	 		chain.doFilter(request,response);
 		}
 		finally {
 			cancelable.cancel();
 		}
 	}
 

}