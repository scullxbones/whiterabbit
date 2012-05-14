package whiterabbit.guice;

import whiterabbit.Rabbit;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class RabbitModule extends AbstractModule {

	private long defaultTimeoutSecs;
	private final Rabbit rabbit;

	public RabbitModule(Rabbit rabbit, long defaultTimeoutSecs)
	{
		this.rabbit = rabbit;
		this.defaultTimeoutSecs = defaultTimeoutSecs;
	}
	
  protected void configure() {
    bindInterceptor(Matchers.any(), Matchers.annotatedWith(RabbitTimeout.class),
    			          new RabbitInterceptor(rabbit,defaultTimeoutSecs));   
  } 	
}