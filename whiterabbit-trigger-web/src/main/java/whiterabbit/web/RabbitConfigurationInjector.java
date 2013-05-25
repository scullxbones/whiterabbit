package whiterabbit.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import whiterabbit.Delay;
import whiterabbit.Rabbit;
import whiterabbit.Rabbit.Builder;
import whiterabbit.impl.RabbitImpl;
import whiterabbit.util.Util;

public abstract class RabbitConfigurationInjector implements ServletContextListener, RabbitConfigurationSupplier {
	
	public static final String FILTER_KEY = RabbitConfigurationInjector.class.getName()+".filter";
	private Rabbit rabbit;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		RabbitFilter filter = locateFilterInstance(event.getServletContext());
		Builder configureRabbit = configureRabbit();
		rabbit = configureRabbit.buildAndStart();
		filter.setRabbit(rabbit);
		filter.setDefaultDelay(configureDefaultTimeout());
		filter.setDelayMapping(Util.valueOrDefault(configureDelayMap(),new HashMap<Pattern,Delay>()));
		filter.setIgnoredUriPatterns(Util.valueOrDefault(configureIgnoredUris(),new HashSet<Pattern>()));
	}

	private Rabbit.Builder configureRabbit() {
		Rabbit.Builder builder = RabbitImpl.builder();
		return configureRabbit(builder);
	}

	private RabbitFilter locateFilterInstance(ServletContext servletContext) {
		String filterKey = (String) servletContext.getAttribute(FILTER_KEY);
		if (filterKey == null)
			throw new RuntimeException("Filter key "+filterKey+" must be set in order for the system to properly initialize");
		return (RabbitFilter) servletContext.getAttribute(filterKey);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (rabbit != null)
			rabbit.stop();
	}

}
