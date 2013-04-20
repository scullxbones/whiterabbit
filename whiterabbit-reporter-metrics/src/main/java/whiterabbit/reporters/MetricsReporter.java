package whiterabbit.reporters;

import java.util.Map;

import whiterabbit.ReportContext;
import whiterabbit.Reporter;
import whiterabbit.reporters.TimeoutRatio.Rate;

import com.yammer.metrics.Meter;
import com.yammer.metrics.MetricRegistry;

public class MetricsReporter implements Reporter {

	private final Meter timeoutMeter;
	private final Meter cancellationMeter;
	
	public MetricsReporter(MetricRegistry registry) {
		timeoutMeter = registry.meter(MetricRegistry.name(getClass(),"timeouts"));
		cancellationMeter = registry.meter(MetricRegistry.name(getClass(),"cancellations"));
		registry.register(MetricRegistry.name(getClass(),"1-minute","timeout","ratio"), 
				new TimeoutRatio(timeoutMeter,cancellationMeter,Rate.OneMinute));
		registry.register(MetricRegistry.name(getClass(),"5-minute","timeout","ratio"), 
				new TimeoutRatio(timeoutMeter,cancellationMeter,Rate.FiveMinute));
		registry.register(MetricRegistry.name(getClass(),"15-minute","timeout","ratio"), 
				new TimeoutRatio(timeoutMeter,cancellationMeter,Rate.FifteenMinute));
		registry.register(MetricRegistry.name(getClass(),"mean","timeout","ratio"), 
				new TimeoutRatio(timeoutMeter,cancellationMeter,Rate.Mean));
	}

	@Override
	public void configure(Map<String, String> parameters) {
		// No-op
	}

	@Override
	public void reportTimeout(ReportContext context) {
		timeoutMeter.mark();
	}

	@Override
	public void reportCancellation(ReportContext context) {
		cancellationMeter.mark();
	}

}
