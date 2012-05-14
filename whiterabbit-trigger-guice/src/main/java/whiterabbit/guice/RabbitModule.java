package whiterabbit.guice;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class RabbitModule extends AbstractModule {
  protected void configure() {
    bindInterceptor(Matchers.any(), Matchers.annotatedWith(RabbitTimeout.class),
    			          new RabbitInterceptor());   
  } 	
}