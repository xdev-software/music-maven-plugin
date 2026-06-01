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
package software.xdev.maven.music.sources.spotify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import software.xdev.maven.music.sources.MusicSource;


/**
 * Uses spotify as a music source.
 * <p>
 * Known limitations/quirks:
 * <ul>
 *     <li>Only starting to play a song/playlist is possible</li>
 *     <li>No information if the song/playlist finished can be received</li>
 *     <li>Can't be stopped</li>
 * </ul>
 * </p>
 */
public class SpotifyMusicSource implements MusicSource
{
	private static final Pattern SPOTIFY_URI_PATTERN =
		Pattern.compile("^spotify:(track|playlist|album|artist|episode|show):([a-zA-Z0-9]+)$");
	private static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile(
		"^https:\\/\\/open\\.spotify\\.com\\/(track|playlist|album|artist|episode|show)\\/([a-zA-Z0-9]+)(?:\\?.*)?$");
	
	/**
	 * Spotify URI, accepting either
	 * <ul>
	 *     <li>{@code spotify:<type>:<id>} format or</li>
	 *     <li>{@code https://open.spotify.com/<type>/<id>} url</li>
	 * </ul>
	 * <p>
	 * Examples:
	 * <ul>
	 *     <li>{@code spotify:track:4cOdK2wGLETKBW3PvgPWqT}</li>
	 *     <li>{@code https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M}</li>
	 * </ul>
	 */
	private String uri;
	private String resolvedUri;
	
	public String getUri()
	{
		return this.uri;
	}
	
	public void setUri(final String uri)
	{
		this.uri = uri;
		this.resolvedUri = resolveUri(uri);
	}
	
	protected static String resolveUri(final String input)
	{
		final String trimmed = input.trim();
		
		if(SPOTIFY_URI_PATTERN.matcher(trimmed).matches())
		{
			return trimmed;
		}
		
		final Matcher urlMatcher = SPOTIFY_URL_PATTERN.matcher(trimmed);
		if(urlMatcher.matches())
		{
			return "spotify:" + urlMatcher.group(1) + ":" + urlMatcher.group(2);
		}
		
		throw new IllegalArgumentException("Invalid Spotify URI or URL format: " + input
			+ "\n"
			+ "Please use a 'spotify:<type>:<id>' URI (e.g., spotify:track:xxxx) "
			+ "or 'https://open.spotify.com/<type>/<id>' URL (e.g., https://open.spotify.com/track/xxxx)");
	}
	
	public String getResolvedUri()
	{
		return this.resolvedUri;
	}
	
	public void setResolvedUri(final String resolvedUri)
	{
		this.resolvedUri = resolvedUri;
	}
	
	@Override
	public String toString()
	{
		return "[spotify]: uri=" + this.uri + ", resolvedUri=" + this.resolvedUri;
	}
}
