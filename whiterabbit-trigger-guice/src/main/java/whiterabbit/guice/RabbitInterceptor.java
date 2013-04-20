package whiterabbit.guice;

import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import whiterabbit.Cancelable;
import whiterabbit.Delay;
import whiterabbit.Rabbit;
import whiterabbit.RabbitTimeout;

public class RabbitInterceptor implements MethodInterceptor {
	
	private final Rabbit rabbit;
	private Delay defaultDelay;
	
	public RabbitInterceptor(Rabbit rabbit, Delay defaultDelay)
	{
		this.rabbit = rabbit;
		this.defaultDelay = defaultDelay;
	}
	
	public Object invoke(MethodInvocation invocation) throws Throwable {
		long to = defaultDelay.duration();
		TimeUnit unit = defaultDelay.unit();
		if (invocation.getMethod().isAnnotationPresent(RabbitTimeout.class)) {
			RabbitTimeout toAnnotation = invocation.getMethod().getAnnotation(RabbitTimeout.class);
			to = toAnnotation.value() > 0 ? toAnnotation.value() : defaultDelay.duration();
			unit = toAnnotation.unit();
		}
		Cancelable cancelable = rabbit.register()
									  .timeout(Delay.of(to, unit))
									  .named(byInvocation(invocation))
									  .build();
		try
		{
			return invocation.proceed();
		}
		finally
		{
			cancelable.cancel();
		}
	}

	private String byInvocation(MethodInvocation invocation) {
		return String.format("%s.%s", invocation.getThis().getClass().getName(),invocation.getMethod().getName());
	} 
}