/*
 * Copyright © 2024 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.maven.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import software.xdev.maven.music.sources.WrappedMusicSource;
import software.xdev.maven.music.sources.mp3ogg.ClassPathMusicSource;


@Mojo(
	name = "music",
	defaultPhase = LifecyclePhase.VALIDATE,
	threadSafe = true
)
public class MusicMojo extends AbstractMojo
{
	protected static final AtomicInteger THREAD_COUNTER = new AtomicInteger(0);
	
	@Parameter(property = "music.skip")
	protected boolean skip;
	
	@Parameter
	protected List<WrappedMusicSource> sources = new ArrayList<>(List.of(
		new WrappedMusicSource(new ClassPathMusicSource("/default/Local_Forecast_-_Elevator.ogg"))
	));
	
	/**
	 * Randomizes the sources.
	 */
	@Parameter
	protected boolean shuffle;
	
	/**
	 * Repeat the sources?
	 */
	@Parameter
	protected boolean repeat = true;
	
	/**
	 * Plays the music in the background (non-blocking)
	 * <p>
	 * If this set to <code>false</code>, {@link #repeat} will be forcefully set to <code>false</code>
	 * </p>
	 */
	@Parameter
	protected boolean background = true;
	
	/**
	 * Volume to use in <a href="https://en.wikipedia.org/wiki/Decibel">dB</a>
	 * <p>
	 * Usually speakers have volumes between around 5 to -50 dB.
	 * </p>
	 * Example values:
	 * <ul>
	 *     <li>2 dB = louder</li>
	 *     <li>0 dB = normal volume</li>
	 *     <li>-20 dB = quieter (default)</li>
	 *     <li>-50 dB = very quiet</li>
	 * </ul>
	 */
	@Parameter
	protected float defaultVolumeDB = -20.0f;
	
	@SuppressWarnings("java:S2142")
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		if(this.skip)
		{
			this.getLog().info("Skipping");
			return;
		}
		
		if(this.sources.isEmpty())
		{
			this.getLog().info("No sources");
			return;
		}
		
		if(!this.background)
		{
			this.repeat = false;
		}
		
		final Thread thread = new Thread(
			this::runOnThread,
			"Maven-Music-Player-" + THREAD_COUNTER.getAndIncrement());
		thread.setDaemon(true);
		thread.start();
		if(!this.background)
		{
			try
			{
				thread.join();
			}
			catch(final InterruptedException e)
			{
				throw new IllegalStateException(e);
			}
		}
	}
	
	private void runOnThread()
	{
		try
		{
			final List<WrappedMusicSource> sourcesWorkingCopy = new ArrayList<>(this.sources);
			if(this.shuffle)
			{
				Collections.shuffle(sourcesWorkingCopy);
			}
			
			PlayerManager.instance().stopActivePlayer();
			
			boolean wasStopped = false;
			do
			{
				for(final WrappedMusicSource source : sourcesWorkingCopy)
				{
					if(PlayerManager.instance().play(
						source.getMusicSource(),
						this.defaultVolumeDB,
						this.getLog()))
					{
						wasStopped = true;
						break;
					}
				}
			}
			while(this.repeat && !wasStopped);
		}
		catch(final Exception ex)
		{
			this.getLog().warn("Failed to play stream", ex);
		}
	}
}
