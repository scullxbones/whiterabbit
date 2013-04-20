package whiterabbit.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import whiterabbit.Rabbit;
import whiterabbit.Reporter;
import whiterabbit.Rabbit.Builder;
import whiterabbit.io.netty.util.HashedWheelTimer;

class RabbitImplBuilder implements Builder {
	private static final long DEFAULT_TICK_DURATION = 100;
	private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
	private static final int DEFAULT_WHEEL_SIZE = 512;
	
	private long tickDuration = DEFAULT_TICK_DURATION;
	private TimeUnit unit = DEFAULT_TIME_UNIT;
	private int wheelSize = DEFAULT_WHEEL_SIZE;
	private ThreadFactory factory = Executors.defaultThreadFactory();
	private Set<Reporter> reporters = new HashSet<Reporter>();
	
	RabbitImplBuilder() {}

	@Override
	public Builder withTickLength(long tick) {
		this.tickDuration = tick;
		return this;
	}

	@Override
	public Builder ofUnit(TimeUnit unit) {
		this.unit = unit;
		return this;
	}

	@Override
	public Builder withSize(int wheelSize) {
		this.wheelSize = wheelSize;
		return this;
	}

	@Override
	public Builder usingThreadFactory(ThreadFactory factory) {
		this.factory = factory;
		return this;
	}
	
	@Override
	public Builder reportingTo(Reporter reporter) {
		reporters.add(reporter);
		return this;
	}
	
	@Override
	public Builder reportingToSeveral(Reporter... reporters) {
		for(Reporter reporter : reporters)
			this.reporters.add(reporter);
		return this;
	}
	
	@Override
	public Builder reportingToIterable(Iterable<Reporter> reporters) {
		for(Reporter reporter : reporters)
			this.reporters.add(reporter);
		return this;
	}
	
	@Override
	public Rabbit buildAndStart() {
		RabbitImpl instance = 
				new RabbitImpl(
						new HashedWheelTimer(factory,tickDuration,unit,wheelSize), reporters)
							.start();
		return instance;
	}
	
}