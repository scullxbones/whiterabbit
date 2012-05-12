package whiterabbit;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface Reporter {
	void reportTimeout(List<StackTraceElement> list, Thread toDump, long delay, TimeUnit unit);
}
