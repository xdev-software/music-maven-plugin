package software.xdev.maven.music.player;

import org.apache.maven.plugin.logging.Log;

import software.xdev.maven.music.sources.MusicSource;


public abstract class StoppablePlayer<S extends MusicSource> implements Player<S>
{
	protected boolean externalStop;
	
	@Override
	public boolean play(final S source, final float defaultVolumeDB, final Log log)
	{
		try
		{
			return this.playInternal(source, defaultVolumeDB, log);
		}
		finally
		{
			this.externalStop = false;
		}
	}
	
	protected abstract boolean playInternal(final S source, final float defaultVolumeDB, final Log log);
	
	@Override
	public void stop()
	{
		this.externalStop = true;
	}
}
