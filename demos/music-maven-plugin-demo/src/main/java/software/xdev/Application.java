package software.xdev;

public final class Application
{
	public static void main(final String[] args) throws InterruptedException
	{
		if(args.length > 0)
		{
			Thread.sleep(Integer.parseInt(args[0]));
		}
	}
	
	private Application()
	{
	}
}
