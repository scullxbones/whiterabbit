package whiterabbit.web;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import whiterabbit.Cancelable;
import whiterabbit.Delay;
import whiterabbit.Rabbit;
import whiterabbit.impl.RabbitImpl;
import whiterabbit.reporters.Slf4jReporter;

public class RabbitFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Rabbit rabbit;
	private Delay defaultDelay = Delay.millis(5000);
	
	private final Map<Pattern,Delay> delayMap = new HashMap<Pattern,Delay>();
	private final Set<Pattern> ignoredUris = new HashSet<Pattern>();
	
	public RabbitFilter() {
		
	}
	
	public RabbitFilter(Rabbit rabbit, Delay delay) {
		this.rabbit = rabbit;
		this.defaultDelay = delay;
	}
	
	public void setRabbit(Rabbit rabbit) {
		this.rabbit = rabbit;
	}
	
	public void setDefaultDelay(Delay delay) {
		this.defaultDelay = delay;
	}
	
	public void setIgnoredUriPatterns(Collection<Pattern> ignoredUris) {
		if (ignoredUris != null)
			this.ignoredUris.addAll(ignoredUris);
	}
	
	public void setDelayMapping(Map<Pattern,Delay> delayMap) {
		if (delayMap != null)
			this.delayMap.putAll(delayMap);
	}
	
	public void init(FilterConfig filterConfig) 
	{
		if (rabbit == null) {
			filterConfig.getServletContext().setAttribute(RabbitConfigurationInjector.FILTER_KEY,getClass().getName());
			filterConfig.getServletContext().setAttribute(getClass().getName(), this);
		}
	}
 
 	public void destroy()
 	{

 	}

 	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
 	{
 		if (rabbit == null)
 			performDefaultConfiguration();

		HttpServletRequest rq = (HttpServletRequest)request;
		String uri = rq.getRequestURI();
 		
 		if (!ignoredUris.isEmpty() && shouldIgnore(uri)) {
 			chain.doFilter(request, response);
 			return;
 		}
 		
 		Delay delay = defaultDelay;
 		if (!delayMap.isEmpty()) 
 			delay = calculateDelay(uri);
 		
 		Cancelable cancelable =	rabbit.register().timeout(delay).named(rq.getRequestURI()).build();
 		try {
	 		chain.doFilter(request,response);
 		}
 		finally {
 			cancelable.cancel();
 		}
 	}

	private Delay calculateDelay(String uri) {
		for(Pattern p:delayMap.keySet()) {
			if (p.matcher(uri).find())
				return delayMap.get(p);
		}
		return defaultDelay;
	}

	private boolean shouldIgnore(String uri) {
		for(Pattern p:ignoredUris) {
			if (p.matcher(uri).find())
				return true;
		}
		return false;
	}

	private void performDefaultConfiguration() {
		Rabbit.Builder builder = RabbitImpl.builder();
		rabbit = builder.reportingTo(new Slf4jReporter()).buildAndStart();
		defaultDelay = Delay.millis(5000);
		logger.info("Default configuration applied to RabbitFilter as custom configuration was not supplied.");
	}
}