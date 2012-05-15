package whiterabbit.guice;

import java.util.concurrent.TimeUnit;

import whiterabbit.Rabbit;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class RabbitModule extends AbstractModule {

	private final long defaultTimeout;
	private final TimeUnit defaultUnit;
	private final Rabbit rabbit;

	public RabbitModule(Rabbit rabbit, long defaultTimeout, TimeUnit defaultUnit)
	{
		if (rabbit == null)
			throw new IllegalArgumentException("Rabbit instance cannot be null");
		this.rabbit = rabbit;
		this.defaultTimeout = defaultTimeout;
		this.defaultUnit = defaultUnit;
	}
	
  protected void configure() {
  	RabbitInterceptor interceptor = new RabbitInterceptor(rabbit,defaultTimeout,defaultUnit);
  	requestInjection(interceptor);
    bindInterceptor(Matchers.any(), Matchers.annotatedWith(RabbitTimeout.class), interceptor);   
  } 	
}