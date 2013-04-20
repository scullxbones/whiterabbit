package whiterabbit.reporters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.yammer.metrics.health.HealthCheck;
import com.yammer.metrics.health.HealthCheckRegistry;

import whiterabbit.ReportContext;
import whiterabbit.Reporter;

public class HealthCheckReporter extends HealthCheck implements Reporter {
	
	private BigDecimal healthyPercentageThreshold = BigDecimal.valueOf(95);
	
	private AtomicLong timeouts = new AtomicLong(0);
	private AtomicLong cancellations = new AtomicLong(0);

	public HealthCheckReporter() {
		this(new HealthCheckRegistry());
	}
	
	public HealthCheckReporter(HealthCheckRegistry registry) {
		registry.register(getClass().getName(), this);
	}
	
	public void setHealthyPercentage(BigDecimal percentage) {
		this.healthyPercentageThreshold = percentage;
	}

	@Override
	public void configure(Map<String, String> parameters) {
		if (parameters.containsKey("HEALTHY_PERCENTAGE"))
			setHealthyPercentage(new BigDecimal(parameters.get("HEALTHY_PERCENTAGE")));
	}

	@Override
	public void reportTimeout(ReportContext context) {
		timeouts.incrementAndGet();
	}

	@Override
	public void reportCancellation(ReportContext context) {
		cancellations.incrementAndGet();
	}

	@Override
	protected Result check() throws Exception {
		BigDecimal ratio = getRatio();
		if (ratio.compareTo(healthyPercentageThreshold) >= 0) {
			return Result.healthy();
		}
		return Result.unhealthy(
				String.format("Ratio of timeouts/cancellations %3.2f is below the healthy threshold of %3.2f",
						ratio,healthyPercentageThreshold)
				);
	}

	private BigDecimal getRatio() {
		long total = timeouts.get() + cancellations.get();
		return new BigDecimal(timeouts.get())
				.divide(new BigDecimal(total),RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(100));
	}

}
