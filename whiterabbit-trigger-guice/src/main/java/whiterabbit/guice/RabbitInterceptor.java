package whiterabbit.guice;

import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import whiterabbit.Rabbit;
import whiterabbit.Rabbit.Cancelable;

public class RabbitInterceptor implements MethodInterceptor {
	
	private long defaultTimeoutSecs;
	private final Rabbit rabbit;
	
	public RabbitInterceptor(Rabbit rabbit, long defaultTimeoutSecs)
	{
		this.rabbit = rabbit;
		this.defaultTimeoutSecs = defaultTimeoutSecs;
	}
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		long to = defaultTimeoutSecs;
		if (invocation.getMethod().isAnnotationPresent(RabbitTimeout.class))
			to = invocation.getMethod().getAnnotation(RabbitTimeout.class).value();
		Cancelable cancelable = rabbit.registerTimeout(to, TimeUnit.SECONDS);
		try
		{
			return invocation.proceed();
		}
		finally
		{
			cancelable.cancel();
		}
	} 
}