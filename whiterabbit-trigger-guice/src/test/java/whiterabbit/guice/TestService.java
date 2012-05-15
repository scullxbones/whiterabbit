package whiterabbit.guice;


public class TestService implements IService {

	private long millisSleep = 200;
	
	@Override
	public void setMillisSleep(long millisSleep)
	{
		this.millisSleep = millisSleep;
	}
	
	/* (non-Javadoc)
	 * @see whiterabbit.guice.IService#invoke()
	 */
	@Override
	@RabbitTimeout(100)
	public void invoke() throws InterruptedException
	{
		Thread.sleep(millisSleep);
	}
	
}
