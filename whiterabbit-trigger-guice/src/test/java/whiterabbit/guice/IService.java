package whiterabbit.guice;

public interface IService {

	@RabbitTimeout(100)
	public abstract void invoke() throws InterruptedException;

	public abstract void setMillisSleep(long millisSleep);

}