package whiterabbit.guice;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
public @interface RabbitTimeout {
	
	long value() default 0; 
	
}