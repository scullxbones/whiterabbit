package whiterabbit;

import java.util.concurrent.TimeUnit;

public class Delay {

	private final long duration;
	private final TimeUnit unit;
	
	private Delay(long duration, TimeUnit unit) {
		this.duration = duration;
		this.unit = unit;
	}
	
	public long duration() {
		return duration;
	}
	
	public TimeUnit unit() {
		return unit;
	}
	
	public long inMillis() {
		return unit.toMillis(duration);
	}
	
	public static Delay of(long duration, TimeUnit unit) {
		return new Delay(duration,unit);
	}

	public static Delay minutes(long minutes) {
		return new Delay(minutes, TimeUnit.MINUTES);
	}
	
	public static Delay seconds(long seconds) {
		return new Delay(seconds, TimeUnit.SECONDS);
	}

	public static Delay millis(long millis) {
		return new Delay(millis, TimeUnit.MILLISECONDS);
	}
}
