package whiterabbit.reporters;

import com.yammer.metrics.Meter;
import com.yammer.metrics.RatioGauge;

public final class TimeoutRatio extends RatioGauge {
	
	enum Rate {
		OneMinute,FiveMinute,FifteenMinute,Mean
	}
	
	private final Meter timeouts;
	private final Meter cancellations;
	private final Rate rate;

	public TimeoutRatio(Meter timeouts, Meter cancellations, Rate rate) {
		this.timeouts = timeouts;
		this.cancellations = cancellations;
		this.rate = rate;
	}

	@Override
	protected Ratio getRatio() {
		Ratio ratio = null;
		switch(rate) {
			case OneMinute:
				ratio = Ratio.of(timeouts.getOneMinuteRate(), timeouts.getOneMinuteRate() + cancellations.getOneMinuteRate());
				break;
			case FiveMinute:
				ratio = Ratio.of(timeouts.getFiveMinuteRate(), timeouts.getFiveMinuteRate() + cancellations.getFiveMinuteRate());
				break;
			case FifteenMinute:
				ratio = Ratio.of(timeouts.getFifteenMinuteRate(), timeouts.getFifteenMinuteRate() + cancellations.getFifteenMinuteRate());
				break;
			default:
				ratio = Ratio.of(timeouts.getMeanRate(), timeouts.getMeanRate() + cancellations.getMeanRate());
				break;
		}
		return ratio;
	}
	
}

