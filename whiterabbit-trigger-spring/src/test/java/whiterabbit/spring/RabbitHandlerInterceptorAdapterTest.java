package whiterabbit.spring;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import whiterabbit.Cancelable;
import whiterabbit.Delay;
import whiterabbit.Rabbit;

public class RabbitHandlerInterceptorAdapterTest {

	Rabbit rabbit = mock(Rabbit.class);
	Cancelable cancelable = mock(Cancelable.class);
	Cancelable.Builder builder = mock(Cancelable.Builder.class);
	HttpServletRequest request = mock(HttpServletRequest.class);
	HttpServletResponse response = mock(HttpServletResponse.class);
	
	Delay delay = Delay.millis(150);
	RabbitHandlerInterceptorAdapter underTest = new RabbitHandlerInterceptorAdapter();
	
	@Before
	public void setUp() throws Exception {
		Field delay = underTest.getClass().getDeclaredField("delay");
		delay.setAccessible(true);
		delay.set(underTest, this.delay);
		
		Field rabbit = underTest.getClass().getDeclaredField("rabbit");
		rabbit.setAccessible(true);
		rabbit.set(underTest, this.rabbit);
		
		when(this.rabbit.register()).thenReturn(builder);
		
		when(builder.build()).thenReturn(cancelable);
		when(builder.timeout(any(Delay.class))).thenReturn(builder);
		when(builder.named(anyString())).thenReturn(builder);
		
		when(request.getRequestURI()).thenReturn("/testcase");
		when(request.getAttribute(RabbitHandlerInterceptorAdapter.class.getName()+".cancelable")).thenReturn(cancelable);
	}
	
	@Test
	public void correctlyChecksTimeoutsBeforeAndAfter() throws Exception {
		assertThat(underTest.preHandle(request, response, new Object())).isEqualTo(true);
		underTest.afterCompletion(request, response, new Object(), null);
		
		verify(builder).timeout(eq(delay));
		verify(builder).named("/testcase");
		verify(cancelable).cancel();
	}

}
