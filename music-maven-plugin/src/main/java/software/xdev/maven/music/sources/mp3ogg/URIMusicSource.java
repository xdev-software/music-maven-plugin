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
package software.xdev.maven.music.sources.mp3ogg;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;


/**
 * Uses a URI as a music source.
 */
public class URIMusicSource extends MP3OggMusicSource
{
	private String uri;
	
	public String getUri()
	{
		return this.uri;
	}
	
	public void setUri(final String uri)
	{
		this.uri = uri;
	}
	
	@Override
	public InputStream openInputStream()
	{
		try
		{
			return URI.create(this.getUri()).toURL().openStream();
		}
		catch(final IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}
	
	@Override
	public String toString()
	{
		return "[uri]: " + this.getUri();
	}
}
