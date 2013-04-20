package whiterabbit.guice;

import whiterabbit.Delay;
import whiterabbit.Rabbit;
import whiterabbit.RabbitTimeout;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class RabbitModule extends AbstractModule {

	private final Rabbit rabbit;
	private final Delay defaultDelay;

	public RabbitModule(Rabbit rabbit, Delay defaultDelay)
	{
		this.defaultDelay = defaultDelay;
		if (rabbit == null)
			throw new IllegalArgumentException("Rabbit instance cannot be null");
		this.rabbit = rabbit;
	}
	
  protected void configure() {
  	RabbitInterceptor interceptor = new RabbitInterceptor(rabbit,defaultDelay);
  	requestInjection(interceptor);
    bindInterceptor(Matchers.any(), Matchers.annotatedWith(RabbitTimeout.class), interceptor);   
  } 	
}