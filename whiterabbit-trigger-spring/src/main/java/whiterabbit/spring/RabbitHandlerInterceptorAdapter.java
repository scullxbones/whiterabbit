package whiterabbit.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import whiterabbit.Cancelable;
import whiterabbit.Delay;
import whiterabbit.Rabbit;

public class RabbitHandlerInterceptorAdapter extends HandlerInterceptorAdapter {
	
	private static final String CANCELABLE_TAG = RabbitHandlerInterceptorAdapter.class.getName()+".cancelable";

	@Autowired
	private Rabbit rabbit;
	
	@Autowired
	private Delay delay;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		Cancelable cancelable = rabbit.register().timeout(delay).named(request.getRequestURI()).build();
		request.setAttribute(CANCELABLE_TAG, cancelable);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) throws Exception {
		Cancelable cancelable = (Cancelable) request.getAttribute(CANCELABLE_TAG);
		if (cancelable != null)
			cancelable.cancel();
	}

}
