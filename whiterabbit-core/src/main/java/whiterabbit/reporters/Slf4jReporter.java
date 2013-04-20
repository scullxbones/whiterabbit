package whiterabbit.reporters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import whiterabbit.ReportContext;
import whiterabbit.Reporter;

public class Slf4jReporter implements Reporter {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private static final String NL = System.getProperty("line.separator");
	
	@Override
	public void configure(Map<String, String> parameters) {
		// No-op
	}

	@Override
	public void reportTimeout(ReportContext context) 
	{
		StringBuilder buffer = new StringBuilder(NL);
		buffer.append(dtFormat.format(new Date()));
		if (context.getName() != null)
			buffer.append(" - ").append(context.getName());
		buffer.append(NL);
		buffer.append("Thread :").append(context.getToDump().getName()).append(" exceeded timeout of ").append(context.getDelay()).append(" ").append(context.getUnit().toString()).append(NL);
		if (context.getContext() != null)
		{
			buffer.append(" === Context === ").append(NL);
			for(Map.Entry<String,Object> each : context.getContext().entrySet())
			{
				buffer.append(" - ").append(each.getKey()).append(" = ").append(each.getValue()).append(NL);
			}
			buffer.append(" === Context === ").append(NL);
		}
		buffer.append(" === Stack === ").append(NL);
		for(StackTraceElement el : context.getStack())
		{
			buffer.append(el.toString()).append(NL);
		}
		buffer.append(" === Stack === ").append(NL);
		logger.info(buffer.toString());
	}

	@Override
	public void reportCancellation(ReportContext context) {
		// No-op
	}

}
