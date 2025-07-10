package software.xdev.maven.music.sources.spotify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import software.xdev.maven.music.sources.MusicSource;


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
