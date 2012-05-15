package whiterabbit.guice;

import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import whiterabbit.Rabbit;
import whiterabbit.Rabbit.Cancelable;

public class RabbitInterceptor implements MethodInterceptor {
	
	private final long defaultTimeout;
	private final Rabbit rabbit;
	private final TimeUnit defaultTimeUnit;
	
	public RabbitInterceptor(Rabbit rabbit, long defaultTimeout, TimeUnit defaultTimeUnit)
	{
		this.rabbit = rabbit;
		this.defaultTimeUnit = defaultTimeUnit;
		this.defaultTimeout = defaultTimeout;
	}
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		long to = defaultTimeout;
		TimeUnit unit = defaultTimeUnit;
		if (invocation.getMethod().isAnnotationPresent(RabbitTimeout.class)) {
			RabbitTimeout toAnnotation = invocation.getMethod().getAnnotation(RabbitTimeout.class);
			to = toAnnotation.value() > 0 ? toAnnotation.value() : defaultTimeout;
			unit = toAnnotation.unit();
		}
		Cancelable cancelable = rabbit.registerTimeout(to, unit);
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