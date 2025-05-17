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

import static software.xdev.maven.music.utils.SpotifyUtils.isSpotifyInstalled;
import static software.xdev.maven.music.utils.SpotifyUtils.isSpotifyRunning;
import static software.xdev.maven.music.utils.SpotifyUtils.validateAndConvertSpotifyUri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import software.xdev.maven.music.utils.SpotifyUtils;


public class MusicSource
{
	private String uri; // Generic URI for direct streaming
	private String classpath;
	private String file;
	
	/**
	 * Optional: Overwrites the default volume in dB for non-Spotify sources.
	 */
	private Float volumeDB;
	
	/**
	 * Spotify URI, accepting either 'spotify:<type>:<id>' format or
	 * 'https://open.spotify.com/<type>/<id>' URL.
	 * Examples:
	 * - spotify:track:4cOdK2wGLETKBW3PvgPWqT
	 * - https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M
	 */
	@Parameter
	private String spotifyUri;
	
	/**
	 * Whether to shuffle the Spotify content (if it's a playlist/album).
	 * Note: This is a hint. Actual shuffle behavior depends on Spotify client's settings
	 * as direct shuffle control via simple OS commands is limited.
	 */
	@Parameter(defaultValue = "false")
	private boolean spotifyShuffle;
	
	// Internal field for the resolved 'spotify:type:id' URI
	private String resolvedSpotifyUri;
	
	public String getUri()
	{
		return this.uri;
	}
	
	public void setUri(final String uri)
	{
		this.uri = uri;
	}
	
	public String getClasspath()
	{
		return this.classpath;
	}
	
	public void setClasspath(final String classpath)
	{
		this.classpath = classpath;
	}
	
	public String getFile()
	{
		return this.file;
	}
	
	public void setFile(final String file)
	{
		this.file = file;
	}
	
	public Float getVolumeDB()
	{
		return this.volumeDB;
	}
	
	public void setVolumeDB(final Float volumeDB)
	{
		this.volumeDB = volumeDB;
	}
	
	public String getSpotifyUri()
	{
		return this.spotifyUri;
	}
	
	public void setSpotifyUri(final String spotifyUri)
	{
		this.spotifyUri = spotifyUri;
	}
	
	public boolean isSpotifyShuffle()
	{
		return this.spotifyShuffle;
	}
	
	public void setSpotifyShuffle(final boolean spotifyShuffle)
	{
		this.spotifyShuffle = spotifyShuffle;
	}
	
	/**
	 * @return The resolved Spotify URI in 'spotify:type:id' format, or null if not a Spotify source or not
	 * initialized.
	 */
	public String getResolvedSpotifyUri()
	{
		return this.resolvedSpotifyUri;
	}
	
	/**
	 * Initializes Spotify settings:
	 * - Validates and parses the {@link #spotifyUri}.
	 * - Converts HTTPS Spotify URLs to the 'spotify:type:id' scheme.
	 * - Stores the result in an internal field for playback.
	 * - Checks if Spotify application is installed and running.
	 *
	 * @throws MojoExecutionException if Spotify configuration is invalid, or Spotify application is not
	 * installed/running.
	 */
	public void initializeSpotify() throws MojoExecutionException
	{
		if(this.spotifyUri == null || this.spotifyUri.trim().isEmpty())
		{
			this.resolvedSpotifyUri = null; // Not configured as a Spotify source
			return;
		}
		
		this.resolvedSpotifyUri = validateAndConvertSpotifyUri(this.spotifyUri);
		
		// If we have a resolved Spotify URI, check environment
		if(!isSpotifyInstalled())
		{
			throw new MojoExecutionException(
				"Spotify is not installed on this system. Please install Spotify to use this feature.");
		}
		
		if(!isSpotifyRunning())
		{
			throw new MojoExecutionException("Spotify is not running. Please start Spotify and try again.");
		}
	}
	
	/**
	 * Plays the resolved Spotify URI using OS-specific commands.
	 * {@link #initializeSpotify()} must be called successfully before this method.
	 *
	 * @throws MojoExecutionException if playback fails or Spotify URI is not resolved.
	 */
	public void playSpotify() throws MojoExecutionException
	{
		if(this.resolvedSpotifyUri == null || this.resolvedSpotifyUri.trim().isEmpty())
		{
			throw new MojoExecutionException(
				"No valid Spotify URI has been resolved. Call initializeSpotify() first or check configuration.");
		}
		
		SpotifyUtils.playSpotifyUri(this.resolvedSpotifyUri);
	}
	
	/**
	 * Opens an InputStream for non-Spotify sources (classpath, file, or generic URI).
	 *
	 * @return An InputStream for the configured source.
	 * @throws UncheckedIOException if the source cannot be opened.
	 * @throws IllegalStateException if no valid non-Spotify source is configured.
	 * @throws IllegalArgumentException if a generic URI is malformed.
	 */
	public InputStream openInputStream()
	{
		if(this.getClasspath() != null)
		{
			final String path = this.getClasspath().startsWith("/") ? this.getClasspath() : "/" + this.getClasspath();
			final InputStream stream = this.getClass().getResourceAsStream(path);
			if(stream == null)
			{
				throw new UncheckedIOException(new FileNotFoundException("Classpath resource not found: " + path));
			}
			return stream;
		}
		
		if(this.getFile() != null)
		{
			final File f = new File(this.getFile());
			try
			{
				return new FileInputStream(f);
			}
			catch(final FileNotFoundException e)
			{
				throw new UncheckedIOException("Failed to find file: " + f.getAbsolutePath(), e);
			}
		}
		
		if(this.getUri() != null)
		{ // This is for generic, directly streamable URIs, not Spotify HTTP URLs
			try
			{
				final URL url = new URI(this.getUri()).toURL(); // Validate URI syntax first
				return url.openStream();
			}
			catch(final MalformedURLException e) // From toURL()
			{
				throw new IllegalArgumentException("Malformed URI for streaming: " + this.getUri(), e);
			}
			catch(final URISyntaxException e) // From new URI()
			{
				throw new IllegalArgumentException("Invalid URI syntax for streaming: " + this.getUri(), e);
			}
			catch(final IOException e) // From openStream()
			{
				throw new UncheckedIOException("Failed to open stream from generic URI: " + this.getUri(), e);
			}
		}
		throw new IllegalStateException("No valid music source (classpath, file, or generic URI) configured for openInputStream.");
	}
	
	public static MusicSource fromClassPath(final String classpath)
	{
		final MusicSource source = new MusicSource();
		source.setClasspath(classpath);
		return source;
	}
	
	@Override
	public String toString()
	{
		if(this.resolvedSpotifyUri != null && !this.resolvedSpotifyUri.isEmpty())
		{
			return "Spotify: " + this.resolvedSpotifyUri + (this.spotifyShuffle ? " (Shuffle Hinted)" : "");
		}
		if(this.spotifyUri != null && !this.spotifyUri.isEmpty()) {
			// If initializeSpotify() hasn't been called or failed before resolving
			return "Spotify (unprocessed input): " + this.spotifyUri;
		}
		if(this.getClasspath() != null)
		{
			return "[classpath]:" + this.getClasspath();
		}
		if(this.getFile() != null)
		{
			return "[file]:" + this.getFile();
		}
		if(this.getUri() != null)
		{ // Generic URI
			return "[uri]:" + this.getUri();
		}
		return "MusicSource (unconfigured)";
	}
}