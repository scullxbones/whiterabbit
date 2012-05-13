package whiterabbit.reporters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import whiterabbit.Reporter;

public class Slf4jReporter implements Reporter {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private static final String NL = System.getProperty("line.separator");
	
	@Override
	public void reportTimeout(List<StackTraceElement> stack, Thread toDump, long delay, TimeUnit unit) {
		StringBuilder buffer = new StringBuilder(NL);
		buffer.append(dtFormat.format(new Date()));
		buffer.append(NL);
		buffer.append("Thread :").append(toDump.getName()).append(" exceeded timeout of ").append(delay).append(" ").append(unit.toString()).append(NL);
		for(StackTraceElement el : stack)
		{
			buffer.append(el.toString()).append(NL);
		}
		buffer.append(NL);
		logger.info(buffer.toString());
	}

}
