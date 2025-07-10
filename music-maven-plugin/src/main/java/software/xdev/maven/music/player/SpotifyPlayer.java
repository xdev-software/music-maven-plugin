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
package software.xdev.maven.music.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Locale;

import org.apache.maven.plugin.logging.Log;

import software.xdev.maven.music.sources.spotify.SpotifyMusicSource;


public class SpotifyPlayer implements Player<SpotifyMusicSource>
{
	private static final String OS_NAME_LOWER = System.getProperty("os.name", "")
		.toLowerCase(Locale.ENGLISH);
	
	@Override
	public Class<SpotifyMusicSource> supportedMusicSourceType()
	{
		return SpotifyMusicSource.class;
	}
	
	@Override
	public boolean play(final SpotifyMusicSource source, final float defaultVolumeDB, final Log log)
	{
		final String uri = source.getResolvedUri();
		
		final ProcessBuilder pb;
		if(OS_NAME_LOWER.contains("win"))
		{
			// For 'start' command on Windows, an empty title "" is often needed if the path/URL might contain spaces
			pb = new ProcessBuilder("cmd", "/c", "start", "\"\"", uri);
		}
		else if(OS_NAME_LOWER.contains("mac"))
		{
			pb = new ProcessBuilder(
				"osascript", "-e",
				String.format("tell application \"Spotify\" to play track \"%s\"", uri));
		}
		else if(OS_NAME_LOWER.contains("nux") || OS_NAME_LOWER.contains("nix"))
		{
			pb = new ProcessBuilder(
				"dbus-send", "--print-reply", "--dest=org.mpris.MediaPlayer2.spotify",
				"/org/mpris/MediaPlayer2", "org.mpris.MediaPlayer2.Player.OpenUri", "string:" + uri);
		}
		else
		{
			return false;
		}
		
		try
		{
			final Process process = pb.start();
			final String stdOut = consumeStream(process.getInputStream());
			final String stdErr = consumeStream(process.getErrorStream());
			final int exitCode = process.waitFor();
			
			if(exitCode != 0)
			{
				String errorMessage = String.format(
					"Failed to play Spotify URI '%s'. Exit code: %d.",
					uri,
					exitCode);
				if(!stdErr.isBlank())
				{
					errorMessage += " Error: " + stdErr;
				}
				if(!stdOut.isBlank())
				{
					errorMessage += " Output: " + stdOut;
				}
				log.warn(errorMessage);
			}
		}
		catch(final IOException ioe)
		{
			throw new UncheckedIOException(ioe);
		}
		catch(final InterruptedException e)
		{
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Got interrupted", e);
		}
		
		return false;
	}
	
	@Override
	public void stop()
	{
		// Can't be stopped
	}
	
	static String consumeStream(final InputStream stream)
	{
		final StringBuilder sb = new StringBuilder();
		try(final BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
		{
			String line;
			while((line = reader.readLine()) != null)
			{
				sb.append(line).append(System.lineSeparator());
			}
		}
		catch(final IOException ioe)
		{
			throw new UncheckedIOException(ioe);
		}
		return sb.toString();
	}
}
