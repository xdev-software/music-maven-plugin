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

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


// https://stackoverflow.com/a/17737483
@SuppressWarnings("checkstyle:MagicNumber")
public class MP3OggPlayer
{
	private final ReentrantLock playingLock = new ReentrantLock();
	private boolean externalStop;
	
	/**
	 * @return <code>true</code> if the music was stopped externally
	 */
	public boolean play(final InputStream is, final float volumeDb)
	{
		this.playingLock.lock();
		try(final AudioInputStream in = getAudioInputStream(is))
		{
			final AudioFormat outFormat = this.getOutFormat(in.getFormat());
			final Info info = new Info(SourceDataLine.class, outFormat);
			
			try(final SourceDataLine line =
				(SourceDataLine)AudioSystem.getLine(info))
			{
				if(line != null)
				{
					line.open(outFormat);
					line.start();
					// https://stackoverflow.com/a/953752
					if(line.getControl(FloatControl.Type.MASTER_GAIN) instanceof final FloatControl floatControl)
					{
						floatControl.setValue(Math.max(
							Math.min(floatControl.getMaximum(), volumeDb),
							floatControl.getMinimum()));
					}
					this.stream(getAudioInputStream(outFormat, in), line);
					line.drain();
					line.stop();
				}
			}
			return this.externalStop;
		}
		catch(final UnsupportedAudioFileException
					| LineUnavailableException
					| IOException e)
		{
			throw new IllegalStateException(e);
		}
		finally
		{
			this.externalStop = false;
			this.playingLock.unlock();
		}
	}
	
	private AudioFormat getOutFormat(final AudioFormat inFormat)
	{
		final int ch = inFormat.getChannels();
		
		final float rate = inFormat.getSampleRate();
		return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
	}
	
	private void stream(final AudioInputStream in, final SourceDataLine line)
		throws IOException
	{
		final byte[] buffer = new byte[4096];
		for(int n = 0; n != -1 && !this.externalStop; n = in.read(buffer, 0, buffer.length))
		{
			line.write(buffer, 0, n);
		}
	}
	
	public void stopAndWaitUntilFinished()
	{
		this.stop();
		this.waitUntilPlayingFinished();
	}
	
	protected void stop()
	{
		this.externalStop = true;
	}
	
	protected void waitUntilPlayingFinished()
	{
		if(this.playingLock.isLocked())
		{
			this.playingLock.lock();
			this.playingLock.unlock();
		}
	}
}
