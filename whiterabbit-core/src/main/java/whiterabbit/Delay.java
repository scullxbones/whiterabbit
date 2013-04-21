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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (duration ^ (duration >>> 32));
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Delay other = (Delay) obj;
		if (duration != other.duration)
			return false;
		if (unit != other.unit)
			return false;
		return true;
	}
}
