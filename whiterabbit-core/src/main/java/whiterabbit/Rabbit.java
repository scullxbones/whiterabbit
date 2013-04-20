package whiterabbit;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public interface Rabbit {
	
	public interface Builder {
		Builder tick(long tick);
		Builder unit(TimeUnit unit);
		Builder size(int wheelSize);
		Builder threadFactory(ThreadFactory factory);
		Builder reportingTo(Reporter... reporters);
		Builder reportingTo(Iterable<Reporter> reporters);
		Rabbit buildAndStart();
	}
	
	public Rabbit stop();
	
	public Cancelable.Builder register();
	
	public <T> WrappedBuilder<T> wrap(Callable<T> callable);
	
	public interface WrappedBuilder<T> {
		WrappedBuilder<T> timeout(Delay delay);
		WrappedBuilder<T> named(String name);
		WrappedBuilder<T> extraInfo(Map<String,Object> context);
		Callable<T> build();
	}
}
