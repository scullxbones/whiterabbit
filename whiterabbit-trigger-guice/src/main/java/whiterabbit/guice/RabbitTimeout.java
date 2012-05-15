package whiterabbit.guice;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
public @interface RabbitTimeout {
	
	long value() default 0; 
	TimeUnit unit() default TimeUnit.MILLISECONDS;
}